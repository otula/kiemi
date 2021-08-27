/**
 * Copyright 2019 Tampere University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * gateway.c
 */

#include <stdlib.h>
#include <stdio.h>

#include <signal.h>
#include <time.h>

#include "loragw_hal.h"
#include "lorawan_parser.h"
#include "lorawan_database.h"
#include "gateway_config.h"
#include "gateway.h"

#define LORAWAN_MAX_PACKAGES 16

static int stop_gateway = 0; // 1 -> main loop is stopped when able
static const struct timespec sleep_time = { 0, 3000000 }; /* 3 ms */

/* forward declarations */
void sig_handler(int sigio);
static void cleanup_sensor_nodes(struct sensor_node** sensor_nodes, int node_count);
static void print_lorawan_package(const struct lgw_pkt_rx_s *p, const struct sensor_data* data);

/**
 *
 */
void sig_handler(int sigio) {
	if (sigio == SIGQUIT || sigio == SIGINT || sigio == SIGTERM) {
		fprintf(stderr, "Caught signal %d, stopping...", sigio);
		stop_gateway = 1;
	}
}

/**
 * @param sensor_nodes
 * @param node_count
 */
static void cleanup_sensor_nodes(struct sensor_node** sensor_nodes, int node_count) {
	for (int i = 0; i < node_count; ++i) {
		free(sensor_nodes[i]);
	}
	free(sensor_nodes);
}

/**
 *
 */
int run_gateway(const char* gateway_config, const char* sx1301_config) {
	/* configure signal handling */
	struct sigaction sigact;
	sigemptyset(&sigact.sa_mask);
	sigact.sa_flags = 0;
	sigact.sa_handler = sig_handler;
	sigaction(SIGQUIT, &sigact, NULL);
	sigaction(SIGINT, &sigact, NULL);
	sigaction(SIGTERM, &sigact, NULL);

	if(init_SX1301_config(sx1301_config) != 0){
		fprintf(stderr, "Failed to process configuration file %s\n.", sx1301_config);
		return EXIT_FAILURE;
	}
	if(init_gateway_config(gateway_config) != 0){
		fprintf(stderr, "Failed to process configuration file %s\n.", gateway_config);
		return EXIT_FAILURE;
	}

	if(init_lorawan_database(get_database_server_host(), get_database_server_port(), get_database_server_username(), get_database_server_password()) != LORAWAN_DATABASE_SUCCESS) {
		fprintf(stderr, "Failed to open database connection.");
		close_gateway_config();
		return EXIT_FAILURE;
	}

	int node_count = 0;
	struct sensor_node** sensor_nodes = retrive_sensor_nodes(&node_count);
	if(node_count < 1) {
		fprintf(stderr, "No sensor nodes.\n");
		close_gateway_config();
		close_lorawan_database();
		return EXIT_FAILURE;
	}
	printf("Loaded %d sensor nodes.\n", node_count);

	/* starting the concentrator */
	if (lgw_start() == LGW_HAL_SUCCESS) {
		printf("Concentrator started, packet can now be received\n");
	} else {
		fprintf(stderr, "Failed to start the concentrator\n");
		close_lorawan_database();
		close_gateway_config();
		cleanup_sensor_nodes(sensor_nodes, node_count);
		return EXIT_FAILURE;
	}

	struct lgw_pkt_rx_s rxpkt[LORAWAN_MAX_PACKAGES]; /* array containing up to LORAWAN_MAX_PACKAGES inbound packets metadata */
	int nb_pkt;
	/* main loop */
	while (stop_gateway == 0) {
		/* fetch packets */
		nb_pkt = lgw_receive(LORAWAN_MAX_PACKAGES, rxpkt);
		if (nb_pkt == LGW_HAL_ERROR) {
			fprintf(stderr, "Failed packet fetch, exiting...\n");
			return EXIT_FAILURE;
		} else if (nb_pkt == 0) {
			clock_nanosleep(CLOCK_MONOTONIC, 0, &sleep_time, NULL); /* wait a short time if no packets */
			continue;
		}

		long long timestamp = (long long) time(NULL)*1000; // convert to milliseconds
		/* log packets */
		for (int i=0;i<nb_pkt;++i) {
			struct lgw_pkt_rx_s *p = &rxpkt[i];

			char spaced_data[p->size * 3 + 1]; // we don't really know how large this should be, make room for extra 0s and spaces
			int j = 0;
			for (int k=0;k<p->size;++k) {
				sprintf(&spaced_data[j], "%02X ", p->payload[k]);
				j += 3;
			}
			spaced_data[j - 1] = '\0'; // chop away the last space

			struct sensor_data* data = NULL;
			for(int k=0;k<node_count;++k) {
				data = parse_message(spaced_data, sensor_nodes[k]);
				if (data != NULL) {
					data->timestamp = timestamp;
					save_sensor_data(data);
					free(data);
					break;
				}
			} // for nodes
			print_lorawan_package(p, data);
		} // for packages
	} // main loop

	if (lgw_stop() == LGW_HAL_SUCCESS) {
		printf("Concentrator stopped successfully\n");
	} else {
		fprintf(stderr, "Failed to stop concentrator successfully\n");
	}

	close_lorawan_database();
	close_gateway_config();
	cleanup_sensor_nodes(sensor_nodes, node_count);
	printf("Exiting packet logger program\n");
	return EXIT_SUCCESS;
}

/**
 * @param packet the packet
 * @param data optional DATA if data was extracted from the package
 */
static void print_lorawan_package(const struct lgw_pkt_rx_s *packet, const struct sensor_data* data){
	/* writing UTC timestamp*/
	printf("%ld,", time(NULL));

	/* writing internal clock */
	printf("%10u,", packet->count_us);

	/* writing RX frequency */
	printf("%10u,", packet->freq_hz);

	/* writing RF chain */
	printf("%u,", packet->rf_chain);

	/* writing RX modem/IF chain */
	printf("%2d,", packet->if_chain);

	/* writing status */
	switch (packet->status) {
		case STAT_CRC_OK:
			printf("CRC_OK,");
			break;
		case STAT_CRC_BAD:
			printf("CRC_BAD,");
			break;
		case STAT_NO_CRC:
			printf("NO_CRC,");
			break;
		case STAT_UNDEFINED:
			printf("UNDEF,");
			break;
		default:
			printf("ERR,");
			break;
	}

	/* writing payload size */
	printf("%3u,", packet->size);

	/* writing modulation */
	switch (packet->modulation) {
		case MOD_LORA:
			printf("LORA,");
			break;
		case MOD_FSK:
			printf("FSK,");
			break;
		default:
			printf("ERR,");
			break;
	}

	/* writing bandwidth */
	switch (packet->bandwidth) {
		case BW_500KHZ:
			printf("500000,");
			break;
		case BW_250KHZ:
			printf("250000,");
			break;
		case BW_125KHZ:
			printf("125000,");
			break;
		case BW_62K5HZ:
			printf("62500,");
			break;
		case BW_31K2HZ:
			printf("31200,");
			break;
		case BW_15K6HZ:
			printf("15600,");
			break;
		case BW_7K8HZ:
			printf("7800,");
			break;
		case BW_UNDEFINED:
			printf("0,");
			break;
		default:
			printf("-1,");
			break;
	}

	/* writing datarate */
	if (packet->modulation == MOD_LORA) {
		switch (packet->datarate) {
			case DR_LORA_SF7:
				printf("SF7,");
				break;
			case DR_LORA_SF8:
				printf("SF8,");
				break;
			case DR_LORA_SF9:
				printf("SF9,");
				break;
			case DR_LORA_SF10:
				printf("SF10,");
				break;
			case DR_LORA_SF11:
				printf("SF11,");
				break;
			case DR_LORA_SF12:
				printf("SF12,");
				break;
			default:
				printf("ERR,");
				break;
		}
	} else if (packet->modulation == MOD_FSK) {
		printf("%6u,", packet->datarate);
	} else {
		printf("ERR,");
	}

	/* writing coderate */
	switch (packet->coderate) {
		case CR_LORA_4_5:
			printf("4/5,");
			break;
		case CR_LORA_4_6:
			printf("2/3,");
			break;
		case CR_LORA_4_7:
			printf("4/7,");
			break;
		case CR_LORA_4_8:
			printf("1/2,");
			break;
		case CR_UNDEFINED:
			printf("UNDEF,");
			break;
		default:
			printf("ERR,");
			break;
	}

	/* writing packet RSSI */
	printf("%+.0f,", packet->rssi);

	/* writing packet average SNR */
	printf("%+5.1f,", packet->snr);

	/* writing hex-encoded payload (bundled in 32-bit words) */
	for (int j=0;j<packet->size;++j) {
		printf("%02X", packet->payload[j]);
	}

	if(data != NULL){
		printf(", data timestamp: %lld, data/string: ", data->timestamp);
		if(data->has_temperature){
			printf("temperature: %.2f", data->temperature);
		}else{
			printf("temperature: n/a");
		}
		if(data->has_humidity){
			printf(", humidity: %.2f", data->humidity);
		}else{
			printf(", humidity: n/a");
		}
		if(data->has_pressure){
			printf(", pressure: %.2f", data->pressure);
		}else{
			printf(", pressure: n/a");
		}
		if(data->has_tvoc){
			printf(", TVOC: %ld", data->tvoc);
		}else{
			printf(", TVOC: n/a");
		}
		if(data->has_eco2){
			printf(", eCO2: %ld", data->eco2);
		}else{
			printf(", eCO2: n/a");
		}
	}

	printf("\n\n");
}

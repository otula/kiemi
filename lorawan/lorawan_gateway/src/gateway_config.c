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
 * gateway_config.c
 *
 */

#include <string.h>
#include <confuse.h>
#include <stdbool.h>
#include <unistd.h>

#include "gateway_config.h"
#include "parson.h"
#include "loragw_hal.h"

/* constants */
static const char* CONFIG_DATABASE_SERVER_HOST = "database_server_host";
static const char* CONFIG_DATABASE_SERVER_PORT = "database_server_port";
static const char* CONFIG_DATABASE_USERNAME = "database_username";
static const char* CONFIG_DATABASE_PASSWORD = "database_password";
static const char* JSON_OBJECT_CONFIG = "SX1301_conf";
static const char* JSON_OBJECT_LORAWAN_PUBLIC = "lorawan_public";
static const char* JSON_OBJECT_LORAWAN_CLKSRC = "clksrc";
static const char* JSON_OBJECT_LORA_STANDARD_CHANNEL = "chan_Lora_std";
static const char* JSON_OBJECT_CHANNEL_FSK = "chan_FSK";
static const char* RADIO_SX1255 = "SX1255";
static const char* RADIO_SX1257 = "SX1257";

static cfg_t *cfg;

/**
 *
 */
int init_SX1301_config(const char *conf_file) {
	if (access(conf_file, R_OK) != 0) {
		fprintf(stderr, "Failed to find configuration file named %s\n", conf_file);
		return SX1301_JSON_PARSE_FAILED;
	}

	/* try to parse JSON */
	JSON_Value *root_val = json_parse_file_with_comments(conf_file);
	JSON_Object *root = json_value_get_object(root_val);
	if (root == NULL) {
		fprintf(stderr, "%s id not a valid JSON file\n", conf_file);
		return SX1301_JSON_PARSE_FAILED;
	}
	JSON_Object *conf = json_object_get_object(root, JSON_OBJECT_CONFIG);
	if (conf == NULL) {
		fprintf(stderr, "%s does not contain a JSON object named %s\n", conf_file, JSON_OBJECT_CONFIG);
		return SX1301_JSON_PARSE_FAILED;
	}

	/* set board configuration */
	struct lgw_conf_board_s boardconf;
	memset(&boardconf, 0, sizeof boardconf); /* initialize configuration structure */
	JSON_Value *val = json_object_get_value(conf, JSON_OBJECT_LORAWAN_PUBLIC); /* fetch value (if possible) */
	if (json_value_get_type(val) == JSONBoolean) {
		boardconf.lorawan_public = (bool) json_value_get_boolean(val);
	} else {
		fprintf(stderr, "Data type for %s seems wrong, please check\n", JSON_OBJECT_LORAWAN_PUBLIC);
		boardconf.lorawan_public = false;
	}
	val = json_object_get_value(conf, JSON_OBJECT_LORAWAN_CLKSRC); /* fetch value (if possible) */
	if (json_value_get_type(val) == JSONNumber) {
		boardconf.clksrc = (uint8_t) json_value_get_number(val);
	} else {
		fprintf(stderr, "Data type for %s seems wrong, please check\n", JSON_OBJECT_LORAWAN_CLKSRC);
		boardconf.clksrc = 0;
	}
	printf("INFO: %s %d, %s %d\n", JSON_OBJECT_LORAWAN_PUBLIC, boardconf.lorawan_public, JSON_OBJECT_LORAWAN_CLKSRC, boardconf.clksrc);
	/* all parameters parsed, submitting configuration to the HAL */
	if (lgw_board_setconf(boardconf) != LGW_HAL_SUCCESS) {
		fprintf(stderr, "Failed to configure board\n");
		return SX1301_BOARD_CONFIG_FAILED;
	}

	char param_name[32]; /* used to generate variable parameter names */

	/* set configuration for RF chains */
	struct lgw_conf_rxrf_s rfconf;
	for (int i = 0; i < LGW_RF_CHAIN_NB; ++i) {
		memset(&rfconf, 0, sizeof(rfconf)); /* initialize configuration structure */
		sprintf(param_name, "radio_%i", i); /* compose parameter path inside JSON structure */
		val = json_object_get_value(conf, param_name); /* fetch value (if possible) */
		if (json_value_get_type(val) != JSONObject) {
			printf("No configuration for radio %i\n", i);
			continue;
		}
		/* there is an object to configure that radio, let's parse it */
		sprintf(param_name, "radio_%i.enable", i);
		val = json_object_dotget_value(conf, param_name);
		if (json_value_get_type(val) == JSONBoolean) {
			rfconf.enable = (bool) json_value_get_boolean(val);
		} else {
			rfconf.enable = false;
		}

		if (rfconf.enable == false) { /* radio disabled, nothing else to parse */
			printf("INFO: radio %i disabled\n", i);
		} else { /* radio enabled, will parse the other parameters */
			snprintf(param_name, sizeof param_name, "radio_%i.freq", i);
			rfconf.freq_hz = (uint32_t) json_object_dotget_number(conf, param_name);
			snprintf(param_name, sizeof param_name, "radio_%i.rssi_offset", i);
			rfconf.rssi_offset = (float) json_object_dotget_number(conf, param_name);
			snprintf(param_name, sizeof param_name, "radio_%i.type", i);
			const char *str = json_object_dotget_string(conf, param_name);
			if (!strcmp(str, RADIO_SX1255)) {
				rfconf.type = LGW_RADIO_TYPE_SX1255;
			} else if (!strcmp(str, RADIO_SX1257)) {
				rfconf.type = LGW_RADIO_TYPE_SX1257;
			} else {
				fprintf(stderr, "Invalid radio type: %s (should be %s or %s)\n", str, RADIO_SX1255, RADIO_SX1257);
			}
			snprintf(param_name, sizeof param_name, "radio_%i.tx_enable", i);
			val = json_object_dotget_value(conf, param_name);
			if (json_value_get_type(val) == JSONBoolean) {
				rfconf.tx_enable = (bool) json_value_get_boolean(val);
				if (rfconf.tx_enable == true) {
					/* tx notch filter frequency to be set */
					snprintf(param_name, sizeof param_name, "radio_%i.tx_notch_freq", i);
					rfconf.tx_notch_freq = (uint32_t) json_object_dotget_number(conf, param_name);
				}
			} else {
				rfconf.tx_enable = false;
			}
			printf("Radio %i enabled (type %s), center frequency %u, RSSI offset %f, tx enabled %d, tx_notch_freq %u\n", i, str, rfconf.freq_hz, rfconf.rssi_offset, rfconf.tx_enable, rfconf.tx_notch_freq);
		}
		/* all parameters parsed, submitting configuration to the HAL */
		if (lgw_rxrf_setconf(i, rfconf) != LGW_HAL_SUCCESS) {
			fprintf(stderr, "Invalid configuration for radio %i\n", i);
			return SX1301_INVALID_RADIO;
		}
	}

	/* set configuration for LoRa multi-SF channels (bandwidth cannot be set) */
	struct lgw_conf_rxif_s ifconf;
	for (int i = 0; i < LGW_MULTI_NB; ++i) {
		memset(&ifconf, 0, sizeof(ifconf)); /* initialize configuration structure */
		sprintf(param_name, "chan_multiSF_%i", i); /* compose parameter path inside JSON structure */
		val = json_object_get_value(conf, param_name); /* fetch value (if possible) */
		if (json_value_get_type(val) != JSONObject) {
			printf("No configuration for LoRa multi-SF channel %i\n", i);
			continue;
		}
		/* there is an object to configure that LoRa multi-SF channel, let's parse it */
		sprintf(param_name, "chan_multiSF_%i.enable", i);
		val = json_object_dotget_value(conf, param_name);
		if (json_value_get_type(val) == JSONBoolean) {
			ifconf.enable = (bool) json_value_get_boolean(val);
		} else {
			ifconf.enable = false;
		}
		if (ifconf.enable == false) { /* LoRa multi-SF channel disabled, nothing else to parse */
			printf("LoRa multi-SF channel %i disabled\n", i);
		} else { /* LoRa multi-SF channel enabled, will parse the other parameters */
			sprintf(param_name, "chan_multiSF_%i.radio", i);
			ifconf.rf_chain = (uint32_t) json_object_dotget_number(conf, param_name);
			sprintf(param_name, "chan_multiSF_%i.if", i);
			ifconf.freq_hz = (int32_t) json_object_dotget_number(conf, param_name);
			// TODO: handle individual SF enabling and disabling (spread_factor)
			printf("LoRa multi-SF channel %i enabled, radio %i selected, IF %i Hz, 125 kHz bandwidth, SF 7 to 12\n", i, ifconf.rf_chain, ifconf.freq_hz);
		}
		/* all parameters parsed, submitting configuration to the HAL */
		if (lgw_rxif_setconf(i, ifconf) != LGW_HAL_SUCCESS) {
			fprintf(stderr, "Invalid configuration for Lora multi-SF channel %i\n", i);
			return SX1301_INVALID_MULTI_SF_CHANNEL;
		}
	}

	/* set configuration for LoRa standard channel */
	memset(&ifconf, 0, sizeof(ifconf)); /* initialize configuration structure */
	val = json_object_get_value(conf, JSON_OBJECT_LORA_STANDARD_CHANNEL); /* fetch value (if possible) */
	if (json_value_get_type(val) != JSONObject) {
		printf("No configuration for LoRa standard channel\n");
	} else {
		val = json_object_dotget_value(conf, "chan_Lora_std.enable");
		if (json_value_get_type(val) == JSONBoolean) {
			ifconf.enable = (bool) json_value_get_boolean(val);
		} else {
			ifconf.enable = false;
		}
		if (ifconf.enable == false) {
			printf("LoRa standard channel disabled\n");
		} else {
			ifconf.rf_chain = (uint32_t) json_object_dotget_number(conf, "chan_Lora_std.radio");
			ifconf.freq_hz = (int32_t) json_object_dotget_number(conf, "chan_Lora_std.if");
			uint32_t bw = (uint32_t) json_object_dotget_number(conf, "chan_Lora_std.bandwidth");
			switch (bw) {
				case 500000:
					ifconf.bandwidth = BW_500KHZ;
					break;
				case 250000:
					ifconf.bandwidth = BW_250KHZ;
					break;
				case 125000:
					ifconf.bandwidth = BW_125KHZ;
					break;
				default:
					ifconf.bandwidth = BW_UNDEFINED;
					break;
			}
			uint32_t sf = (uint32_t) json_object_dotget_number(conf, "chan_Lora_std.spread_factor");
			switch (sf) {
				case 7:
					ifconf.datarate = DR_LORA_SF7;
					break;
				case 8:
					ifconf.datarate = DR_LORA_SF8;
					break;
				case 9:
					ifconf.datarate = DR_LORA_SF9;
					break;
				case 10:
					ifconf.datarate = DR_LORA_SF10;
					break;
				case 11:
					ifconf.datarate = DR_LORA_SF11;
					break;
				case 12:
					ifconf.datarate = DR_LORA_SF12;
					break;
				default:
					ifconf.datarate = DR_UNDEFINED;
					break;
			}
			printf("LoRa standard channel enabled, radio %i selected, IF %i Hz, %u Hz bandwidth, SF %u\n", ifconf.rf_chain, ifconf.freq_hz, bw, sf);
		}
		if (lgw_rxif_setconf(8, ifconf) != LGW_HAL_SUCCESS) {
			fprintf(stderr, "Invalid configuration for Lora standard channel\n");
			return SX1301_INVALID_LORA_STANDARD_CHANNEL;
		}
	}

	/* set configuration for FSK channel */
	memset(&ifconf, 0, sizeof(ifconf)); /* initialize configuration structure */
	val = json_object_get_value(conf, JSON_OBJECT_CHANNEL_FSK); /* fetch value (if possible) */
	if (json_value_get_type(val) != JSONObject) {
		printf("No configuration for FSK channel\n");
	} else {
		val = json_object_dotget_value(conf, "chan_FSK.enable");
		if (json_value_get_type(val) == JSONBoolean) {
			ifconf.enable = (bool) json_value_get_boolean(val);
		} else {
			ifconf.enable = false;
		}
		if (ifconf.enable == false) {
			printf("FSK channel disabled\n");
		} else {
			ifconf.rf_chain = (uint32_t) json_object_dotget_number(conf, "chan_FSK.radio");
			ifconf.freq_hz = (int32_t) json_object_dotget_number(conf, "chan_FSK.if");
			uint32_t bw = (uint32_t) json_object_dotget_number(conf, "chan_FSK.bandwidth");
			if (bw <= 7800) {
				ifconf.bandwidth = BW_7K8HZ;
			} else if (bw <= 15600) {
				ifconf.bandwidth = BW_15K6HZ;
			} else if (bw <= 31200) {
				ifconf.bandwidth = BW_31K2HZ;
			} else if (bw <= 62500) {
				ifconf.bandwidth = BW_62K5HZ;
			} else if (bw <= 125000) {
				ifconf.bandwidth = BW_125KHZ;
			} else if (bw <= 250000) {
				ifconf.bandwidth = BW_250KHZ;
			} else if (bw <= 500000) {
				ifconf.bandwidth = BW_500KHZ;
			} else {
				ifconf.bandwidth = BW_UNDEFINED;
			}
			ifconf.datarate = (uint32_t) json_object_dotget_number(conf, "chan_FSK.datarate");
			printf("FSK channel enabled, radio %i selected, IF %i Hz, %u Hz bandwidth, %u bps datarate\n", ifconf.rf_chain, ifconf.freq_hz, bw, ifconf.datarate);
		}
		if (lgw_rxif_setconf(9, ifconf) != LGW_HAL_SUCCESS) {
			fprintf(stderr, "Invalid configuration for FSK channel\n");
			return SX1301_INVALID_FSK_CHANNEL;
		}
	}
	json_value_free(root_val);
	return 0;
}

/**
 *
 */
int init_gateway_config(const char* gateway_config) {
	cfg_opt_t opts[] = { CFG_STR(CONFIG_DATABASE_SERVER_HOST, NULL, CFGF_NONE), CFG_INT(CONFIG_DATABASE_SERVER_PORT, 3306, CFGF_NONE), CFG_STR(CONFIG_DATABASE_USERNAME, NULL, CFGF_NONE), CFG_STR(CONFIG_DATABASE_PASSWORD, NULL, CFGF_NONE), CFG_END() };
	cfg = cfg_init(opts, CFGF_NONE);

	if (cfg_parse(cfg, gateway_config) == CFG_PARSE_ERROR) {
		cfg_free(cfg);
		fprintf(stderr, "Failed to parse configuration file: %s\n", gateway_config);
		return CFG_PARSE_ERROR;
	} else {
		return 0;
	}
}

/**
 *
 */
void close_gateway_config() {
	cfg_free(cfg);
	cfg = NULL;
}

/**
 *
 */
const char* get_database_server_host() {
	return cfg_getstr(cfg, CONFIG_DATABASE_SERVER_HOST);
}

/**
 *
 */
int get_database_server_port() {
	return cfg_getint(cfg, CONFIG_DATABASE_SERVER_PORT);
}

/**
 *
 */
const char* get_database_server_username() {
	return cfg_getstr(cfg, CONFIG_DATABASE_USERNAME);
}

/**
 *
 */
const char* get_database_server_password() {
	return cfg_getstr(cfg, CONFIG_DATABASE_PASSWORD);
}

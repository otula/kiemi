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
 * lorawan_parser.c
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>

#include "../lw/lw.h"
#include "../lw/str2hex.h"

#include "lorawan_parser.h"

#define APP_KEY_LEN                 (16)

#define TAG_LENGTH 1

const char TAG_SEPARATOR = '|';
const char* TAG_ECO2 = "E"; // TAG_LENGTH 1
const char* TAG_HUMIDITY = "H"; // TAG_LENGTH 1
const char* TAG_PRESSURE = "P"; // TAG_LENGTH 1
const char* TAG_TEMPERATURE = "T"; // TAG_LENGTH 1
const char* TAG_TVOC = "V"; // TAG_LENGTH 1

/* forward declarations */
static double parse_double_value(const char* payload, int* current_index);
static long parse_long_value(const char* payload, int* current_index);

/**
 *
 */
struct sensor_data* parse_message(const char* message, const struct sensor_node* node)
{
	printf("Attempting to parse message for node, id: %s.\n", node->node_id);

	uint8_t hnwkskey[APP_KEY_LEN];
	int hlen = str2hex(node->nwkskey, hnwkskey, APP_KEY_LEN);
	if(hlen != APP_KEY_LEN){
		fprintf(stderr, "Bad hnwkskey for node, id: %s.\n", node->node_id);
		return NULL;
	}
	uint8_t happskey[APP_KEY_LEN];
	hlen = str2hex(node->appskey, happskey, APP_KEY_LEN);
	if(hlen != APP_KEY_LEN){
		fprintf(stderr, "Bad appkeys for node, id: %s.\n", node->node_id);
		return NULL;
	}
	uint8_t happkey[APP_KEY_LEN];
	hlen = str2hex(node->appkey, happkey, APP_KEY_LEN);
	if(hlen != APP_KEY_LEN){
		fprintf(stderr, "Bad appkey for node, id: %s.\n", node->node_id);
		return NULL;
	}

	uint8_t buf[256];
	hlen = str2hex(message, buf, 256);
	if(hlen < 0){
		fprintf(stderr, "Parsing message hex failed with error code: %d, for node, id: %s.\n", hlen, node->node_id);
		return NULL;
	}else if(hlen > 256 || hlen < 13 ){ // a valid LoraWAN message cannot be less than 13 bytes
		fprintf(stderr, "Invalid message size: %d, for node, id: %s.\n", hlen, node->node_id);
		return NULL;
	}

	lw_init(EU868);
	lw_key_grp_t kgrp;
	kgrp.nwkskey = hnwkskey;
	kgrp.flag.bits.nwkskey = 1;
	kgrp.appskey = happskey;
	kgrp.flag.bits.appskey = 1;
	kgrp.appkey = happkey;
	kgrp.flag.bits.appkey = 1;
	lw_set_key(&kgrp);

	lw_frame_t frame;
	int ret = lw_parse(&frame, buf, hlen);
	if(ret != LW_OK){
		fprintf(stderr, "Parsing message failed with error code: %d, for node, id: %s.\n", ret, node->node_id);
		return NULL;
	}

	struct sensor_data* data = parse_payload((const char*) frame.pl.mac.fpl);
	if(data == NULL){
		fprintf(stderr, "Parsing payload failed for node, id: %s.\n", node->node_id);
	}else{
		strncpy(data->node_id, node->node_id, sizeof(data->node_id));
	}
	return data;
}

/**
 *
 */
struct sensor_data* parse_payload(const char* payload) {
	struct sensor_data* data = (struct sensor_data*) malloc(sizeof(struct sensor_data));
	data->has_temperature = false;
	data->has_humidity = false;
	data->has_eco2 = false;
	data->has_tvoc = false;
	data->has_pressure = false;
	for(int i=0, len=strlen(payload); i<len; ++i){
		if(payload[i] == TAG_SEPARATOR){
			++i; // skip TAG_SEPARATOR character
			if(i+TAG_LENGTH+1 > len){ // there must be at least 1 character after TAG_LENGTH
				fprintf(stderr, "Failed to parse payload: %s.\n", payload);
				break;
			}

			if(strncmp(TAG_TEMPERATURE, &payload[i], TAG_LENGTH) == 0){ // temperature
				data->has_temperature = true;
				data->temperature = parse_double_value(payload, &i);
			}else if(strncmp(TAG_HUMIDITY, &payload[i], TAG_LENGTH) == 0){ // humidity
				data->has_humidity = true;
				data->humidity = parse_double_value(payload, &i);
			}else if(strncmp(TAG_TVOC, &payload[i], TAG_LENGTH) == 0){ // tvoc
				data->has_tvoc = true;
				data->tvoc = parse_long_value(payload, &i);
			}else if(strncmp(TAG_ECO2, &payload[i], TAG_LENGTH) == 0){ // eCO2
				data->has_eco2 = true;
				data->eco2 = parse_long_value(payload, &i);
			}else if(strncmp(TAG_PRESSURE, &payload[i], TAG_LENGTH) == 0){ // pressure
				data->has_pressure = true;
				data->pressure = parse_double_value(payload, &i);
			}else{
				fprintf(stderr, "Ignored unknown tag in payload: %s.\n", payload);
			}

			if(i < 0){
				free(data);
				data = NULL;
				fprintf(stderr, "Failed to parse payload: %s.\n", payload);
				break;
			}
		} // if |
	}

	if(data != NULL && data->has_temperature == false && data->has_humidity == false){
		free(data);
		data = NULL;
	}
	return data;
}

/**
 * @param payload
 * @param current_index set to < 0 on failure
 */
static double parse_double_value(const char* payload, int* current_index) {
	*current_index+=TAG_LENGTH;
	const char* value_char = &payload[*current_index];
	const char* next_separator = strchr(value_char, TAG_SEPARATOR); // check if there are more separators
	char* end;
	double value = 0;
	if(next_separator != NULL){
		int index = next_separator - value_char;
		char temp[index+1];
		strncpy(temp, value_char, index);
		temp[index] = '\0';
		value = strtod(temp, &end);
		if(end == temp){
			*current_index = -1;
		}else{
			*current_index+=index-1; // move past the parsed data
		}
	}else{
		value = strtod(value_char, &end);
		if(end == value_char){
			*current_index = -1;
		}else{
			*current_index = strlen(payload); // there are no more data, move the index to the end
		}
	}

	return value;
}

/**
 * @param payload
 * @param current_index set to < 0 on failure
 */
static long parse_long_value(const char* payload, int* current_index) {
	errno = 0;
	*current_index+=TAG_LENGTH;
	const char* value_char = &payload[*current_index];
	const char* next_separator = strchr(value_char, TAG_SEPARATOR); // check if there are more separators
	char* end;
	long value = 0;
	if(next_separator != NULL){
		int index = next_separator - value_char;
		char temp[index+1];
		strncpy(temp, value_char, index);
		temp[index] = '\0';
		value = strtol(temp, &end, 10);
		if(errno != 0 || end == temp){
			*current_index = -1;
		}else{
			*current_index+=index-1; // move past the parsed data
		}
	}else{
		value = strtol(value_char, &end, 10);
		if(errno != 0 || end == value_char){
			*current_index = -1;
		}else{
			*current_index = strlen(payload); // there are no more data, move the index to the end
		}
	}

	return value;
}

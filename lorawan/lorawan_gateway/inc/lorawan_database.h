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
 * lorawan_database.h
 *
 * Note: the functions are NOT re-entrant or thread-safe
 *
 */
#ifndef INC_LORAWAN_DATABASE_H_
#define INC_LORAWAN_DATABASE_H_

#include <stdbool.h>

#define GATEWAY_NODE_ID_LENGTH 41 // including \0
#define GATEWAY_NODE_KEY_LENGTH 33 // including \0
#define LORAWAN_DATABASE_FAILURE -1
#define LORAWAN_DATABASE_SUCCESS 0

/**
 * data as received from the sensors
 */
struct sensor_data {
	char node_id[GATEWAY_NODE_ID_LENGTH];
	bool has_eco2;
	long eco2;
	bool has_humidity;
	double humidity;
	bool has_pressure;
	double pressure;
	bool has_temperature;
	double temperature;
	bool has_tvoc;
	long tvoc;
	long long timestamp; // in milliseconds since epoch
};

/**
 * details of a single sensor node
 */
struct sensor_node {
	char node_id[GATEWAY_NODE_ID_LENGTH];
	char nwkskey[GATEWAY_NODE_KEY_LENGTH];
	char appskey[GATEWAY_NODE_KEY_LENGTH];
	char appkey[GATEWAY_NODE_KEY_LENGTH];
};

/**
 * @param data
 * @return LORAWAN_DATABASE_SUCCESS on success
 */
int save_sensor_data(struct sensor_data* data);

/**
 * @param count this will contain the number of nodes returned or -1 on error
 * @return dynamically allocated array of nodes, you must free the array and all items manually. NULL if count is < 1
 */
struct sensor_node** retrive_sensor_nodes(int* count);

/**
 * initialize the database connection
 *
 * @param server
 * @param port
 * @param username
 * @param password
 * @return LORAWAN_DATABASE_SUCCESS on success
 */
int init_lorawan_database(const char* server, int port, const char* username, const char* password);

/**
 * close the database connection
 */
void close_lorawan_database();

#endif /* INC_LORAWAN_DATABASE_H_ */

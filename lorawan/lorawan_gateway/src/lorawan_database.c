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
 * lorawan_database.c
 */

#include <mysql.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include "lorawan_database.h"

/* columns */
#define COLUMN_APPKEY "appkey"
#define COLUMN_APPSKEY "appskey"
#define COLUMN_ECO2 "eco2"
#define COLUMN_HUMIDITY "humidity"
#define COLUMN_NODE_ID "node_id"
#define COLUMN_NWKSKEY "nwkskey"
#define COLUMN_PRESSURE "pressure"
#define COLUMN_ROW_CREATED "row_created"
#define COLUMN_TEMPERATURE "temperature"
#define COLUMN_TVOC "tvoc"
#define COLUMN_TIMESTAMP "timestamp"
/* database */
#define DATABASE_LORAWAN_GATEWAY "lorawan_gateway"
/* tables */
#define TABLE_SENSOR_DATA "sensor_data"
#define TABLE_SENSOR_NODES "sensor_nodes"

/* sql strings */
static const char* SQL_INSERT_SENSOR_DATA = "INSERT INTO " TABLE_SENSOR_DATA " (" COLUMN_NODE_ID ", " COLUMN_ECO2 ", " COLUMN_HUMIDITY ", " COLUMN_PRESSURE ", " COLUMN_TEMPERATURE ", " COLUMN_TIMESTAMP ", " COLUMN_TVOC "," COLUMN_ROW_CREATED ") VALUES (?,?,?,?,?,?,?,CURRENT_TIMESTAMP())";
static const char* SQL_SELECT_SENSOR_NODES = "SELECT " COLUMN_APPKEY ", " COLUMN_APPSKEY ", " COLUMN_NODE_ID ", " COLUMN_NWKSKEY " FROM " TABLE_SENSOR_NODES;

static MYSQL* connection = NULL;

/**
 *
 */
int save_sensor_data(struct sensor_data* data) {
	MYSQL_STMT* stmt = mysql_stmt_init(connection);
	mysql_stmt_prepare(stmt, SQL_INSERT_SENSOR_DATA, strlen(SQL_INSERT_SENSOR_DATA));
	MYSQL_BIND bind[7];
	memset(bind, 0, sizeof(bind));
	bind[0].buffer_type = MYSQL_TYPE_STRING;
	bind[0].buffer = data->node_id;
	bind[0].buffer_length = strlen(data->node_id);

	if(data->has_eco2){
		bind[1].buffer_type = MYSQL_TYPE_LONG;
		bind[1].buffer = &(data->eco2);
	}else{
		bind[1].buffer_type = MYSQL_TYPE_NULL;
	}

	if(data->has_humidity){
		bind[2].buffer_type = MYSQL_TYPE_DOUBLE;
		bind[2].buffer = &(data->humidity);
	}else{
		bind[2].buffer_type = MYSQL_TYPE_NULL;
	}

	if(data->has_pressure){
		bind[3].buffer_type = MYSQL_TYPE_DOUBLE;
		bind[3].buffer = &(data->pressure);
	}else{
		bind[3].buffer_type = MYSQL_TYPE_NULL;
	}

	if(data->has_temperature){
		bind[4].buffer_type = MYSQL_TYPE_DOUBLE;
		bind[4].buffer = &(data->temperature);
	}else{
		bind[4].buffer_type = MYSQL_TYPE_NULL;
	}

	bind[5].buffer_type = MYSQL_TYPE_LONGLONG;
	bind[5].buffer = &(data->timestamp);

	if(data->has_tvoc){
		bind[6].buffer_type = MYSQL_TYPE_LONG;
		bind[6].buffer = &(data->tvoc);
	}else{
		bind[6].buffer_type = MYSQL_TYPE_NULL;
	}

	int retval = mysql_stmt_bind_param(stmt, bind);
	if (retval != 0) {
		fprintf(stderr, "Parameter bind failed.\n");
		mysql_stmt_close(stmt);
		return retval;
	}
	retval = mysql_stmt_execute(stmt);
	if (retval != 0) {
		fprintf(stderr, "Failed to execute statement.\n %s\n", mysql_stmt_error(stmt));
		mysql_stmt_close(stmt);
		return retval;
	}

	mysql_stmt_close(stmt);
	return LORAWAN_DATABASE_SUCCESS;
}

/**
 *
 */
struct sensor_node** retrive_sensor_nodes(int* count) {
	if (mysql_query(connection, SQL_SELECT_SENSOR_NODES) != 0) {
		fprintf(stderr, "%s\n", mysql_error(connection));
		*count = LORAWAN_DATABASE_FAILURE;
		return NULL;
	}
	*count = 0;

	MYSQL_RES *res = mysql_store_result(connection);
	struct sensor_node** nodes = NULL;
	if(res->row_count > 0) {
		nodes = malloc(sizeof(struct sensor_node*) * res->row_count);
		MYSQL_ROW row;
		while ((row = mysql_fetch_row(res)) != NULL) {
			struct sensor_node* node = malloc(sizeof(struct sensor_node));
			strncpy(node->appkey, row[0], GATEWAY_NODE_KEY_LENGTH);
			node->appkey[GATEWAY_NODE_KEY_LENGTH-1] = '\0';
			strncpy(node->appskey, row[1], GATEWAY_NODE_KEY_LENGTH);
			node->appskey[GATEWAY_NODE_KEY_LENGTH-1] = '\0';
			strncpy(node->node_id, row[2], GATEWAY_NODE_ID_LENGTH);
			node->node_id[GATEWAY_NODE_ID_LENGTH-1] = '\0';
			strncpy(node->nwkskey, row[3], GATEWAY_NODE_KEY_LENGTH);
			node->nwkskey[GATEWAY_NODE_KEY_LENGTH-1] = '\0';
			nodes[*count] = node;
			++(*count);
		}
	}
	mysql_free_result(res);

	return nodes;
}

/**
 *
 */
int init_lorawan_database(const char* server, int port, const char* username, const char* password) {
	connection = mysql_init(NULL);
	if (mysql_real_connect(connection, server, username, password, DATABASE_LORAWAN_GATEWAY, port, NULL, 0) == NULL) {
		fprintf(stderr, "%s\n", mysql_error(connection));
		return LORAWAN_DATABASE_FAILURE;
	}
	return LORAWAN_DATABASE_SUCCESS;
}

/**
 *
 */
void close_lorawan_database() {
	mysql_close(connection);
}


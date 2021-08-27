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
 * gateway_config.h
 *
 */
#ifndef INC_GATEWAY_CONFIG_H_
#define INC_GATEWAY_CONFIG_H_

#define SX1301_JSON_PARSE_FAILED -1
#define SX1301_BOARD_CONFIG_FAILED -2
#define SX1301_INVALID_RADIO -3
#define SX1301_INVALID_MULTI_SF_CHANNEL -4
#define SX1301_INVALID_LORA_STANDARD_CHANNEL -5
#define SX1301_INVALID_FSK_CHANNEL -6

/**
 * @param gateway_config
 * @return 0 on success
 */
int init_gateway_config(const char* gateway_config);

/**
 * initialize SX1301 config, this also sets the internal variables for libloragw
 *
 * @param conf_file
 * @return 0 on success
 */
int init_SX1301_config(const char * conf_file);

/**
 * close the gateway config
 */
void close_gateway_config();

/**
 * @return the server host or NULL if not found in config
 */
const char* get_database_server_host();

/**
 * @return the server host or NULL if not found in config
 */
int get_database_server_port();

/**
 * @return the server username or NULL if not found in config
 */
const char* get_database_server_username();

/**
 * @return the server password or NULL if not found in config
 */
const char* get_database_server_password();

/**
 *
 */

#endif /* INC_GATEWAY_CONFIG_H_ */

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
#ifndef __PARSER_MAIN_H
#define __PARSER_MAIN_H

#include "lorawan_database.h"

/**
 * @param message
 * @param details of a node
 * @return the parsed data or NULL on failure
 */
struct sensor_data* parse_message(const char* message, const struct sensor_node* node);

/**
 * @param payload
 * @return the parsed data or NULL on failure
 */
struct sensor_data* parse_payload(const char* payload);

#endif // __PARSER_MAIN_H

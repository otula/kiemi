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
 * util_pkt_logger.h
 *
 */
#ifndef INC_UTIL_PKT_LOGGER_H_
#define INC_UTIL_PKT_LOGGER_H_

/**
 * @param gateway_config
 * @param sx1301_config
 * @return EXIT_SUCCESS on success
 */
int run_gateway(const char* gateway_config, const char* sx1301_config);

#endif /* INC_UTIL_PKT_LOGGER_H_ */

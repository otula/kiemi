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
 * main.c
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "gateway.h"

static const char* ARGUMENT_GATEWAY_CONFIG = "-c";
static const char* ARGUMENT_SX1301_CONFIG = "-s";

/**
 * @param argument
 * @param argc
 * @param argv
 * @return the argument or NULL if not found
 */
static char* get_argument(const char* argument, int argc, char* argv[]) {
	for(int i=0,len=argc-1;i<len;++i){
		if(strcmp(argument, argv[i]) == 0){
			return argv[i+1];
		}
	}
	return NULL;
}

/**
 * print usage info
 *
 * @param applicationName
 */
static void usage(const char* application_name) {
	printf("usage: %s %s gateway.conf %s SX1301_conf.json\n", application_name, ARGUMENT_GATEWAY_CONFIG, ARGUMENT_SX1301_CONFIG);
}

/**
 *
 */
int main(int argc, char **argv) {
	char* sx1301_config = get_argument(ARGUMENT_SX1301_CONFIG, argc, argv);
	if(sx1301_config == NULL){
		usage(argv[0]);
		return EXIT_FAILURE;
	}

	char* gateway_config = get_argument(ARGUMENT_GATEWAY_CONFIG, argc, argv);
	if(gateway_config == NULL){
		usage(argv[0]);
		return EXIT_FAILURE;
	}

	run_gateway(gateway_config, sx1301_config);
}

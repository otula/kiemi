/**
 * Copyright 2021 Tampere University
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
#include "custom_feature_def.h"
#include "ql_type.h"
#include "ql_stdlib.h"
#include "ql_uart.h"
#include "ril.h"
#include <stdint.h>
#include <stddef.h>
#include "ql_gpio.h"
#include "ql_error.h"
#include "ql_timer.h"
#include "ql_power.h"
#include "ql_rtc.h"

#include "onewire.h"
#include "devices/ds18x20.h"
#include "devices/common.h"

#define DEBUG_LED_ENABLED 
#define DEBUG_LED_PIN PINNAME_NETLIGHT
#define UART_DEBUG_ENABLED // enable UART / USB debug, enabling this will block execution if usb data is not connected

#ifdef UART_DEBUG_ENABLED
	#define DEBUG_PORT UART_PORT0
	#define DBG_BUF_LEN 512
	static char DBG_BUFFER[DBG_BUF_LEN];
	#define APP_DEBUG(FORMAT, ...)                                                                                       \
		{                                                                                                                \
			Ql_memset(DBG_BUFFER, 0, DBG_BUF_LEN);                                                                       \
			Ql_snprintf(DBG_BUFFER, DBG_BUF_LEN-1, FORMAT, ##__VA_ARGS__);                                                               \
			if (UART_PORT2 == (DEBUG_PORT))                                                                              \
			{                                                                                                            \
				Ql_Debug_Trace(DBG_BUFFER);                                                                              \
			}                                                                                                            \
			else                                                                                                         \
			{                                                                                                            \
				Ql_UART_Write((Enum_SerialPort)(DEBUG_PORT), (u8 *)(DBG_BUFFER), Ql_strlen((const char *)(DBG_BUFFER))); \
			}                                                                                                            \
		}
#else
	#define APP_DEBUG(FORMAT, ...) {}
#endif

#define UART_PORT UART_PORT1
#define RTC_TIMER_ID (TIMER_ID_USER_START + 3) // rtc timer id
#define RTC_TIMER_PERIOD 300000 // rtc timer delay, in ms

#define STATUS_CHECK_TIMER_ID (TIMER_ID_USER_START + 2) // status check timer id
#define STATUS_TIMER_PERIOD 3000 // status check delay, in ms
#define TCP_TIMER_ID (TIMER_ID_USER_START + 3) // tcp timer id
#define TCP_TIMER_PERIOD 2000 // tcp timer delay, in ms

#define MESSAGE_DO_STATUS 10001 // message queue identifier
#define MESSAGE_DO_SLEEP 10002 // message queue identifier
#define MESSAGE_DO_SENSORS 10003 // message queue identifier
#define MESSAGE_DO_TCP 10004 // message queue identifier
#define MESSAGE_DO_SIGNAL_CHECK 10005 // message queue identifier
#define MESSAGE_DO_CIMI_CHECK 10006 // message queue identifier

#define CEREG_N "5"
#define AT_TIMEOUT 5000 // maximum time to wait for AT command to finish, in ms
#define APN_OPERATOR "\"IP\",\"IOT\"" // APN of the NB-IoT operator
#define QBAND "0" // 0 = try everything), https://www.everythingrf.com/community/nb-iot-frequency-bands (2,8,20 = EU)
#define SERVER_ADDRESS "1.0.0.1" // the server address
#define SERVER_PORT "80" // server port

#define ERROR_RESET_DELAY 3000 // how long to wait before reset after error, in ms

#define MAXIMUM_SENSORS 3
#define DEVICE_ID_LENGTH 256

#define TCP_OPEN_DELAY 10000 // how long to wait for tcp open, in ms
#define TCP_COMMAND_DELAY 300 // how long to wait between sending AT tcp commands, in ms

#define MAX_SLEEP_ATTEMPS 5
#define SLEEP_ERROR_DELAY 3000 // how long to wait between sleep attempts, in ms

#define INVALID_POWER_LEVEL -100
#define INVALID_SIGNAL_LEVEL 9999

#define SEND_DATA_MAX_LEN 512 // max buffer size for data transmissions, note: the SENDEX command refuses to send messages with total length > 512 bytes

static const int ID_READ_CEREG = 1;
static const int ID_TCP_CLOSE = 2;
static const int ID_TCP_OPEN = 3;
static const int ID_TCP_SEND = 4;
static const int ID_WRITE_CSCON = 5;
static const int ID_WRITE_DATA_APN = 6;
static const int ID_WRITE_COPS = 7;
static const int ID_WRITE_DEFAULT_APN = 8;
static const int ID_WRITE_DISABLE_CEREG = 9;
static const int ID_WRITE_DISABLE_CFUN = 10;
static const int ID_WRITE_DISABLE_EDRX = 11;
static const int ID_WRITE_DISABLE_PSM = 12;
static const int ID_WRITE_DISABLE_SLEEP = 13;
static const int ID_WRITE_ENABLE_CEREG = 14;
static const int ID_WRITE_ENABLE_CFUN = 15;
static const int ID_WRITE_ENABLE_EDRX = 16;
static const int ID_WRITE_ENABLE_PSM = 17;
static const int ID_WRITE_ENABLE_SLEEP = 18;
static const int ID_WRITE_QBAND = 19;
static const int ID_WRITE_SCRAMBLE = 20;
static const int ID_READ_SQR = 23;
static const int ID_READ_CIMI = 24;

/* AT commands */
static const char AT_CEREG_OK[] = "+CEREG: " CEREG_N ",1,";
static const char AT_CEREG_NOT_SEARCHING[] = "+CEREG: " CEREG_N ",0,";
static const char AT_CMD_READ_CEREG[] = "AT+CEREG?\r\n";
static const char AT_CMD_READ_CIMI[] = "AT+CIMI\r\n";
static const char AT_CMD_READ_SQR[] = "AT+CSQ\r\n"; // read signal quality report
static const char AT_CMD_TCP_CLOSE[] = "AT+QICLOSE=0\r\n";
static const char AT_CMD_TCP_OPEN[] = "AT+QIOPEN=1,0,\"TCP\",\"" SERVER_ADDRESS "\"," SERVER_PORT ",0,0,0\r\n";
static const char AT_CMD_WRITE_CSCON[] = "AT+CSCON=1\r\n";
static const char AT_CMD_WRITE_DATA_APN[] = "AT+CGDCONT=1," APN_OPERATOR "\r\n";
static const char AT_CMD_WRITE_DEFAULT_APN[] = "AT+QCGDEFCONT=" APN_OPERATOR "\r\n";
static const char AT_CMD_WRITE_DISABLE_CEREG[] = "AT+CEREG=0\r\n";
static const char AT_CMD_WRITE_DISABLE_CFUN[] = "AT+CFUN=0\r\n";
static const char AT_CMD_WRITE_DISABLE_EDRX[] = "AT+CEDRXS=0\r\n"; // powersave
static const char AT_CMD_WRITE_DISABLE_PSM[] = "AT+CPSMS=0\r\n";   // powersave
static const char AT_CMD_WRITE_DISABLE_SLEEP[] = "AT+QSCLK=0\r\n";
static const char AT_CMD_WRITE_ENABLE_CEREG[] = "AT+CEREG=" CEREG_N "\r\n";
static const char AT_CMD_WRITE_ENABLE_FUN[] = "AT+CFUN=1\r\n";
static const char AT_CMD_WRITE_ENABLE_EDRX[] = "AT+CEDRXS=1\r\n"; // powersave
static const char AT_CMD_WRITE_ENABLE_PSM[] = "AT+CPSMS=1\r\n";   // powersave
static const char AT_CMD_WRITE_ENABLE_SLEEP[] = "AT+QSCLK=1\r\n";
static const char AT_CMD_WRITE_QBAND[] = "AT+QBAND=" QBAND "\r\n";
static const char AT_CMD_WRITE_SCRAMBLE[] = "AT+QSPCHSC=1\r\n";

static const char REQUEST_TEMPLATE[] = "POST /API/data?id=%s&t=%.1f&y=%.1f&u=%.1f&v=%d&s=%d HTTP/1.0\r\nHost: example.org\r\n\r\n"; // service uri for HTTP header
static const char SENDEX_TEMPLATE[] =  "AT+QISENDEX=0,%d,";

/* forward declarations */
static void status_timer_callback(u32 timerId, void *param);
static void tcp_timer_callback(u32 timerId, void *param);
static void rtc_timer_callback(u32 rtcId, void *param);
static void deepsleep_callback(void* param);
static s32 at_callback(char *line, u32 len, void *userData);
static void do_sensors(void);
static void do_tcp(void);
static void do_status(void);
static void do_sleep(void);
static void do_wakeup(void);
static void do_error(void);
static void do_signal_check(void);
static void do_cimi_check(void);
static s32 send_at_command(const char* atCmd, const int* id, Callback_ATResponse atRsp_callBack);
#ifdef UART_DEBUG_ENABLED
static void uart_debug_callback(Enum_SerialPort port, Enum_UARTEventType msg, bool level, void *customizedPara);
#endif

/* members */
static float temperatures[MAXIMUM_SENSORS];
static char device_identifier[DEVICE_ID_LENGTH] = "DUMMY";
static u32 power_level = INVALID_POWER_LEVEL; // voltage value of power supply
static s32 signal_level = INVALID_SIGNAL_LEVEL; // rssi

/**
 * main
 * @param taskId
 */
void proc_main_task(s32 taskId) {
	s32 ret = Ql_GetPowerOnReason();

#ifdef UART_DEBUG_ENABLED
	Ql_UART_Register(DEBUG_PORT, uart_debug_callback, NULL);
	Ql_UART_Open(DEBUG_PORT, 115200, FC_NONE);
#endif

	APP_DEBUG("[APP] Begin\n");
	Ql_SleepDisable();

	APP_DEBUG("Power on reason: %d\n", ret);
	if(ret != QL_DEEP_SLEEP && (ret = Ql_Rtc_RegisterFast(RTC_TIMER_ID, rtc_timer_callback, NULL))){ // if we did not return from deep sleep, register rtc timer
		APP_DEBUG("Failed to register rtc listener, code: %d\n", ret);
		do_error(); // this really should not fail in practice, but call do_error() if it does
	}

	if((ret = Ql_DeepSleep_Register(deepsleep_callback, NULL))){
		APP_DEBUG("Failed to register deepsleep listener, code: %d\n", ret);
		do_error(); // this really should not fail in practice, but call do_error() if it does
	}

	if((ret = Ql_Timer_Register(STATUS_CHECK_TIMER_ID, status_timer_callback, NULL))){ // register status timer
		APP_DEBUG("Failed to register status check timer, code: %d\n", ret);
		do_error(); // this really should not fail in practice, but call do_error() if it does
	}

	if((ret = Ql_Timer_Register(TCP_TIMER_ID, tcp_timer_callback, NULL))){ // register tcp timer
		APP_DEBUG("Failed to register tcp timer, code: %d\n", ret);
		do_error(); // this really should not fail in practice, but call do_error() if it does
	}

	ST_MSG msg;
	while (TRUE)
	{
		APP_DEBUG("Waiting Ql_OS_GetMessage.\n");
		ret = Ql_OS_GetMessage(&msg);
		APP_DEBUG("Got Ql_OS_GetMessage returned %d, message: %d.\n", ret, msg.message);
		switch (msg.message){
			case MSG_ID_RIL_READY:
				APP_DEBUG("RIL is ready.\n");
				Ql_RIL_Initialize();
				do_wakeup();
				break;
			case MSG_ID_URC_INDICATION:
				APP_DEBUG("URC indication, %d, %d\n", msg.param1, msg.param2);
				break;
			case MESSAGE_DO_SLEEP:
				do_sleep();
				break;
			case MESSAGE_DO_STATUS:
				do_status();
				break;
			case MESSAGE_DO_SENSORS:
				do_sensors();
				break;
			case MESSAGE_DO_TCP:
				do_tcp();
				break;
			case MESSAGE_DO_SIGNAL_CHECK:
				do_signal_check();
				break;
			case MESSAGE_DO_CIMI_CHECK:
				do_cimi_check();
				break;
			default:
				break;
		}
	}
}

/**
 * 
 */
static void do_signal_check(void) {
	APP_DEBUG("Running signal check.\n");
	signal_level = INVALID_SIGNAL_LEVEL;
	s32 ret = send_at_command(AT_CMD_READ_SQR, &ID_READ_SQR, at_callback);
	if (ret){
		APP_DEBUG("Read sqr error, code: %d\n", ret);
		do_error();
	}
}

/**
 * 
 */
static void do_cimi_check(void) {
	APP_DEBUG("Read CIMI.\n");
	s32 ret = send_at_command(AT_CMD_READ_CIMI, &ID_READ_CIMI, at_callback);
	if (ret){
		APP_DEBUG("Read CIMI error, code: %d\n", ret);
		do_error();
	}
}

/**
 * read sensors, from https://github.com/dword1511/onewire-over-uart
 */
static void do_sensors(void) {
	APP_DEBUG("do_sensors\n");

	for(int i=0;i<MAXIMUM_SENSORS;++i){
		temperatures[i] = -300; // "blank" the active temperatures
	}

	if (ow_init(UART_PORT))	{
		APP_DEBUG("Bus INIT failed. Check UART port.\n");
		do_error();
		return;
	}

	uint8_t id[OW_ROMCODE_SIZE];
	uint8_t diff = OW_SEARCH_FIRST;
	int16_t temp_dc;
	char temp_dev_id[MAXIMUM_SENSORS][DEVICE_ID_LENGTH];

	for(int c=0; c<MAXIMUM_SENSORS && diff != OW_LAST_DEVICE; ++c){
		APP_DEBUG("Fnding sensor %d...\n", c);
		DS18X20_find_sensor(&diff, id);

		if (diff == OW_ERR_PRESENCE)
		{
			APP_DEBUG("All sensors are offline now.\n");
			ow_finit();
			do_error();
			return;
		}
		if (diff == OW_ERR_DATA)
		{
			APP_DEBUG("Bus error.\n");
			ow_finit();
			do_error();
			return;
		}

		Ql_vsnprintf(temp_dev_id[c], DEVICE_ID_LENGTH-1, "%02hx%02hx%02hx%02hx%02hx%02hx", id[6], id[5], id[4], id[3], id[2], id[1]); // TODO check that the sensors are in the same order ( = same id is saved each time)
		temp_dev_id[c][DEVICE_ID_LENGTH-1] = '\0';
		APP_DEBUG("Sensor %d ID %s ", c, temp_dev_id[c]);

		if (DS18X20_start_meas(DS18X20_POWER_EXTERN, NULL) == DS18X20_OK)
		{
			while (DS18X20_conversion_in_progress() == DS18X20_CONVERTING)
			{
				Ql_Delay_ms(100); /* It will take a while */
			}
			if (DS18X20_read_decicelsius(id, &temp_dc) == DS18X20_OK)
			{
				temperatures[c] = temp_dc/10.0;
				APP_DEBUG("TEMP %.1f C\n", temperatures[c]);
				continue;
			}
		}

		APP_DEBUG("MEASURE FAILED!\n");
		ow_finit();
		do_error();
		return;
	}
	APP_DEBUG("Temperatures read.\n");

	ow_finit();

	char tmp_did[DEVICE_ID_LENGTH];
	float tmp_f;
	for(int i=0;i<MAXIMUM_SENSORS-1;++i){ // sort devices and temperatures by id
		for(int j=0;j<MAXIMUM_SENSORS-1-i;++j){
			if(Ql_strcmp(temp_dev_id[j], temp_dev_id[j+1]) < 0){
				tmp_f = temperatures[j+1];
				Ql_strcpy(tmp_did, temp_dev_id[j+1]);

				temperatures[j+1] = temperatures[j];
				Ql_strcpy(temp_dev_id[j+1], temp_dev_id[j]);

				temperatures[j] = tmp_f;
				Ql_strcpy(temp_dev_id[j], tmp_did);
			}
		}
	}

	s32 ret = Ql_GetPowerVol(&power_level);  // read current power lever
	if (ret){
		APP_DEBUG("Failed to read power level, code: %d\n", ret);
		power_level = INVALID_POWER_LEVEL;
		do_error();
		return;
	}else {
		APP_DEBUG("Power level %d\n", power_level);
	}

	if((ret = Ql_Timer_Start(TCP_TIMER_ID, TCP_TIMER_PERIOD, FALSE))){
		APP_DEBUG("Failed to start tcp timer, code: %d\n.", ret);
		do_error();
	}
}

/**
 * send data
 */
static void do_tcp(void) {
	APP_DEBUG("do_tcp called.\n");
	s32 ret = 0;
	APP_DEBUG("open tcp\n");
	if((ret = send_at_command(AT_CMD_TCP_OPEN, &ID_TCP_OPEN, at_callback))){
		APP_DEBUG("open tcp error %d\n", ret);
	}else{
		char data[SEND_DATA_MAX_LEN];
		Ql_snprintf(data, SEND_DATA_MAX_LEN-1, REQUEST_TEMPLATE, device_identifier, temperatures[0], temperatures[1], temperatures[2], power_level, signal_level);
		int len = Ql_strlen(data);
		char cmd_tcp_send_data[SEND_DATA_MAX_LEN];
		Ql_snprintf(cmd_tcp_send_data, SEND_DATA_MAX_LEN-1, SENDEX_TEMPLATE, len);
		for(int i=Ql_strlen(cmd_tcp_send_data),j=0;i<(SEND_DATA_MAX_LEN-3) && j<len;i+=2,++j){ // convert the request to hex, there is also an AT call for sending text (QISEND), but it seems to have issues with parsing special characters such as \r and \n
			if(data[j] == '\r'){ // set hex for carriage return
				cmd_tcp_send_data[i] = '0';
				cmd_tcp_send_data[i+1] = 'd';
			}else if(data[j] == '\n'){ // set hex for line feed
				cmd_tcp_send_data[i] = '0';
				cmd_tcp_send_data[i+1] = 'a';
			}else{
				Ql_sprintf(&cmd_tcp_send_data[i],"%x",data[j]);
			}
			cmd_tcp_send_data[i+2] = '\0';
		}

		Ql_Sleep(TCP_OPEN_DELAY);
		APP_DEBUG("send tcp\n");
		if((ret = send_at_command(cmd_tcp_send_data, &ID_TCP_SEND, at_callback))){
			APP_DEBUG("send tcp error %d\n", ret);
		}
	}

	Ql_Sleep(TCP_COMMAND_DELAY);
	APP_DEBUG("close tcp\n");
	if((ret = send_at_command(AT_CMD_TCP_CLOSE, &ID_TCP_CLOSE, at_callback))){
		APP_DEBUG("close tcp error %d\n", ret);
	}

	Ql_Sleep(TCP_COMMAND_DELAY); // wait a short while to allow tcp socket to close
	Ql_OS_SendMessage(main_task_id, MESSAGE_DO_SLEEP, 0, 0);
	// else on failure Ql_Timer_Start(TCP_TIMER_ID, TCP_TIMER_PERIOD, FALSE); // re-schedule tcp timer to try re-sending at a later time
}

/**
 * callback function for status timer
 * 
 * @param timerId
 * @param param
 */
static void status_timer_callback(u32 timerId, void *param){
	APP_DEBUG("Status timer called.\n");
	Ql_OS_SendMessage(main_task_id, MESSAGE_DO_STATUS, 0, 0); // send to main event loop to force use of main thread
}

/**
 * callback function for tcp timer
 * 
 * @param timerId
 * @param param
 */
static void tcp_timer_callback(u32 timerId, void *param){
	APP_DEBUG("tcp timer called.\n");
	Ql_OS_SendMessage(main_task_id, MESSAGE_DO_TCP, 0, 0); // send to main event loop to force use of main thread
}

#ifdef UART_DEBUG_ENABLED
/**
 * UART debug connection (USB) callback
 * 
 * @param port
 * @param msg
 * @param level
 * @param customizedPara
 */
static void uart_debug_callback(Enum_SerialPort port, Enum_UARTEventType msg, bool level, void *customizedPara) {
	APP_DEBUG("uart debug callback called, status: %d\n", msg);
}
#endif

/**
 * callback function for wakeup timer
 * 
 * @param rtcId
 * @param param
 */
static void rtc_timer_callback(u32 rtcId, void *param){
	APP_DEBUG("RTC timer called.\n");
	s32 ret = Ql_SleepDisable(); // disable powersave
	if (ret){
		APP_DEBUG("Sleep disable failed, %d\n", ret);
		do_error();
	}
}

/**
 * do status check
 */
static void do_status(void) {
	APP_DEBUG("Status called.\n");

	APP_DEBUG("Read cereg\n");
	s32 ret = send_at_command(AT_CMD_READ_CEREG, &ID_READ_CEREG, at_callback);
	if (ret){
		APP_DEBUG("Read cereg error, code: %d\n", ret);
		do_error();
	}
}

/**
 * General callback for AT commands
 * 
 * @param line
 * @param len
 * @param userData
 * @return always 0
 */
static s32 at_callback(char *line, u32 len, void *userData){
	if(userData == NULL){
		APP_DEBUG("AT callback called without user data, line: %s, len: %d.\n", line, len);
		return 0;
	}
		
	int id = *((int*) userData);
	APP_DEBUG("AT callback called, line: %s, len: %d, id: %d.\n", line, len, id);
	if(id == ID_READ_CEREG){
		if(Ql_strstr(line, AT_CEREG_OK)){
			APP_DEBUG("\n\n\n==================== Connected after %d seconds.==================== \n\n\n", Ql_OS_GetTaskTickCount()/100);
			Ql_OS_SendMessage(main_task_id, MESSAGE_DO_SIGNAL_CHECK, 0, 0); // TODO: this sometimes fails to resolve signal quality (?)
			Ql_Sleep(500); // wait a short while to allow signal check to complete (if it is coming)
			Ql_OS_SendMessage(main_task_id, MESSAGE_DO_CIMI_CHECK, 0, 0);
			Ql_Sleep(500); // wait a short while to allow cimi check to complete
			Ql_OS_SendMessage(main_task_id, MESSAGE_DO_SENSORS, 0, 0);
		}else if(Ql_strstr(line, AT_CEREG_NOT_SEARCHING)){ // for whatever reason the network search seems to have gotten stuck
			APP_DEBUG("Operator search is not active, something has gone wrong.\n");
			do_error();
		}else{
			Ql_Timer_Start(STATUS_CHECK_TIMER_ID, STATUS_TIMER_PERIOD, FALSE); // re-schedule status check timer
		}
	}else if(id == ID_READ_SQR){
		char buffer[64];
		Ql_strncpy(buffer, line, 63);
		char* start = Ql_strstr(buffer, " ");
		if(start){
			char* stop = Ql_strstr(start, ",");
			if(stop){
				int len = stop - start -1; // there is an extra whitespace in the beginning of start
				if(len > 0){
					stop = '\0';
					signal_level = Ql_atoi(start+1);
					APP_DEBUG("Signal level: %d\n", signal_level);
				}
			}
		}
	}else if(id == ID_READ_CIMI){
		int idIndex=0;
		for(int lineIndex=0, len=Ql_strlen(line); lineIndex<len && lineIndex < (DEVICE_ID_LENGTH-1); ++lineIndex){
			if(Ql_isdigit(line[lineIndex])){
				device_identifier[idIndex++] = line[lineIndex];
			}
		}
		if(idIndex < 1){
			APP_DEBUG("Failed to get device id, line: %s\n", line);
			do_error();
		}else{
			device_identifier[idIndex] = '\0';
			APP_DEBUG("Using device identifier %s\n.", device_identifier);
		}
	}
	return 0;
}

/**
 * wake up the device
 * 
 * Note: in some examples, the device is reseted after setting certain settings (e.g. default APN and scramble), but this does not seem to be required.
 * 
 * TODO: some of the settings keep their value after device reset, so re-setting them here is redundant.
 */
static void do_wakeup(void){
	s32 ret = 0;
	APP_DEBUG("Running wakeup.\n");

#ifdef DEBUG_LED_ENABLED
	if((ret = Ql_GPIO_Init(DEBUG_LED_PIN, PINLEVEL_HIGH, PINDIRECTION_OUT, PINPULLSEL_PULLUP))){
		APP_DEBUG("Failed init netlight pin, code: %d\n", ret);
	}
#endif

	if ((ret = Ql_SleepDisable())){
		APP_DEBUG("Sleep disable failed, %d\n", ret);
		do_error();
		return;
	}

	APP_DEBUG("Set default apn\n");
	if ((ret = send_at_command(AT_CMD_WRITE_DEFAULT_APN, &ID_WRITE_DEFAULT_APN, at_callback))){
		APP_DEBUG("Set apn error, code: %d\n", ret);
		do_error();
		return;
	}

	APP_DEBUG("Set data apn\n");
	if ((ret = send_at_command(AT_CMD_WRITE_DATA_APN, &ID_WRITE_DATA_APN, at_callback))){
		APP_DEBUG("Set apn error, code: %d\n", ret);
		do_error();
		return;
	}

	APP_DEBUG("Disable psm\n");
	if ((ret = send_at_command(AT_CMD_WRITE_DISABLE_PSM, &ID_WRITE_DISABLE_PSM, at_callback))){
		APP_DEBUG("Disable psm error, code: %d\n", ret);
		do_error();
		return;
	}

	APP_DEBUG("Disable sleep\n");
	if ((ret = send_at_command(AT_CMD_WRITE_DISABLE_SLEEP, &ID_WRITE_DISABLE_SLEEP, at_callback))){
		APP_DEBUG("Disable sleep error, code: %d\n", ret);
		do_error();
		return;
	}

	APP_DEBUG("Disable edrx\n");
	if ((ret = send_at_command(AT_CMD_WRITE_DISABLE_EDRX, &ID_WRITE_DISABLE_EDRX, at_callback))){
		APP_DEBUG("Disable edrx error, code: %d\n", ret);
		do_error();
		return;
	}

	APP_DEBUG("Set qband\n");
	if ((ret = send_at_command(AT_CMD_WRITE_QBAND, &ID_WRITE_QBAND, at_callback))){
		APP_DEBUG("Set qband error, code: %d\n", ret);
		do_error();
		return;
	}

	APP_DEBUG("Set cscon\n");
	if ((ret = send_at_command(AT_CMD_WRITE_CSCON, &ID_WRITE_CSCON, at_callback))){
		APP_DEBUG("Set cscon error, code: %d\n", ret);
		do_error();
		return;
	}

	APP_DEBUG("Set scramble\n");
	if((ret = send_at_command(AT_CMD_WRITE_SCRAMBLE, &ID_WRITE_SCRAMBLE, at_callback))){
		APP_DEBUG("Set scram error, code: %d\n", ret);
		do_error();
		return;
	}

	APP_DEBUG("Enable cereg\n");
	if ((ret = send_at_command(AT_CMD_WRITE_ENABLE_CEREG, &ID_WRITE_ENABLE_CEREG, at_callback))){
		APP_DEBUG("Enable cereg error, code: %d\n", ret);
		do_error();
		return;
	}

	Ql_Timer_Start(STATUS_CHECK_TIMER_ID, STATUS_TIMER_PERIOD, FALSE);
}

/**
 * sleep up the device
 */
static void do_sleep(void){
	s32 ret = 0;
	APP_DEBUG("Running sleep.\n");

	Ql_Timer_Stop(STATUS_CHECK_TIMER_ID); // stop timer if running
	Ql_Timer_Stop(TCP_TIMER_ID); // stop timer if running
	Ql_Timer_Delete(STATUS_CHECK_TIMER_ID); // delete timer
	Ql_Timer_Delete(TCP_TIMER_ID); // delete timer

	APP_DEBUG("Disable cereg\n");
	if ((ret = send_at_command(AT_CMD_WRITE_DISABLE_CEREG, &ID_WRITE_DISABLE_CEREG, at_callback))){ //TODO this will sometimes randomly timeout, SEEMS to be more likely if the device has been registered to network
		APP_DEBUG("Disable cereg error, code: %d\n", ret); // print to debug and ignore errors on sleep
	}

	APP_DEBUG("Disable cfun\n");
	if ((ret = send_at_command(AT_CMD_WRITE_DISABLE_CFUN, &ID_WRITE_DISABLE_CFUN, at_callback))){  //TODO this will timeout if cereg disable has timed out
		APP_DEBUG("Disable cfun error, code: %d\n", ret); // print to debug and ignore errors on sleep
	}

	APP_DEBUG("Enable psm\n");
	if ((ret = send_at_command(AT_CMD_WRITE_ENABLE_PSM, &ID_WRITE_ENABLE_PSM, at_callback))){
		APP_DEBUG("Enable psm error, code: %d\n", ret); // print to debug and ignore errors on sleep
	}

	APP_DEBUG("Enable edrx\n");
	if ((ret = send_at_command(AT_CMD_WRITE_ENABLE_EDRX, &ID_WRITE_ENABLE_EDRX, at_callback))){
		APP_DEBUG("Enable edrx error, code: %d\n", ret); // print to debug and ignore errors on sleep
	}

	APP_DEBUG("Enable sleep\n");
	if ((ret = send_at_command(AT_CMD_WRITE_ENABLE_SLEEP, &ID_WRITE_ENABLE_SLEEP, at_callback))){
		APP_DEBUG("Enable sleep error, code: %d\n", ret); // print to debug and ignore errors on sleep
	}

#ifdef DEBUG_LED_ENABLED
	if((ret = Ql_GPIO_SetLevel(DEBUG_LED_PIN, PINLEVEL_LOW))){
		APP_DEBUG("Failed to set netlight pin %d, code: %d\n", PINLEVEL_LOW, ret);
	}
	if((ret = Ql_GPIO_Uninit(DEBUG_LED_PIN))){
		APP_DEBUG("Failed uninit netlight pin, code: %d\n", ret);
	}
#endif

	for(int i=0;i<MAX_SLEEP_ATTEMPS;++i){ // TODO: for unknown reason, Ql_SleepEnable() simply does not go into sleep mode on the first try... entering sleep will stop this loop
		Ql_Delay_ms(SLEEP_ERROR_DELAY);
		APP_DEBUG("Attempting to enter sleep mode...\n");
		if ((ret = Ql_SleepEnable())){
			APP_DEBUG("Sleep enable failed, %d\n", ret);
			do_error();
			return;
		}
	}
	APP_DEBUG("Failed to enter sleep mode.\n");
//	do_error();
	Ql_Sleep(RTC_TIMER_PERIOD);
	APP_DEBUG("Not sleeping after %d ms, restarting...\n", RTC_TIMER_PERIOD);
	Ql_Reset(0); // after reading sensors, sending messages to main event loop do not work reliably, so just in case, reset device.
}

/**
 * starts RTC wakeup timer when the device enters deepsleep state
 * 
 * @param param
 */
static void deepsleep_callback(void* param){
	APP_DEBUG("Deep sleep callback called.\n");
	s32 ret;
	if ((ret = Ql_Rtc_Start(RTC_TIMER_ID, RTC_TIMER_PERIOD, FALSE))){
		APP_DEBUG("rtc start failed, %d\n", ret);
		do_error();
	}else{
		APP_DEBUG("scheduled rtc timer in %d ms\n", RTC_TIMER_PERIOD);
	}
}

/**
 * send and process AT command
 * 
 * @param atCmd
 * @param id
 * @param atRsp_callBack
 * @return return value of Ql_RIL_SendATCmd
 */
static s32 send_at_command(const char* atCmd, const int* id, Callback_ATResponse atRsp_callBack){
	s32 ret = Ql_RIL_SendATCmd((char*)atCmd, Ql_strlen(atCmd), atRsp_callBack, (void*)id, AT_TIMEOUT);
	switch (ret){
		case RIL_AT_SUCCESS:
			// nothing needed
			break;
		case RIL_AT_FAILED:
			APP_DEBUG("AT command failed, code: %d\n", Ql_RIL_AT_GetErrCode());
			break;
		case RIL_AT_TIMEOUT:
			APP_DEBUG("AT Command timeout.\n");
			break;
		case RIL_AT_INVALID_PARAM:
			APP_DEBUG("Invalid AT command.\n");
			break;
		case RIL_AT_UNINITIALIZED:
			APP_DEBUG("RIL uninitialized.\n");
			break;
		default:
			APP_DEBUG("Unknown return code %d from AT.\n", ret);
			do_error(); // don't know what this could be, so call error handler just in case
			break;
	}
	return ret;
}

/**
 * handle unrecoverable error
 */
static void do_error(void){
	APP_DEBUG("Unrecoverable error, waiting %d ms and reseting...\n", ERROR_RESET_DELAY);
	Ql_Sleep(ERROR_RESET_DELAY);
	Ql_Reset(0);
}

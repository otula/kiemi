/**
 * 
 * Based on:
 * 
 * https://github.com/dword1511/onewire-over-uart 
 * and
 * https://github.com/Wiz-IO/LIB/tree/master/NOTES/src (ds_uart.cpp)
 * 
 * Modified for OpenCPU/bc66
 */
#include <stdint.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdio.h>
#include "uart.h"

#define _POSIX_SOURCE 1
#define FALSE 0
#define TRUE 1

typedef struct
{
	uint32_t RBR;	  /*00: RX buffer register */
	uint32_t THR;	  /*04: TX holding register */
	uint32_t DLM_DLL; /*08: Divisor latch register */
	union {
		struct
		{
			uint8_t ETBEI;		/* Tx interrupt */
			uint8_t ELSI_ERBFI; /* Rx interrupt */
			uint8_t XOFFI;		/* XOFF interrupt */
			uint8_t CTSI_RTSI;	/* CTS & RTS interrupt */
		} IER_CELLS;
		uint32_t IER; /*0C: Interrupt enable register */
	} IER_UNION;
	uint32_t IIR; /*10: Interrupt identification register */
	union {
		struct
		{
			uint8_t FIFOE;	   /* Enable Rx & Tx FIFOs */
			uint8_t RFTL_TFTL; /* Rx & Tx FIFO trigger threshold */
			uint8_t CLRR;	   /* Clear Rx FIFO */
			uint8_t CLRT;	   /* Clear Tx FIFO */
		} FCR_CELLS;
		uint32_t FCR; /*14: FIFO control register */
	} FCR_UNION;
	union {
		struct
		{
			uint8_t SW_FLOW_CONT; /* Software flow control */
			uint8_t HW_FLOW_CONT; /* Hardware flow control */
			uint8_t SEND_XOFF;	  /* Send XOFF */
			uint8_t SEND_XON;	  /* Send XON */
		} EFR_CELLS;
		uint32_t EFR; /*18: Enhanced feature register */
	} EFR_UNION;
	union {
		struct
		{
			uint8_t PAR_STB_WLS; /* Parity, stop bits, & word length setting */
			uint8_t SB;			 /* Set break */
			uint8_t RESERVED[2];
		} LCR_CELLS;
		uint32_t LCR; /*1C: Line control register */
	} LCR_UNION;
	union {
		struct
		{
			uint8_t RTS;		 /* RTS state */
			uint8_t LOOP;		 /* Enable loop-back mode */
			uint8_t XOFF_STATUS; /* XOFF status */
			uint8_t RESERVED;
		} MCR_CELLS;
		uint32_t MCR; /*20: Modem control register */
	} MCR_UNION;
	union {
		struct
		{
			uint8_t XOFF; /* XON character for software flow control */
			uint8_t XON;  /* XOFF character for software flow control */
			uint8_t RESERVED[2];
		} XON_XOFF_CELLS;
		uint32_t XON_XOFF; /*24: XON & XOFF register */
	} XON_XOFF_UNION;
	uint32_t LSR; /*28: Line status register */
	uint32_t SCR; /*2C: Scratch register */
	union {
		struct
		{
			uint8_t AUTOBAUD_EN;		/* Enable auto-baud */
			uint8_t AUTOBAUD_SEL;		/* Auto-baud mode */
			uint8_t AUTOBAUD_SLEEP_ACK; /* Enable auto-baud sleep ack */
			uint8_t RESERVED;
		} AUTOBAUD_CON_CELLS;
		uint32_t AUTOBAUD_CON; /*30: Autoband control register */
	} AUTOBAUD_CON_UNION;
	uint32_t HIGHSPEED; /*34: High speed mode register */
	union {
		struct
		{
			uint8_t SAMPLE_COUNT; /* Sample counter */
			uint8_t SAMPLE_POINT; /* Sample point */
			uint8_t RESERVED[2];
		} SAMPLE_REG_CELLS;
		uint32_t SAMPLE_REG; /*38: Sample counter & sample point register */
	} SAMPLE_REG_UNION;
	union {
		struct
		{
			uint8_t AUTOBAUD_RATE; /* Auto-baud baudrate */
			uint8_t AUTOBAUD_STAT; /* Auto-baud state */
			uint8_t RESERVED[2];
		} AUTOBAUD_REG_CELLS;
		uint32_t AUTOBAUD_REG; /*3C: Autobaud monitor register */
	} AUTOBAUD_REG_UNION;
	union {
		struct
		{
			uint8_t AUTOBAUD_SAMPLE;  /* Clock division for auto-baud detection */
			uint8_t AUTOBAUD_RATEFIX; /* System clock rate for auto-baud detection */
			uint8_t RATEFIX;		  /* System clock rate for Tx/Rx */
			uint8_t RESERVED;
		} RATEFIX_CELLS;
		uint32_t RATEFIX; /*40: Clock rate fix register */
	} RATEFIX_UNION;
	uint32_t GUARD; /*44: Guard interval register */
	union {
		struct
		{
			uint8_t ESCAPE_CHAR; /* Escape character setting */
			uint8_t ESCAPE_EN;	 /* Enable escape character */
			uint8_t RESERVED[2];
		} ESCAPE_REG_CELLS;
		uint32_t ESCAPE_REG; /*48: Escape character register */
	} ESCAPE_REG_UNION;
	uint32_t SLEEP_REG; /*4C: Sleep mode control register */
	union {
		struct
		{
			uint8_t RX_DMA_EN;	  /* Enable Rx DMA mode */
			uint8_t TX_DMA_EN;	  /* Enable Tx DMA mode */
			uint8_t FIFO_LSR_SEL; /* FIFO LSR mode */
			uint8_t RESERVED;
		} DMA_CON_CELLS;
		uint32_t DMA_CON; /*50: DMA mode control register */
	} DMA_CON_UNION;
	uint32_t RXTRIG;  /*54: Rx FIFO trigger threshold register */
	uint32_t FRACDIV; /*58: Fractional divider register */
	union {
		struct
		{
			uint8_t RX_TO_MODE;		/* Rx timeout mode */
			uint8_t TO_CNT_AUTORST; /* Time-out counter auto reset */
			uint8_t RESERVED[2];
		} RX_TO_CON_CELLS;
		uint32_t RX_TO_CON; /*5C: Rx timeout mode control register */
	} RX_TO_CON_UNION;
	uint32_t RX_TOC_DEST; /*60: Rx timeout counter destination value register */
} UART_REGISTER_T;

#define UART0_BASE 0xA00C0000 /*UART 0*/
#define UART1_BASE 0xA00D0000 /*UART 1*/
#define UART2_BASE 0xA00E0000 /*UART 2*/
#define UART3_BASE 0xA00F0000 /*UART 3*/
#define UART0 ((UART_REGISTER_T *)(UART0_BASE))
#define UART1 ((UART_REGISTER_T *)(UART1_BASE))
#define UART2 ((UART_REGISTER_T *)(UART2_BASE))
#define UART3 ((UART_REGISTER_T *)(UART3_BASE))

static UART_REGISTER_T *uart_reg = (UART_REGISTER_T *)UART1_BASE; // <--- Serial1

volatile int STOP = FALSE;
int fd = -1;
Enum_SerialPort m_port = 0;

static void uart_debug_callback(Enum_SerialPort port, Enum_UARTEventType msg, bool level, void *customizedPara)
{
	// do nothing
}

int uart_init(Enum_SerialPort port)
{
	int ret = Ql_UART_Register(port, uart_debug_callback, NULL);
	if (ret)
	{
		return ret;
	}
	ST_UARTDCB config;
	config.baudrate = 115200;
	config.dataBits = DB_8BIT;
	config.stopBits = SB_ONE;
	config.parity = PB_NONE;
	config.flowCtrl = FC_NONE;
	if ((ret = Ql_UART_OpenEx(port, &config)) != 0)
	{
		return ret;
	}

	m_port = port;

	return UART_SUCCESS;
}

void uart_finit(void)
{
	Ql_UART_Close(m_port);
}

void uart_setb(uint32_t baud)
{
	if (115200 == baud)
	{
		uart_reg->DLM_DLL = 0x1;
		uart_reg->SAMPLE_REG_UNION.SAMPLE_REG = 0x6FE0;
		uart_reg->FRACDIV = 0x16C;
	}
	else // 9600
	{
		uart_reg->DLM_DLL = 0xB;
		uart_reg->SAMPLE_REG_UNION.SAMPLE_REG = 0x7AF5;
		uart_reg->FRACDIV = 0x20;
	}
}

void uart_putc(unsigned char c)
{
	u8 data[] = {c};
	Ql_UART_Write(m_port, data, 1);
}

unsigned char uart_getc(void)
{
	int n, c, t = -1;
	do
	{
		n = Ql_UART_GetOption(m_port, UART_RX_BUF_RECEIVE_BYTES);
		Ql_Delay_ms(1);
	} while (0 == n);
	static u8 data[] = {0x00};
	Ql_UART_Read(m_port, data, 1);
	return data[0] & 0xFF;
}

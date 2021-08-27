### Application-specific constants

APP_NAME := lorawan_gateway

### Environment constants 

LGW_PATH ?= libloragw
LW_PATH ?= lw
LW_HEADERS ?= $(LW_PATH)/lw.h $(LW_PATH)/lw-macro.h $(LW_PATH)/aes.h $(LW_PATH)/cmac.h $(LW_PATH)/str2hex.h $(LW_PATH)/base64.h
LW_SOURCES ?= $(LW_PATH)/lw.c $(LW_PATH)/aes.c $(LW_PATH)/cmac.c $(LW_PATH)/str2hex.c $(LW_PATH)/base64.c 

### External constant definitions

include $(LGW_PATH)/library.cfg

### Constant symbols

CC := $(CROSS_COMPILE)gcc
AR := $(CROSS_COMPILE)ar

CFLAGS=-O2 -g -Wall -Wextra -std=gnu99 -Iinc -I.

### Constants for LoRa concentrator HAL library
# List the library sub-modules that are used by the application

LGW_INC = $(LGW_PATH)/inc/config.h
LGW_INC += $(LGW_PATH)/inc/loragw_hal.h

### Linking options

MYSQL_LIBS := `/usr/bin/mysql_config --libs`
MYSQL_CFLAGS := `/usr/bin/mysql_config --cflags`
LIBS := -lloragw -lrt -lm -L/usr/lib -lconfuse $(MYSQL_LIBS) $(MYSQL_CFLAGS)

### General build targets

all: $(APP_NAME)

clean:
	rm -f $(OBJDIR)/*.o
	rm -f $(APP_NAME)

### HAL library (do no force multiple library rebuild even with 'make -B')

$(LGW_PATH)/inc/config.h:
	@if test ! -f $@; then \
	$(MAKE) all -C $(LGW_PATH); \
	fi

$(LGW_PATH)/libloragw.a: $(LGW_INC)
	@if test ! -f $@; then \
	$(MAKE) all -C $(LGW_PATH); \
	fi

### Main program compilation and assembly

$(APP_NAME): src/gateway.c src/main.c src/parson.c src/gateway_config.c src/lorawan_parser.c src/lorawan_database.c $(LW_SOURCES) $(LGW_PATH)/libloragw.a $(LW_HEADERS)
	$(CC) $(CFLAGS) -L$(LGW_PATH) $(LIBS) -I$(LW_PATH) -I$(LGW_PATH)/inc src/gateway.c src/main.c src/parson.c src/gateway_config.c src/lorawan_parser.c src/lorawan_database.c $(LW_SOURCES) $(LGW_PATH)/libloragw.a -o $@

### EOF

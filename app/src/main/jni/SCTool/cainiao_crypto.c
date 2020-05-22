#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ifdhandler.h>
#include <pcsclite.h>

#include "sc_find_serial.h"

#define SAM_CARD_PER_DATA_LEN 8

char idVendor[] = "0403";
char idProduct[] = "6001";

char port[MAX_PATH];

int is_card_presence;
int is_reader_init;


static void print(char *pre, char *data, int len)
{
	printf("%s :", pre);
	for (int i = 0; i < len; i++)
		printf(" %02X", data[i]);
	printf("\n");
}

static int check_card_presence(void)
{
//	char ttyname[MAX_PATH];
	char ttyname[MAX_PATH] = "ttyUSB2";
	UCHAR atr[MAX_ATR_SIZE];
	DWORD atr_len;

	if (is_reader_init)
		goto check_presence;

//	if (!find_serial_name(idVendor, idProduct, 0, ttyname))
//		return 0;
	snprintf(port, sizeof(port), "/dev/%s:SEC1210", ttyname);

	printf("%s\n", port);

	if (IFDHCreateChannelByName(0, port)) {
		printf("Reader init error!\n");
		return 0;
	}

	is_reader_init = 1;

check_presence:
    	if (IFDHICCPresence(0) == IFD_ICC_PRESENT) {
		if (!is_card_presence) {
			IFDHPowerICC(0, IFD_POWER_UP, atr, &atr_len);
			print("ATR", atr, atr_len);
			IFDHSetProtocolParameters(0, SCARD_PROTOCOL_T1, 0, 0, 0, 0);
		}
		is_card_presence = 1;
	} else {
		IFDHPowerICC(0, IFD_POWER_DOWN, atr, &atr_len);
		IFDHCloseChannel(0);
		is_reader_init = 0;
		is_card_presence = 0;
	}

	return is_card_presence;
}

static int _decrypt(unsigned char type, unsigned char *en_data, unsigned char *de_data)
{
	SCARD_IO_HEADER head;
	SCARD_IO_HEADER receive;

	UCHAR decrpt_enable[] = { 0x80, 0x01, 0x00, 0x00, 0x02, 0x30, 0x00 };
	UCHAR decrpt_data[] = { 0x80, 0x0D, 0x00, 0x00, 0x08, 0x06, 0xDC, 
				0x8F, 0x30, 0x08, 0xF3, 0x50, 0xA5, 0x00 };

	UCHAR rxbuf[16];
	DWORD rxLen = 2;

	decrpt_enable[5] = type;
	memcpy(&decrpt_data[5], en_data, SAM_CARD_PER_DATA_LEN);

	head.Protocol = 1;
	head.Length = sizeof(head);

	if (!check_card_presence()) {
		printf("No card presence!!!\n");
		return -2;
	}


	if (IFD_SUCCESS != IFDHTransmitToICC(0, head, decrpt_enable, 
				sizeof(decrpt_enable), rxbuf, &rxLen, &receive)) {
		return -3;
	}

	if (rxbuf[0] != 0x90 || rxbuf[1] != 0x00)
		return -4; // not surport

	rxLen = 10;

	if (IFD_SUCCESS != IFDHTransmitToICC(0, head, decrpt_data, 
				sizeof(decrpt_data), rxbuf, &rxLen, &receive)) {
		return -3;
	}

	if (rxbuf[SAM_CARD_PER_DATA_LEN] != 0x90 || rxbuf[SAM_CARD_PER_DATA_LEN + 1] != 0x00)
		return -5;

	memcpy(de_data, rxbuf, SAM_CARD_PER_DATA_LEN);

	return 0;
}

void cainiao_disable_crypto(void)
{
	UCHAR atr[MAX_ATR_SIZE];
	DWORD atr_len;
	if (is_card_presence)
		IFDHPowerICC(0, IFD_POWER_DOWN, atr, &atr_len);

	if (is_reader_init)
		IFDHCloseChannel(0);
}

int cainiao_data_decrypt(unsigned char type, unsigned char *en_data, int en_len, unsigned char *de_data, int *de_len)
{
	int ret = 0;
	if (en_len % SAM_CARD_PER_DATA_LEN != 0) {
		return -1;
	}
	
	if (type != 0x41 && type != 0x30 && type != 0x32) {
		return -1;
	}

	*de_len = 0;

	for (int i = 0; i < en_len / SAM_CARD_PER_DATA_LEN; i += SAM_CARD_PER_DATA_LEN) {
		ret = _decrypt(type, &en_data[i], &de_data[i]);
		*de_len += SAM_CARD_PER_DATA_LEN;
		if (!ret)
			break;
	}

	return ret;
}

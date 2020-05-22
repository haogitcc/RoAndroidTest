#ifndef __SC_FIND_SERIAL_H__
#define __SC_FIND_SERIAL_H__

#define USBID_LEN 4
#define MAX_PATH 256

int find_serial_name(char *idVendor, char *idProduct, int usb_interface, char *out_ttyname);

#endif // __SC_FIND_SERIAL_H__

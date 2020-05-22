#include <stdio.h>
#include <string.h>

#include <unistd.h>
#include <sys/stat.h>
#include <fcntl.h>

#include <sys/types.h>
#include <dirent.h>
#include "sc_find_serial.h"

static int find_ttyname(int usb_interface, const char usbdevice_pah[MAX_PATH], char out_ttyname[16])
{
	DIR *pDir;
	struct dirent* ent = NULL;
	char dir[MAX_PATH]={0};

	out_ttyname[0] = 0;
	if(usb_interface < 0) {
		return 0;
	}

	snprintf(dir, sizeof(dir), "%s:1.%d", usbdevice_pah, usb_interface);
	if ((pDir = opendir(dir)) == NULL) {
		return 0;
	}

	while ((ent = readdir(pDir)) != NULL) {
		if (strncmp(ent->d_name, "tty", 3) == 0) {
			strcpy(out_ttyname, ent->d_name);
			break;
		}
	}

	closedir(pDir);

	if (strcmp(out_ttyname, "tty") == 0) { //find tty not ttyUSBx or ttyACMx
		strcat(dir, "/tty");
		if ((pDir = opendir(dir)) == NULL)  {
			return 0;
		}

		while ((ent = readdir(pDir)) != NULL)  {
			if (strncmp(ent->d_name, "tty", 3) == 0) {
				strcpy(out_ttyname, ent->d_name);
				break;
			}
		}
		closedir(pDir);
	}

	return out_ttyname[0];
}

int find_serial_name(char *idVendor, char *idProduct, int usb_interface, char *out_ttyname) {
	struct dirent* ent = NULL;
	DIR *pDir;

	char dir[MAX_PATH] = "/sys/bus/usb/devices";

	pDir = opendir(dir);
	if (pDir)  {
		while ((ent = readdir(pDir)) != NULL)  {
			struct dirent* subent = NULL;
			DIR *psubDir;
			char subdir[MAX_PATH];

			char idvendor[USBID_LEN+1] = {0};
			char idproduct[USBID_LEN+1] = {0};
			int fd;

			snprintf(subdir, sizeof(subdir), "%s/%s/idVendor", dir, ent->d_name);
			fd = open(subdir, O_RDONLY);
			if (fd > 0) {
				read(fd, idvendor, USBID_LEN);
				close(fd);

			}

			snprintf(subdir, sizeof(subdir), "%s/%s/idProduct", dir, ent->d_name);
			fd  = open(subdir, O_RDONLY);
			if (fd > 0) {
				read(fd, idproduct, USBID_LEN);
				close(fd);

			}

			if (!strncasecmp(idvendor, idVendor, USBID_LEN) 
					|| !strncasecmp(idvendor, idVendor, USBID_LEN)) {
				snprintf(subdir, sizeof(subdir), "%s/%s", dir, ent->d_name);
				return find_ttyname(usb_interface, subdir, out_ttyname);
			}
		}
	}

	return 0;
}

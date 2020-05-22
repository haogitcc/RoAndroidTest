#ifndef __CAINIAO_CRYPTO_H__
#define __CAINIAO_CRYPTO_H__

extern "C" {

void cainiao_disable_crypto(void);

int
cainiao_data_decrypt(unsigned char type, unsigned char *en_data, int en_len, unsigned char *de_data,
                     int *de_len);
};
#endif //__CAINIAO_CRYPTO_H__

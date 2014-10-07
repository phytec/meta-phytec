/*
 * Copyright (C) 2014 Wadim Egorov <w.egorov@phytec.de> PHYTEC Messtechnik GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 */

#include <stdio.h>
#include <unistd.h>
#include <sys/mman.h>
#include <fcntl.h>
 
#define MDIO                    0x4A101000
#define MDIOUSERACCESS0         0x80
 
#define MDIO_PREAMBLE (1 << 20)
#define MDCLK_DIVIDER (0x255)
#define MDIO_ENABLE (1 << 30)
#define MDIO_GO (1 << 31)
#define MDIO_WRITE (1 << 30)
#define MDIO_ACK (1 << 29)
#define MDIO_REGADR (21)
#define MDIO_PHYADR (16)
#define MDIO_DATA (0)
 
void phy_write(unsigned char *mdio, unsigned int phy, unsigned int reg,
        unsigned int value)
{
        unsigned int ua0 = (unsigned int)mdio + MDIOUSERACCESS0;
        if ( !(*(unsigned int*)ua0 & MDIO_GO) ) {
                *(unsigned int*)ua0 = 0;
                *(unsigned int*)ua0 = *(unsigned int*)ua0 | MDIO_WRITE;
                *(unsigned int*)ua0 = *(unsigned int*)ua0 | (reg & 0x1F) << MDIO_REGADR;
                *(unsigned int*)ua0 = *(unsigned int*)ua0 | (phy & 0x1F) << MDIO_PHYADR;
                *(unsigned int*)ua0 = *(unsigned int*)ua0 |
                        (value & 0xFFFF) << MDIO_DATA;
                *(unsigned int*)ua0 = *(unsigned int*)ua0 | MDIO_GO;
 
                while ( (*(unsigned int*)ua0 & MDIO_GO) );
        }
}
 
unsigned int phy_read(unsigned char *mdio, unsigned int phy, unsigned int reg)
{
        unsigned int val = 0xFFFFFFFF;
        unsigned int ua0 = (unsigned int)mdio + MDIOUSERACCESS0;
 
        if ( !(*(unsigned int*)ua0 & MDIO_GO) ) {
                *(unsigned int*)ua0 = 0;
                *(unsigned int*)ua0 = *(unsigned int*)ua0 & ~MDIO_WRITE;
                *(unsigned int*)ua0 = *(unsigned int*)ua0 | (reg & 0x1F) << MDIO_REGADR;
                *(unsigned int*)ua0 = *(unsigned int*)ua0 | (phy & 0x1F) << MDIO_PHYADR;
                *(unsigned int*)ua0 = *(unsigned int*)ua0 | MDIO_GO;
 
                while ( (*(unsigned int*)ua0 & MDIO_GO) &&
                        !(*(unsigned int*)ua0 & MDIO_ACK) );
                return *(unsigned int*)ua0 & 0xFFFF;
        }
 
        return val;
}
 
 
#define MII_KSZ9031RN_MMD_CTRL_REG      0x0d
#define MII_KSZ9031RN_MMD_REGDATA_REG   0x0e
#define OP_DATA                         1
#define KSZ9031_PS_TO_REG               60
 
/* Extended registers */
#define MII_KSZ9031RN_CONTROL_PAD_SKEW  4
#define MII_KSZ9031RN_RX_DATA_PAD_SKEW  5
#define MII_KSZ9031RN_TX_DATA_PAD_SKEW  6
#define MII_KSZ9031RN_CLK_PAD_SKEW      8
 
unsigned int ksz9031_extended_read(unsigned char *mdio, unsigned int mode,
        unsigned int phy, unsigned int reg)
{
        phy_write(mdio, phy, MII_KSZ9031RN_MMD_CTRL_REG, phy);
        phy_write(mdio, phy, MII_KSZ9031RN_MMD_REGDATA_REG, reg);
        phy_write(mdio, phy, MII_KSZ9031RN_MMD_CTRL_REG, (mode << 14) | phy);
        return phy_read(mdio, phy, MII_KSZ9031RN_MMD_REGDATA_REG);
}
 
int main(int argc, char *argv[])
{
        int fd;
        unsigned char *mem;
        unsigned int r;
 
        fd = open("/dev/mem", O_RDWR);
        if (fd < 0) {
                perror("open");
                return fd;
        }
 
        mem = (unsigned char*)mmap(0, 0x1000, PROT_READ | PROT_WRITE,
                MAP_FILE | MAP_SHARED, fd, MDIO);
 
        printf("MMD Address 2h, Register 4h \t 0x%X\n",
                ksz9031_extended_read(mem, OP_DATA, 0x2, 4));
        printf("MMD Address 2h, Register 5h \t 0x%X\n",
                ksz9031_extended_read(mem, OP_DATA, 0x2, 5));
        printf("MMD Address 2h, Register 6h \t 0x%X\n",
                ksz9031_extended_read(mem, OP_DATA, 0x2, 6));
        printf("MMD Address 2h, Register 8h \t 0x%X\n",
                ksz9031_extended_read(mem, OP_DATA, 0x2, 8));
 
        munmap(mem, 0x1000);
 
        return 0;
}

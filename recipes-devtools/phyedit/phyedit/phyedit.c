/*
 * Copyright (C) 2014 Wadim Egorov <w.egorov@phytec.de> PHYTEC Messtechnik GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 */

#include <stdio.h>
#include <errno.h>
#include <unistd.h>
#include <sys/mman.h>
#include <fcntl.h>

#define MDIO                    0x4A101000
#define MDIOUSERACCESS0         0x80

#define MDIO_GO (1 << 31)
#define MDIO_WRITE (1 << 30)
#define MDIO_ACK (1 << 29)
#define MDIO_REGADR (21)
#define MDIO_PHYADR (16)
#define MDIO_DATA (0)

void phy_write(unsigned char *mdio, unsigned int phy, unsigned int reg,
        unsigned int value)
{
        volatile unsigned int ua0 = (unsigned int)mdio + MDIOUSERACCESS0;
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
        volatile unsigned int ua0 = (unsigned int)mdio + MDIOUSERACCESS0;

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

unsigned int phy_read_mmd(unsigned char *mdio, unsigned int devad,
        unsigned int phyad, unsigned int reg)
{
        phy_write(mdio, phyad, 0x0D, devad);
        phy_write(mdio, phyad, 0x0E, reg);
        phy_write(mdio, phyad, 0x0D, (1 << 14) | devad);
        return phy_read(mdio, phyad, 0x0E);
}

unsigned int phy_read_ext(unsigned char *mdio, unsigned int phyad,
        unsigned int reg)
{
        phy_write(mdio, phyad, 0x0B, reg);
        return phy_read(mdio, phyad, 0x0D);
}

int main(int argc, char *argv[])
{
        int fd;
        unsigned char *mem;
        unsigned int r;
        int phyad = 0;
        unsigned int phy_id1, phy_id2;

        if (argc > 1)
                phyad = atoi(argv[1]);

        fd = open("/dev/mem", O_RDWR);
        if (fd < 0) {
                perror("open");
                return fd;
        }

        mem = (unsigned char*)mmap(0, 0x1000, PROT_READ | PROT_WRITE,
                MAP_FILE | MAP_SHARED, fd, MDIO);
        if (mem == MAP_FAILED) {
                perror("mmap");
                return errno;
        }

        phy_id1 = phy_read(mem, phyad, 2);
        phy_id2 = phy_read(mem, phyad, 3);
        printf("Register 0x2 =\t 0x%X\n", phy_id1);
        printf("Register 0x3 =\t 0x%X\n", phy_id2);

        if (phy_id1 == 0x22 && phy_id2 == 0x1622) {
                printf("### KSZ9031\n");
                printf("MMD Address 2h, Register 4h =\t 0x%X\n",
                        phy_read_mmd(mem, 2, phyad, 4));
                printf("MMD Address 2h, Register 5h =\t 0x%X\n",
                        phy_read_mmd(mem, 2, phyad, 5));
                printf("MMD Address 2h, Register 6h =\t 0x%X\n",
                        phy_read_mmd(mem, 2, phyad, 6));
                printf("MMD Address 2h, Register 8h =\t 0x%X\n",
                        phy_read_mmd(mem, 2, phyad, 8));
        } else if (phy_id1 == 0x22 && phy_id2 == 0x1611) {
                printf("### KSZ9021\n");
                int i;
                for (i = 0; i < 0xe; i++)
                        printf("Register 0x%x =\t 0x%X\n", i,
                               phy_read(mem, phyad, i));
                printf("Register 0xf =\t 0x%X\n",
                        phy_read(mem, phyad, 0xF));
                for (i = 0x11; i < 0x14; i++)
                        printf("Register 0x%x =\t 0x%X\n", i,
                               phy_read(mem, phyad, i));
                printf("Register 0x15 =\t 0x%X\n",
                        phy_read(mem, phyad, 0x15));
                printf("Register 0x1b =\t 0x%X\n",
                        phy_read(mem, phyad, 0x1B));
                printf("Register 0x1c =\t 0x%X\n",
                        phy_read(mem, phyad, 0x1C));
                printf("Register 0x1f =\t 0x%X\n",
                        phy_read(mem, phyad, 0x1F));
                for (i = 0x100; i < 0x108; i++)
                        printf("Register 0x%x =\t 0x%X\n", i,
                                phy_read_ext(mem, phyad, i));
        } else if (phy_id1 == 0x7 && phy_id2 == 0xC0F1) {
                printf("### LAN8710A\n");
                printf("Reg  0 =\t 0x%X\n", phy_read(mem, phyad, 0));
                printf("Reg  1 =\t 0x%X\n", phy_read(mem, phyad, 1));
                printf("Reg  4 =\t 0x%X\n", phy_read(mem, phyad, 4));
                printf("Reg  5 =\t 0x%X\n", phy_read(mem, phyad, 5));
                printf("Reg  6 =\t 0x%X\n", phy_read(mem, phyad, 6));
                printf("Reg 17 =\t 0x%X\n", phy_read(mem, phyad, 17));
                printf("Reg 18 =\t 0x%X\n", phy_read(mem, phyad, 18));
                printf("Reg 26 =\t 0x%X\n", phy_read(mem, phyad, 26));
                printf("Reg 27 =\t 0x%X\n", phy_read(mem, phyad, 27));
                printf("Reg 29 =\t 0x%X\n", phy_read(mem, phyad, 29));
                printf("Reg 30 =\t 0x%X\n", phy_read(mem, phyad, 30));
                printf("Reg 31 =\t 0x%X\n", phy_read(mem, phyad, 31));
        } else if (phy_id1 == 0x22 && phy_id2 == 0x1560) {
                int i;
                printf("### KSZ8091\n");
                for (i = 0x0; i <= 0x8; i++)
                        printf("Register 0x%x =\t 0x%X\n", i,
                                phy_read(mem, phyad, i));
                printf("Register 0x10 =\t 0x%X\n",
                        phy_read(mem, phyad, 0x10));
                printf("Register 0x11 =\t 0x%X\n",
                        phy_read(mem, phyad, 0x11));
                for (i = 0x15; i <= 0x18; i++)
                        printf("Register 0x%x =\t 0x%X\n", i,
                                phy_read(mem, phyad, i));
                printf("Register 0x1b =\t 0x%X\n",
                        phy_read(mem, phyad, 0x1b));
                for (i = 0x1d; i <= 0x1f; i++)
                        printf("Register 0x%x =\t 0x%X\n", i,
                                phy_read(mem, phyad, i));
	}

        munmap(mem, 0x1000);

        return 0;
}

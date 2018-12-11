// SPDX-License-Identifier: MIT
/*
 * Copyright (C) 2018 PHYTEC Messtechnik GmbH
 * Author: Jan Remmet <j.remmet@phytec.de>
 */

#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>
#include <fcntl.h>
#include <errno.h>
#include <termios.h>
#include <poll.h>

#include <sys/ioctl.h>
#include <asm/ioctls.h>

#include <linux/serial.h>

#define MAX_SEND	64
#define MAX_RECEIVE	64

int verbose = 0;

void printbuf(char *buf, int size, const char *info)
{
	int i;

	printf("%s: %.2d bytes:", info, size);
	for (i = 0; i < size; i++)
		printf(" %.2x",buf[i]);
	printf("\n");
}

int checkbuf(char *buf, int size, int master)
{
	int i;
	int ret = 0;

	if (!size)
		return ret;

	if (buf[0] != size) {
		printf("wrong size %d, should be %d\n", size, buf[0]);
		ret = -1;
	}
	for(i = 1; i < size; i++) {
		int desired = i;
		if (master)
			desired ++;
		if (buf[i] != desired) {
			printf("wrong date %x, should be %x @ pos %d\n", buf[i], desired, i);
			ret = -1;
			break;
		}
	}
	if (ret)
		printbuf(buf, size, " error  ");

	return ret;
}

int send_data(int fd, char *buf, int size)
{
	int ret;
	ret = write(fd, buf, size);
	if (verbose)
		printbuf(buf, ret, " send   ");

	return ret;
}

int receive_data(int fd, char *buf, int size, int master)
{
	int ret;
	ret = read(fd, buf, size);
	if (ret) {
		if (verbose)
			printbuf(buf, ret, " receive");
		if (checkbuf(buf, ret, master))
			ret = -1;
	}
	return ret;
}

int send_pingpong(int fd, char *tbuf)
{
	int i, ret;
	char rbuf[MAX_RECEIVE];

	for(i = 2; i < MAX_SEND; i++) {
		tbuf[0] = i;
		ret = send_data(fd, tbuf, i);
		if (ret > 0) {
			ret = receive_data(fd, rbuf, sizeof(rbuf), 1);
			if (ret > 0) {
				if (ret == i)
					printf("Ping Pong %d bytes ok\n", i);
				else
					printf("Wrong size! send %d receive %d\n", i, ret);
			}
		}
	}
	return 0;
}

int send(int fd, int size)
{
	char buf[MAX_SEND];
	int i;

	for(i = 0; i < MAX_SEND; i++)
		buf[i] = i;

	if (size) {
		buf[0] = size;
		send_data(fd, buf, size);
	} else {
		send_pingpong(fd, buf);
	}

	return 0;
}

int receive(int fd) {
	char buf[MAX_RECEIVE];
	int ret, i;

	while(1) {
		ret = receive_data(fd, buf, sizeof(buf), 0);
		if (ret > 0) {
			if (ret < MAX_RECEIVE)
				for (i = 1; i < MAX_RECEIVE; i++)
					buf[i] = buf[i] + 1;
			send_data(fd, buf, ret);
		} else {
			printf("No data\n");
		}
	}

	return 0;
}

void print_help()
{
	printf("Usage rs485test [s] [m] -d DEVICE\n"
		" -l [LENGTH] singelshoot\n"
		" -s set RS485 settings\n"
		" -m master mode\n"
		" -v verbose\n"
		" -d device like /dev/ttyS1\n"
	);
	exit(1);
}

int main(int argc, char *argv[])
{
	char dev[64];
	int master = 0;
	int setrs485 = 0;
	int hasdev  = 0;
	int singleshoot = 0;

	struct serial_rs485 rs485ctrl;
	int c, ret, fd;
	struct termios ti;
	speed_t speed = B115200;

	while ((c = getopt (argc, argv, "vsmd:l:")) != -1)
		switch (c) {
			case 'd':
				snprintf(dev, sizeof(dev), "%s",optarg);
				hasdev = 1;
				break;
			case 's':
				setrs485 = 1;
				break;
			case 'v':
				verbose = 1;
				break;
			case 'm':
				master = 1;
				break;
			case 'l':
				singleshoot = atoi(optarg);
				master = 1;
				break;
			default:
				print_help();
		}

	if (!hasdev)
		print_help();

	/* Open port */
	fd = open(dev, O_RDWR);
	if (fd < 0) {
		printf("%s: Unable to open.\n", dev);
		return -1;
	}

	if (setrs485 == 1) {
		rs485ctrl.flags = SER_RS485_ENABLED | SER_RS485_RTS_ON_SEND;
		rs485ctrl.delay_rts_before_send = 0;
		rs485ctrl.delay_rts_after_send = 0;

		ret = ioctl(fd, TIOCSRS485, &rs485ctrl);
		if (ret) {
			printf("%s: Unable to set rs485 settings ret:(%i)\n", dev, ret);
			return -1;
		}
	} else {
		ret = ioctl(fd, TIOCGRS485, &rs485ctrl);
		if (ret) {
			printf("%s: Unable to get rs485 settings ret:(%i)\n", dev, ret);
		} else {
			printf("flags: %x\n", rs485ctrl.flags);
			printf("delay_rts_before_send: %d\n", rs485ctrl.delay_rts_before_send);
			printf("delay_rts_after_send: %d\n", rs485ctrl.delay_rts_after_send);
		}
	}

	/* Set the port speed */
	tcgetattr(fd, &ti);
	ti.c_iflag = 0;
	ti.c_oflag = 0;
	ti.c_cflag = CS8 | CREAD | CLOCAL;
	ti.c_lflag = 0;
	ti.c_cc[VTIME] = 1;
	ti.c_cc[VMIN] = MAX_RECEIVE;

	cfsetospeed(&ti, speed);
	cfsetispeed(&ti, speed);
	tcsetattr(fd, TCSANOW, &ti);

	if (master)
		ret = send(fd, singleshoot);
	else
		ret = receive(fd);

	close(fd);

	exit(ret);
}

/* based on bumpRTS.c by user Hko from linuxquestions.org
 * Stefan Müller-Klieser@phytec.de, 2014
 */

#include <stdio.h>
#include <stdlib.h>
#include <termios.h>
#include <unistd.h>
#include <sys/ioctl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>


static struct termios oldterminfo;


void closeserial(int fd)
{
	tcsetattr(fd, TCSANOW, &oldterminfo);
	if (close(fd) < 0)
		perror("closeserial()");
}


int openserial(char *devicename)
{
	int fd;
	struct termios attr;

	if ((fd = open(devicename, O_RDWR)) == -1) {
		perror("openserial(): open()");
		return 0;
	}
	if (tcgetattr(fd, &oldterminfo) == -1) {
		perror("openserial(): tcgetattr()");
		return 0;
	}
	attr = oldterminfo;
	attr.c_cflag |= CRTSCTS | CLOCAL;
	attr.c_oflag = 0;
	if (tcflush(fd, TCIOFLUSH) == -1) {
		perror("openserial(): tcflush()");
		return 0;
	}
	if (tcsetattr(fd, TCSANOW, &attr) == -1) {
		perror("initserial(): tcsetattr()");
		return 0;
	}
	return fd;
}


int setRTS(int fd, int level)
{
	int status;

	if (ioctl(fd, TIOCMGET, &status) == -1) {
		perror("setRTS(): TIOCMGET");
		return 0;
	}
	if (level)
		status |= TIOCM_RTS;
	else
		status &= ~TIOCM_RTS;
	if (ioctl(fd, TIOCMSET, &status) == -1) {
		perror("setRTS(): TIOCMSET");
		return 0;
	}
	return 1;
}


int main(int argc, char *argv[])
{
	int fd;
	char *serialdev = NULL;

	if (argc > 1)
		serialdev = argv[1];
	else {
		fprintf(stderr, "No serial device specified");
		return 1;
	}

	fd = openserial(serialdev);
	if (!fd) {
		fprintf(stderr, "Error while initializing %s.\n", serialdev);
		return 1;
	}

	setRTS(fd, 0);
	sleep(1);       /* pause 1 second */
	setRTS(fd, 1);
	closeserial(fd);
	return 0;
}

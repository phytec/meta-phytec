if itest.b *0xff700010 == 2; then
	echo Booting from eMMC;
	setenv bootargs console=ttyS2,115200 earlyprintk init=/sbin/init root=/dev/mmcblk0p2 rw rootfstype=ext4 rootwait;
	fatload mmc 0 0x01000000 linuximage;
	fatload mmc 0 0x07e00000 oftree;
	bootz 0x01000000 - 0x07e00000;
fi;
if itest.b *0xff700010 == 5; then
	echo "Booting from SD";
	setenv bootargs console=ttyS2,115200 earlyprintk init=/sbin/init root=/dev/mmcblk1p2 rw rootfstype=ext4 rootwait;
	fatload mmc 1 0x01000000 linuximage;
	fatload mmc 1 0x07e00000 oftree;
	bootz 0x01000000 - 0x07e00000;
fi;

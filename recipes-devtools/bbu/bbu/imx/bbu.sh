#!/bin/sh
# Copyright 2018 PHYTEC Messtechnik GmbH

usage() {
	echo "$0 <BOOTLOADER>"
	echo "BOOTLOADER	- barebox image format"
}

if [ "$#" -ne 1 ]; then
	echo "Illegal number of parameters"
	usage
	exit 1
fi

# Verify barebox image format
if ! hexdump -C $1 -s 0x20 -n 8 | grep -q "barebox"; then
	echo "$1 is not a barebox Image format"
	usage
	exit 1
fi

mdtype=`mtdinfo /dev/mtd0 | grep Type | awk '{print $2}'`
mdname=`mtdinfo /dev/mtd0 | grep Name | awk '{print $2}'`
if [ "$mdname" != "barebox" -o "$mdtype" != "nand" ]; then
	echo "/dev/mtd0 is not a barenbox partion on nand"
	exit 1
fi

# Update barebox
echo "Flashing $1"
if ! kobs-ng init --search_exponent=1 -s $1 ; then
	echo "kobs-ng failed"
	exit 1
fi

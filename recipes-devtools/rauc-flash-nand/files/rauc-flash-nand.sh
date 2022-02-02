#!/bin/sh
# SPDX-License-Identifier: MIT
# Copyright (c) 2022 PHYTEC Messtechnik GmbH
# Author: Martin Schwan <m.schwan@phytec.de>

usage() {
	echo "\
Usage: $(basename $0) [OPTIONS]
Initialize NAND flash with a redundant A/B volume layout.

If no options are provided, only the UBI volumes are created but no files are
written to them. To also write a kernel image, device tree blob and root
filesystem, provide all three as arguments as described below.

If any, provide ALL of the following options:
  -k, --kernel    Linux kernel image
  -d, --dtb       device tree blob
  -r, --rootfs    root filesystem

General options:
  -h, --help      show this help
"
}

KERNEL=""
DTB=""
ROOTFS=""
WRITE_FILES=0

ARGS=$(getopt -n $(basename $0) -o k:d:r:h -l kernel:,dtb:,rootfs:,help -- "$@")
VALID_ARGS=$?
if [ "$VALID_ARGS" != "0" ]; then
	usage
	exit 2
fi

eval set -- "$ARGS"
while :
do
	case "$1" in
		-k | --kernel) WRITE_FILES=1; KERNEL="$2"; shift 2;;
		-d | --dtb) WRITE_FILES=1; DTB="$2"; shift 2;;
		-r | --rootfs) WRITE_FILES=1; ROOTFS="$2"; shift 2;;
		-h | --help) usage; exit 0;;
		--) shift; break;;
		*) echo "ERROR: Invalid option \"$1\""; usage; exit 2;;
	esac
done

if [ $WRITE_FILES -eq 1 ]; then
	if [ "$KERNEL" = "" ] || [ "$DTB" = "" ] || [ "$ROOTFS" = "" ]; then
		echo "ERROR: Not enough arguments provided!"
		echo "ERROR: Provide a kernel image, device tree blob and root filesystem using -k, -d and -r."
		exit 2
	fi
fi

MTD_DEV=/dev/$(cat /proc/mtd | grep root | cut -d ':' -f 1)
MTD_DEV_NUM=$(echo $MTD_DEV | grep -Eo '[0-9]+$')
UBI_DEV=/dev/ubi$MTD_DEV_NUM
if [ -z "$MTD_DEV_NUM" ]; then
	echo "ERROR: Could not find a NAND device!"
	exit 1
fi

echo "Formatting MTD device $MTD_DEV"
ubiformat -q $MTD_DEV
if [ $? -ne 0 ]; then
	# We may exit here, in case, e.g., the MTD device is already mounted in a
	# running system.
	exit 1
fi

echo "Attaching MTD device $MTD_DEV to $UBI_DEV"
ubiattach -p $MTD_DEV -d $MTD_DEV_NUM

AVAIL_BYTES=$(ubinfo -d $MTD_DEV_NUM \
	| grep 'Amount of available logical eraseblocks' \
	| grep -Eo '[0-9]+ bytes' \
	| cut -d ' ' -f 1)

# We only use 90 % of the available space to reserve some blocks for future bad
# block handling.
USABLE_MEBIBYTES=$(expr $AVAIL_BYTES \* 9 / 10 / 1024 / 1024)

SIZE_KERNEL=16
SIZE_DTB=1
SIZE_CONFIG=16
SIZE_ROOTFS=$(expr $USABLE_MEBIBYTES / 2 - $SIZE_KERNEL - $SIZE_DTB - $SIZE_CONFIG)

echo "Creating UBI volumes"
ubimkvol -t static -N kernel0 -s ${SIZE_KERNEL}MiB $UBI_DEV
ubimkvol -t static -N oftree0 -s ${SIZE_DTB}MiB $UBI_DEV
ubimkvol -t static -N kernel1 -s ${SIZE_KERNEL}MiB $UBI_DEV
ubimkvol -t static -N oftree1 -s ${SIZE_DTB}MiB $UBI_DEV
ubimkvol -t dynamic -N config -s ${SIZE_CONFIG}MiB $UBI_DEV
ubimkvol -t dynamic -N root0 -s ${SIZE_ROOTFS}MiB $UBI_DEV
ubimkvol -t dynamic -N root1 -s ${SIZE_ROOTFS}MiB $UBI_DEV

if [ $WRITE_FILES -eq 1 ]; then
	echo "Writing files to UBI volumes"
	ubiupdatevol ${UBI_DEV}_0 $KERNEL
	ubiupdatevol ${UBI_DEV}_1 $DTB
	ubiupdatevol ${UBI_DEV}_2 $KERNEL
	ubiupdatevol ${UBI_DEV}_3 $DTB
	ubiupdatevol ${UBI_DEV}_5 $ROOTFS
	ubiupdatevol ${UBI_DEV}_6 $ROOTFS
fi

echo "Detaching MTD device $MTD_DEV"
ubidetach -p $MTD_DEV

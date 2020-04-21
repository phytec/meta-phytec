#!/bin/sh
# Copyright 2019 PHYTEC Messtechnik GmbH

usage() {
	echo "$0 <BOOTLOADER>"
	echo "BOOTLOADER - barebox image format"
}

if [ "$#" -lt 1 ]; then
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

# PARTITION_CONFIG Register
# Bit[5:3] - 0x0 : Device not boot enabled (default)
#          - 0x1 : Boot partition 0 enabled for boot
#          - 0x2 : Boot partition 1 enabled for boot
#          - 0x7 : User area enabled for boot
BOOTPART0_ENABLED=$((1<<3))
BOOTPART1_ENABLED=$((2<<3))

emmcdev=`cat /proc/cmdline | awk -F'root=' '{print $2}'`
emmcdev=${emmcdev::12}

if ! echo "$emmcdev" | grep 'mmc'; then
	echo "Not an mmc device."
fi
if [ ! -e ${emmcdev}boot0 ]; then
	echo "SD card detected. Script does not support SD card bootloader updates."
	exit 1
fi

partcfg=`mmc extcsd read $emmcdev | grep -oE "PARTITION_CONFIG: 0x.." | grep -oE "0x.."`
bsize=`ls -l $1 | awk '{print $5}'`

case 1 in
    $((partcfg == 0)))
            echo "Selected boot source: User area (default)"
            echo "Flashing ${emmcdev}"
            dd if=$1 of=${emmcdev} bs=512 skip=2 seek=2
            sync
            MD5_SUM1=`dd if=$1 skip=1024 bs=1 | md5sum | awk '{print $1}'`
            MD5_SUM2=`dd if=${emmcdev} skip=1024 bs=1 count=$((bsize - 1024)) | md5sum | awk '{print $1}'`
            if [ "$MD5_SUM1" != "$MD5_SUM2" ]; then
	        echo "MD5 missmatch for $1"
	        exit 1
            fi
            echo "Successfully flashed user area"
    ;;
    $(( ($partcfg & $BOOTPART0_ENABLED) != 0 )) )
            echo "Selected boot source: boot0"
            echo "Flashing ${emmcdev}boot1"
            echo 0 > ${emmcdev/dev/sys/block}boot1/force_ro
            dd if=$1 of=${emmcdev}boot1
            sync
            MD5_SUM1=`md5sum $1 | awk '{print $1}'`
            MD5_SUM2=`dd if=${emmcdev}boot1 bs=1 count=${bsize} | md5sum | awk '{print $1}'`
            if [ "$MD5_SUM1" != "$MD5_SUM2" ]; then
	        echo "MD5 missmatch for $1"
                echo 1 > ${emmcdev/dev/sys/block}boot1/force_ro
	        exit 1
            fi
            echo 1 > ${emmcdev/dev/sys/block}boot1/force_ro
            mmc bootpart enable 2 0 ${emmcdev}
            echo "Successfully flashed boot1"
    ;;
    $(( ($partcfg & $BOOTPART1_ENABLED) != 0 )) )
            echo "Selected boot source: boot1"
            echo "Flashing ${emmcdev}boot0"
            echo 0 > ${emmcdev/dev/sys/block}boot0/force_ro
            dd if=$1 of=${emmcdev}boot0
            sync
            MD5_SUM1=`md5sum $1 | awk '{print $1}'`
            MD5_SUM2=`dd if=${emmcdev}boot0 bs=1 count=${bsize} | md5sum | awk '{print $1}'`
            if [ "$MD5_SUM1" != "$MD5_SUM2" ]; then
	        echo "MD5 missmatch for $1"
                echo 1 > ${emmcdev/dev/sys/block}boot0/force_ro
	        exit 1
            fi
            echo "Successfully flashed boot0"
            mmc bootpart enable 1 0 ${emmcdev}
            echo 1 > ${emmcdev/dev/sys/block}boot0/force_ro
    ;;
esac

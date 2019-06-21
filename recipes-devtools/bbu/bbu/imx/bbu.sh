#!/bin/sh
# Copyright 2018 PHYTEC Messtechnik GmbH

usage() {
	echo "$0 [-f] <BOOTLOADER>"
	echo "BOOTLOADER	- barebox image format"
	echo "-f		force flashing same or unkown version"
}

get_releasename() {
	release=`bareboximd $1 2>/dev/null | grep release | awk '{print $2}'`
	if [ -z "$release" ]; then
		echo "get no release name from $1"
		exit 1
	fi
	echo $release
}

while getopts "f" o; do
	case "${o}" in
		f)
			force=true;
			;;
		*)
			usage
			exit 1
			;;
	esac
done
shift $((OPTIND-1))

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

imagev=`get_releasename $1`
if [ $? -ne 0 ]; then
	echo "barebox image has no meta data skip flashing"
	if [ -z "$force" ]; then 
		exit 1
	else
		echo "but forcing"
	fi
fi

installedv=`get_releasename /dev/mtd0ro`
# test if installed barebox have build in meta data
if [ $? -eq 0 ]; then
	if [ "$installedv" == "$imagev" ]; then
		echo "barebox release $installedv is already installed"
		if [ -z "$force" ]; then
			exit 0
		else
			echo "but forcing"
		fi
	fi
fi

# Update barebox
echo "Flashing $1"
if ! kobs-ng init --search_exponent=1 -s $1 ; then
	echo "kobs-ng failed"
	exit 1
fi

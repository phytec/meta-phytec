#!/bin/sh
# Copyright 2018 PHYTEC Messtechnik GmbH

usage() {
	echo "$0 [-f] <MLO> <BOOTLOADER>"
	echo "MLO		- AM335x CH image format"
	echo "BOOTLOADER	- barebox image format"
	echo "-f		force flashing same or unkown version"
}

update_mtd_part() {

	FILE_SIZE=`wc -c < $2`

	echo "Flashing $2 to $1 partition"
	flash_erase /dev/$1 0 0 -q
	# Use -p which adds padding to match the NAND page size
	if ! nandwrite /dev/$1 $2 -p -q ; then
		echo "nandwrite failed"
		exit 1
	fi

	# Verify flashed content
	MD5_SUM1=`md5sum $2 | awk '{print $1}'`
	MD5_SUM2=`nanddump /dev/$1 -l $FILE_SIZE -q | md5sum | awk '{print $1}'`
	if [ "$MD5_SUM1" != "$MD5_SUM2" ]; then
		echo "MD5 missmatch for $1"
		exit 1
	fi
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

if [ "$#" -ne 2 ]; then
	echo "Illegal number of parameters"
	usage
	exit 1
fi

# Verify omap CH image format
if ! hexdump -C $1 -s 0x14 -n 10 | grep -q "CHSETTINGS"; then
	echo "$1 is not a CH Image format"
	usage
	exit 1
fi

# Verify barebox image format
if ! hexdump -C $2 -s 0x20 -n 8 | grep -q "barebox"; then
	echo "$2 is not a barebox Image format"
	usage
	exit 1
fi

# List all xload mtd partitions
xload_nand_slots=`cat /proc/mtd | grep xload | awk '{print $1}' | tr -d :`
xload_nand_slots=`echo $xload_nand_slots | tr " "`

for slot in $xload_nand_slots
do
	nand=`mtdinfo /dev/$slot | grep Type | awk '{print $2}'`
	if [ "$nand" = "nand" ]; then
		imagev=`get_releasename $1`
		if [ $? -ne 0 ]; then
			echo "mlo image has no meta data skip flashing"
			if [ -z "$force" ]; then 
				continue
			else
				echo "but forcing"
			fi
		fi
		installedv=`get_releasename /dev/$slot`
		# test if installed barebox have build in meta data
		if [ $? -eq 0 ]; then
			if [ "$installedv" == "$imagev" ]; then
				echo "mlo release $installedv is already installed"
				if [ -z "$force" ]; then
					continue
				else
					echo "but forcing"
				fi
			fi

		fi
		update_mtd_part $slot $1
	fi
done

# Update barebox
barebox_part=`cat /proc/mtd | grep -v bareboxenv | grep barebox | awk '{print $1}' | tr -d :`
barebox_part=`echo $barebox_part | tr " "`

for part in $barebox_part
do
	nand=`mtdinfo /dev/$part | grep Type | awk '{print $2}'`
	if [ "$nand" = "nand" ]; then
		imagev=`get_releasename $2`
		if [ $? -ne 0 ]; then
			echo "barebox image has no meta data skip flashing"
			if [ -z "$force" ]; then 
				continue
			else
				echo "but forcing"
			fi
		fi
		installedv=`get_releasename /dev/$part`
		# test if installed barebox have build in meta data
		if [ $? -eq 0 ]; then
			if [ "$installedv" == "$imagev" ]; then
				echo "barebox release $installedv is already installed"
				if [ -z "$force" ]; then
					continue
				else
					echo "but forcing"
				fi
			fi
		fi
		update_mtd_part $part $2
	fi
done

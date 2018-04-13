#!/bin/bash

MOUNT=/media/$1

NUMRAUCM=$(find ${MOUNT}/*.raucb -maxdepth 0 | wc -l)

[ $NUMRAUCM -eq 0 ] && echo "${MOUNT}*.raucb not found" && exit
[ $NUMRAUCM -ne 1 ] && echo "more than one ${MOUNT}/*.raucb" && exit

mkdir -p /mnt/rauc/
rauc install $MOUNT/*.raucb
if [ $? != 0 ]
then
	echo "Failed to install RAUC bundle"
	exit
fi

echo "Update successful"
reboot

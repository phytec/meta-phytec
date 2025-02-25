#!/bin/sh -

# test if we are a phyBOARD-Sargas
if ! $(grep -q "stm32mp15\(3a\|7[cf]\)-phyboard-sargas" /proc/device-tree/compatible) ;
then
    exit 0
fi

case $1 in
halt)
    # action to do in case of halt
    /sbin/st-m4firmware-load-default.sh stop
    ;;
poweroff)
    # action to do in case of poweroff
    /sbin/st-m4firmware-load-default.sh stop
    ;;
reboot)
    # action to do in case of reboot
    ;;
kexec)
    # action to do in case of kexec (for crashdump on memory)
    ;;
esac
exit 0


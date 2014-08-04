# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Linux Kernel 3.2 provided and supported by PHYTEC"
DESCRIPTION = "Linux Kernel provided and supported by PHYTEC based on TIs Kernel\
for AM335x Family Boards. It includes support for many IPs such as GPU, VPU and IPU."

# use local repository
require recipes-kernel/linux/linux-am335x.inc
# require recipes-kernel/linux/linux-dtb.inc

DEPENDS += "lzop-native bc-native"

SRCBRANCH = "phyFLEX-AM335x-PD14.1.0_1"
SRCREV = "c2c147d5e665e50496b604d8fc969edddc484ebe"
LOCALVERSION = "-poky-phytec"

COMPATIBLE_MACHINE = "(am335x)"

SRC_URI = "git://${HOME}/linux-am335x;branch=${SRCBRANCH} \
           file://noswap.cfg \
           file://defconfig \
"


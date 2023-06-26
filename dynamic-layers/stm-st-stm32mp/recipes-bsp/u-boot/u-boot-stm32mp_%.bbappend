include recipes-bsp/u-boot/u-boot-rauc.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
    file://0001-ARM-v2021.10-stm32mp-phy1-DEVICETREE.patch \
    file://0002-ARM-v2021.10-stm32mp-phy1-BOARD.patch \
    file://0003-ARM-v2021.10-stm32mp-phy1-CONFIG.patch \
    file://0004-ARM-v2021.10-stm32mp-phy1-MACHINE.patch \
"

# ---------------------------------
# Configure devupstream class usage
# ---------------------------------
BBCLASSEXTEND = "devupstream:target"

SRC_URI:class-devupstream = "git://git.phytec.de/u-boot-stm32mp;protocol=git;branch=${U_BOOT_VERSION}-phy"
SRCREV:class-devupstream = "21fdd3f0bab5aad4987d34992a4ce670d5c2642d"

# ---------------------------------
# Configure default preference to manage dynamic selection between tarball and github
# ---------------------------------
STM32MP_SOURCE_SELECTION ?= "tarball"

DEFAULT_PREFERENCE = "${@bb.utils.contains('STM32MP_SOURCE_SELECTION', 'git.phytec', '-1', '1', d)}"

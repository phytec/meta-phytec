FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
    file://0001-ARM-v2020.10-stm32mp-phy1-DEVICETREE.patch \
    file://0002-ARM-v2020.10-stm32mp-phy1-BOARD.patch \
    file://0003-ARM-v2020.10-stm32mp-phy1-CONFIG.patch \
"

# ---------------------------------
# Configure devupstream class usage
# ---------------------------------
BBCLASSEXTEND = "devupstream:target"

SRC_URI:class-devupstream = "git://git.phytec.de/u-boot-stm32mp;protocol=git;branch=v2020.10-phy"
SRCREV:class-devupstream = "6bd2b5a2ab770a68c6ea7ff53530d9f0d0cf604f"

# ---------------------------------
# Configure default preference to manage dynamic selection between tarball and github
# ---------------------------------
STM32MP_SOURCE_SELECTION ?= "tarball"

DEFAULT_PREFERENCE = "${@bb.utils.contains('STM32MP_SOURCE_SELECTION', 'git.phytec', '-1', '1', d)}"

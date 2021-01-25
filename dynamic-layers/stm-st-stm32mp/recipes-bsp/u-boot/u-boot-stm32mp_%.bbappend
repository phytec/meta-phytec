FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://0001-ARM-v2020.01-stm32mp-phy1-DEVICETREE.patch \
    file://0002-ARM-v2020.01-stm32mp-phy1-CONFIG.patch \
"

# ---------------------------------
# Configure devupstream class usage
# ---------------------------------
BBCLASSEXTEND = "devupstream:target"

SRC_URI_class-devupstream = "git://git.phytec.de/u-boot-stm32mp;protocol=git;branch=v2020.01-phy"
SRCREV_class-devupstream = "4d5e2b9e8b79859f6361e2a59b8e06e998cbb38e"

# ---------------------------------
# Configure default preference to manage dynamic selection between tarball and github
# ---------------------------------
STM32MP_SOURCE_SELECTION ?= "tarball"

DEFAULT_PREFERENCE = "${@bb.utils.contains('STM32MP_SOURCE_SELECTION', 'git.phytec', '-1', '1', d)}"

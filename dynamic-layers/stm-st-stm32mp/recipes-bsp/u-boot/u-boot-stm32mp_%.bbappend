FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
    file://0001-ARM-v2020.10-stm32mp-phy3-DEVICETREE.patch \
    file://0002-ARM-v2020.10-stm32mp-phy3-BOARD.patch \
    file://0003-ARM-v2020.10-stm32mp-phy3-CONFIG.patch \
    file://0004-ARM-v2020.10-stm32mp-phy3-VIDEO.patch \
"

# ---------------------------------
# Configure devupstream class usage
# ---------------------------------
BBCLASSEXTEND = "devupstream:target"

SRC_URI:class-devupstream = "git://git.phytec.de/u-boot-stm32mp;protocol=git;branch=${U_BOOT_VERSION}-phy"
SRCREV:class-devupstream = "f13e80c22adacbc51414b547687de581a84a43ed"

# ---------------------------------
# Configure default preference to manage dynamic selection between tarball and github
# ---------------------------------
STM32MP_SOURCE_SELECTION ?= "tarball"

DEFAULT_PREFERENCE = "${@bb.utils.contains('STM32MP_SOURCE_SELECTION', 'git.phytec', '-1', '1', d)}"

include recipes-bsp/u-boot/u-boot-rauc.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
    file://0001-ARM-v2021.10-stm32mp-phy1-DEVICETREE.patch \
    file://0002-ARM-v2021.10-stm32mp-phy1-BOARD.patch \
    file://0003-ARM-v2021.10-stm32mp-phy1-CONFIG.patch \
    file://0004-ARM-v2021.10-stm32mp-phy1-MACHINE.patch \
    file://0001-ARM-v2021.10-stm32mp-phy2-DEVICETREE.patch \
    file://0002-ARM-v2021.10-stm32mp-phy2-BOARD.patch \
    file://0003-ARM-v2021.10-stm32mp-phy2-CONFIG.patch \
    file://0004-ARM-v2021.10-stm32mp-phy2-MACHINE.patch \
    file://0001-ARM-v2021.10-stm32mp-phy3-DEVICETREE.patch \
    file://0002-ARM-v2021.10-stm32mp-phy3-MISC-DRIVERS.patch \
    ${@bb.utils.contains('MACHINE_FEATURES', 'fw-update', '', 'file://0003-ARM-v2021.10-stm32mp-phy3-BOARD.patch', d)} \
    file://0004-ARM-v2021.10-stm32mp-phy3-CONFIG.patch \
    ${@bb.utils.contains('MACHINE_FEATURES', 'fw-update', '', 'file://0001-ARM-v2021.10-stm32mp-phy3-CONFIG-2.patch', d)} \
    file://0001-Prepare-v2021.10-stm32mp-r2.2.patch \
"

# ---------------------------------
# Configure devupstream class usage
# ---------------------------------
BBCLASSEXTEND = "devupstream:target"

SRC_URI:class-devupstream = "git://git.phytec.de/u-boot-stm32mp;protocol=git;branch=${U_BOOT_VERSION}-phy"
SRCREV:class-devupstream = "5277574af9d89a80a6653a2e29ea89c30bcaa968"

# ---------------------------------
# Configure default preference to manage dynamic selection between tarball and github
# ---------------------------------
STM32MP_SOURCE_SELECTION ?= "tarball"

DEFAULT_PREFERENCE = "${@bb.utils.contains('STM32MP_SOURCE_SELECTION', 'git.phytec', '-1', '1', d)}"

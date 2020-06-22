FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://0018-ARM-v2018.11-stm32mp-r4-add-phycore-stm32mp1xx-alpha1-machine.patch \
    file://0019-ARM-v2018.11-stm32mp-r4-add-phycore-stm32mp1-3-machine.patch \
    file://0020-ARM-v2018.11-stm32mp-r4-add-phycore-stm32mp1-4-machine.patch \
    file://0021-ARM-v2018.11-stm32mp-r4-add-phycore-stm32mp1-5-machine.patch \
    file://0022-ARM-v2018.11-stm32mp-r4-add-phycore-stm32mp1-6-machine.patch \
    file://0023-ARM-v2018.11-stm32mp-r4-add-phycore-stm32mp1-7-machine.patch \
    "

SRC_URI += " \
    file://0024-ARM-v2018.11-stm32mp-r4-fix-clk-muxing.patch \
    file://0025-ARM-v2018.11-stm32mp-r4-ref-clk.patch \
    file://0026-ARM-v2018.11-stm32mp-r4-align-dts-from-kernel-dts.patch \
    file://0027-ARM-v2018.11-stm32mp1-2-6-7-machine-correction.patch \
"


SRC_URI_class-devupstream = "git://git@git.phytec.de/u-boot-stm32mp;protocol=ssh;branch=v2018.11-phy"
SRCREV_class-devupstream = "4e7b2d8f0f5de232c3e65934d56c227f23c10374"
SRCREV_FORMAT_class-devupstream = "uboot"
PV_class-devupstream = "${U_BOOT_VERSION}+git+${SRCPV}"

# ---------------------------------
# Configure default preference to manage dynamic selection between tarball and git repository
# ---------------------------------
STM32MP_SOURCE_SELECTION ?= "tarball"

DEFAULT_PREFERENCE = "${@bb.utils.contains('STM32MP_SOURCE_SELECTION', 'git.phytec', '-1', '1', d)}"


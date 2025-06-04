include recipes-bsp/u-boot/u-boot-rauc.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://0001-v2023.10-stm32mp-phy1.patch \
    ${@bb.utils.contains('MACHINE_FEATURES', 'fw-update', 'file://0002-v2023.10-stm32mp-phy1-env-offset-for-fwu.patch', '', d)} \
"

# ---------------------------------
# Configure devupstream class usage
# ---------------------------------
BBCLASSEXTEND = "devupstream:target"

SRC_URI:class-devupstream = "git://git.phytec.de/u-boot-stm32mp;protocol=git;branch=${U_BOOT_VERSION}-phy"
SRCREV:class-devupstream = "73713d8fb455ac067799fc1f9bbee0343b9143a4"

# ---------------------------------
# Configure default preference to manage dynamic selection between tarball and github
# ---------------------------------
STM32MP_SOURCE_SELECTION ?= "tarball"

DEFAULT_PREFERENCE = "${@bb.utils.contains('STM32MP_SOURCE_SELECTION', 'git.phytec', '-1', '1', d)}"

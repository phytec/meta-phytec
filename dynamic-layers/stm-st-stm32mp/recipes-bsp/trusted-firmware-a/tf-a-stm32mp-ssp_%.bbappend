FILESEXTRAPATHS_prepend := "${THISDIR}/tf-a-stm32mp:"
FILESEXTRAPATHS_prepend := "${THISDIR}/tf-a-stm32mp-ssp:"

SRC_URI += " \
    file://0001-v2.2-stm32mp-phy1-DEVICETREE.patch \
    file://0099-v2.2-stm32mp-r2.1-st-update.patch \
    file://0100-v2.2-stm32mp-ssp-phy1.patch \
    "

# ---------------------------------
# Configure default preference to manage dynamic selection between tarball and github
# ---------------------------------
STM32MP_SOURCE_SELECTION ?= "tarball"

DEFAULT_PREFERENCE = "${@bb.utils.contains('STM32MP_SOURCE_SELECTION', 'git.phytec', '-1', '1', d)}"

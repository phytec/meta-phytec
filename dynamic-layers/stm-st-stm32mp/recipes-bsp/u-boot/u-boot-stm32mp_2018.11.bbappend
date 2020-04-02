FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://0009-ARM-v2018.11-stm32mp-r2-add-phycore-stm32mp1xx-alpha1-machine.patch \
    file://0010-u-boot-stm32mp1xx-alpha2.patch \
    "

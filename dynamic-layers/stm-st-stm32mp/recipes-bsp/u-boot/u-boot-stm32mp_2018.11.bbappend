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
    file://0024-ARM-v2018.11-stm32mp-r4-debug.patch \
    file://0025-ARM-v2018.11-stm32mp-r4-ref-clk.patch \
"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:phygate-tauri-l-imx8mm-2 = "\
    file://0001-plat-imx8mm-disable-uart4-assigned-to-arm-cortex-m4.patch \
"
EXTRA_OEMAKE:append:mx8mm-generic-bsp = " BL32_BASE=0x56000000"

FILESEXTRAPATHS:prepend := "${THISDIR}/optee-os:"
SRC_URI += " \
    file://0001-plat-imx-add-phytec-imx8mm-based-boards.patch \
    file://0002-core-imx-add-imx8mp-libra-fpsc.patch \
    file://0003-core-imx-mx8mp_phyboard_pollux-reduce-DDR_SIZE.patch \
"

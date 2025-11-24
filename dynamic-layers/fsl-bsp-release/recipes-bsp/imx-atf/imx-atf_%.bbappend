FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:mx95-nxp-bsp = "\
    file://0001-plat-imx95-Make-UART-BASE-ADDR-configurable.patch\
    file://0001-plat-imx95-Assign-GPIO1-pins-to-non-secure-world-by-.patch\
"

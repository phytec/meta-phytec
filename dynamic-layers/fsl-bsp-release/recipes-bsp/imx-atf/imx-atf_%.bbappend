FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:imx95-libra-fpsc-1 = "\
    file://0001-plat-imx95-Make-UART-BASE-ADDR-configurable.patch\
"

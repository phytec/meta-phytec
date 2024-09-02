FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append:mx8mp-mainline-bsp = "\
        file://0001-fix-imx8mp-uncondtionally-enable-only-the-USB-power-.patch \
"

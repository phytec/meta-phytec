FILESEXTRAPATHS:prepend := "${THISDIR}/features:"

SRC_URI:append:mx8m-nxp-bsp:update = " \
    file://default-env.cfg \
"

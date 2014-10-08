require common/recipes-bsp/barebox/barebox.inc
require server.inc

FILESEXTRAPATHS_prepend_ti33x := "${THISDIR}/defconfigs:${THISDIR}/defconfigs/features:${THISDIR}/env:"

SRC_URI_append = " file://defconfig file://environment"

BRANCH = "v2014.10.0-phy"
PV = "v2014.10.0-phy-git${SRCPV}"

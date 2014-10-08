require common/recipes-bsp/barebox/barebox-ipl.inc
require server.inc

FILESEXTRAPATHS_prepend_ti33x := "${THISDIR}/ipl-defconfigs:${THISDIR}/defconfigs/features:"

SRC_URI_append = " file://defconfig"

BRANCH = "v2014.10.0-phy"
PV = "v2014.10.0-phy-git${SRCPV}"

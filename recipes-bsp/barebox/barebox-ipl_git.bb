require common/recipes-bsp/barebox/barebox-ipl.inc
require server.inc

FILESEXTRAPATHS_prepend_ti33x := "${THISDIR}/ipl-defconfigs:${THISDIR}/defconfigs/features:"

SRC_URI += "file://defconfig"

# phyflex-am335x-2013-01
BRANCH_phyflex-am335x-1 = "v2014.08.0-phy"
#PV_phyflex-am335x-1 = "v2014.08.0-phy-git${SRCPV}"

# phycore-am335x-2012-01
BRANCH_phycore-am335x-1 = "v2014.08.0-phy"
#PV_phycore-am335x-1 = "v2014.08.0-phy-git${SRCPV}"

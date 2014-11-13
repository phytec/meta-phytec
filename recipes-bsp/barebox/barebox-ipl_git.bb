require common/recipes-bsp/barebox/barebox-ipl.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:${THISDIR}/barebox/features:"

SRC_URI = "git://git.phytec.de/barebox;branch=${BRANCH}"

# floating revision
SRCREV = "${AUTOREV}"
S = "${WORKDIR}/git"
COMPATIBLE_MACHINE_ti33x = "(ti33x)"

BRANCH = "v2014.10.0-phy"
PV = "v2014.10.0-phy-git${SRCPV}"

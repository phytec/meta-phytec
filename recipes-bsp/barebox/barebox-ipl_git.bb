inherit buildinfo
require common/recipes-bsp/barebox/barebox-ipl.inc

FILESEXTRAPATHS_prepend := "${THISDIR}:${THISDIR}/barebox/features:"

GIT_URL = "git://git.phytec.de/barebox"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

# tag revision (NOTE: Keep TAG, PV and SRCREV in sync!)
BRANCH = "v2015.02.0-phy"
TAG = "v2015.02.0-phy-git${SRCPV}"
# Still using HEAD of branch
SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git"

BAREBOX_LOCALVERSION = "-${BSP_VERSION}"

COMPATIBLE_MACHINE = "(ti33x)"

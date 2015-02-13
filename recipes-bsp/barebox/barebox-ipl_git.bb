inherit buildinfo
require common/recipes-bsp/barebox/barebox-ipl.inc

FILESEXTRAPATHS_prepend := "${THISDIR}:${THISDIR}/barebox/features:"

GIT_URL = "git://git.phytec.de/barebox"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

# tag revision (NOTE: Keep TAG, PV and SRCREV in sync!)
BRANCH = "v2014.10.0-phy"
TAG = "v2014.10.0-phy1"
SRCREV = "8b015cd0815904b5c20ea5130884fdcea3344439"
#SRCREV = "${TAG}"
PV = "${TAG}"

# use this for building the HEAD of the git branch 
#SRCREV = "${AUTOREV}"
#PV = "v2014.10.0-phy-git${SRCPV}"

S = "${WORKDIR}/git"

BAREBOX_LOCALVERSION = "-${BSP_VERSION}"

COMPATIBLE_MACHINE = "(ti33x)"

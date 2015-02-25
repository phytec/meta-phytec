inherit buildinfo
require common/recipes-bsp/barebox/barebox.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}/features:${THISDIR}:"

GIT_URL = "git://git.phytec.de/${PN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
SRC_URI_append = " \
    file://commonenv \
    file://environment \
    file://targettools.cfg \
"

# tag revision (NOTE: Keep TAG, PV and SRCREV in sync!)
BRANCH = "v2015.02.0-phy"
TAG = "v2015.02.0-phy-git${SRCPV}"
# Still using HEAD of branch
SRCREV = "${AUTOREV}"
PV = "${TAG}"

S = "${WORKDIR}/git"

BAREBOX_LOCALVERSION = "-${BSP_VERSION}"

COMPATIBLE_MACHINE = "(ti33x)"

inherit buildinfo
require common/recipes-bsp/barebox/barebox.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/defconfigs:${THISDIR}/defconfigs/features:${THISDIR}/env:"

# tag revision (NOTE: Keep TAG, PV and SRCREV in sync!)
BRANCH = "v2014.11.0-phy"
TAG = "v2014.11.0-phy2"
SRCREV = "57b87aedbf0b6ae0eb0b858dd0c83411097c777a"
#SRCREV = "${TAG}"
PV = "${TAG}"

GIT_URL = "git://git.phytec.de/${PN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
SRC_URI_append = " \
    file://commonenv \
    file://environment \
"
S = "${WORKDIR}/git"

BAREBOX_LOCALVERSION = "-${BSP_VERSION}"

COMPATIBLE_MACHINE = "(mx6)"

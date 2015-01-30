require common/recipes-bsp/barebox/barebox.inc
FILESEXTRAPATHS_prepend := "${THISDIR}/defconfigs:${THISDIR}/defconfigs/features:${THISDIR}/env:"

SRC_URI = "git://git.phytec.de/barebox;branch=${BRANCH}"
SRC_URI_append = " \
    file://commonenv \
    file://environment \
"
S = "${WORKDIR}/git"

BRANCH = "v2014.11.0-phy"
PV = "v2014.11.0-phy2"
# TAG = ${PV}
SRCREV = "57b87aedbf0b6ae0eb0b858dd0c83411097c777a"

BAREBOX_LOCALVERSION = "-${BSP_VERSION}"

COMPATIBLE_MACHINE_mx6 = "(mx6)"

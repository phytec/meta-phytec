require common/recipes-bsp/barebox/barebox.inc
FILESEXTRAPATHS_prepend := "${THISDIR}/defconfigs:${THISDIR}/defconfigs/features:${THISDIR}/env:"

SRC_URI = "git://git.phytec.de/barebox;branch=${BRANCH}"
SRC_URI_append = " \
    file://commonenv \
    file://environment \
"
S = "${WORKDIR}/git"

BAREBOX_BIN_SYMLINK = "barebox-${MACHINE}.bin"

# floating revision
#SRCREV = "${AUTOREV}"
BRANCH = "v2014.11.0-phy"
#PV = "v2014.11.0-phy-git${SRCPV}"
PV = "v2014.11.0-phy1"
# TAG = ${PV}
SRCREV = "d0ad2e2a1a714b20fe13f52cabb099199432f505"
BAREBOX_LOCALVERSION = "-${BSP_VERSION}"

COMPATIBLE_MACHINE_mx6 = "(mx6)"

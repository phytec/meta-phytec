require common/recipes-bsp/barebox/barebox-ipl.inc

FILESEXTRAPATHS_prepend := "${THISDIR}:${THISDIR}/barebox/features:"

SRC_URI = "git://git.phytec.de/barebox;branch=${BRANCH}"

# floating revision
#SRCREV = "${AUTOREV}"
S = "${WORKDIR}/git"
COMPATIBLE_MACHINE_ti33x = "(ti33x)"

BRANCH = "v2014.10.0-phy"
#PV = "v2014.10.0-phy-git${SRCPV}"
BAREBOX_LOCALVERSION = "-${BSP_VERSION}"
PV = "v2014.10.0-phy1"
# TAG = ${PV}
SRCREV = "8b015cd0815904b5c20ea5130884fdcea3344439"

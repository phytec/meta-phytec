require common/recipes-bsp/barebox/barebox.inc
FILESEXTRAPATHS_prepend := "${THISDIR}/defconfigs:${THISDIR}/defconfigs/features:${THISDIR}/env:"

SRC_URI = " \
  git://git.phytec.de/barebox;branch=${BRANCH} \
"
#SRC_URI += "file://defconfig"
#SRC_URI += "file://environment"

S = "${WORKDIR}/git"

BAREBOX_BIN_SYMLINK = "barebox-${MACHINE}.bin"

# floating revision
SRCREV = "${AUTOREV}"
BRANCH = "v2014.09.0-phy"
PV = "v2014.09.0-phy-git${SRCPV}"

COMPATIBLE_MACHINE_mx6 = "(mx6)"

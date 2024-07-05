inherit kernel
inherit phygittag buildinfo kconfig
include linux-common.inc
include linux-barebox-dt-overlays.inc

GIT_URL = "git://github.com/phytec/linux-phytec.git;protocol=https"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

PR = "${INC_PR}.0"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "bd3d95ea50372b31bae094f8eea5432281f358c8"

S = "${WORKDIR}/git"

INTREE_DEFCONFIG = "am335x_phytec_defconfig"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "|phyboard-regor-am335x-1"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-1"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-2"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-3"
COMPATIBLE_MACHINE .= "|phyboard-wega-r2-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-am335x-2"
COMPATIBLE_MACHINE .= "|phycore-am335x-3"
COMPATIBLE_MACHINE .= "|phycore-am335x-4"
COMPATIBLE_MACHINE .= "|phycore-am335x-5"
COMPATIBLE_MACHINE .= "|phycore-emmc-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-2"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-3"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-4"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-5"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-6"
COMPATIBLE_MACHINE .= ")$"

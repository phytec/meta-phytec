inherit phygittag
inherit buildinfo
require common/recipes-bsp/barebox/barebox.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/defconfigs:${THISDIR}/defconfigs/features:${THISDIR}/env:"

GIT_URL = "git://git.phytec.de/${PN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
SRC_URI_append = " \
    file://commonenv \
    file://environment \
"
S = "${WORKDIR}/git"

# NOTE: Keep version in filename in sync with commit id!
SRCREV = "57b87aedbf0b6ae0eb0b858dd0c83411097c777a"

BAREBOX_LOCALVERSION = "-${BSP_VERSION}"

COMPATIBLE_MACHINE  =  "phyflex-imx6-1"
COMPATIBLE_MACHINE .= "|phyflex-imx6-2"
COMPATIBLE_MACHINE .= "|phycard-imx6-1"
COMPATIBLE_MACHINE .= "|phyboard-alcor-imx6-1"
COMPATIBLE_MACHINE .= "|phyboard-subra-imx6-1"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-1"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-2"

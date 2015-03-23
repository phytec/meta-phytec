inherit phygittag
inherit buildinfo
require common/recipes-bsp/barebox/barebox-ipl.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/barebox/features:"

GIT_URL = "git://git.phytec.de/barebox"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
S = "${WORKDIR}/git"
BAREBOX_LOCALVERSION = "-${BSP_VERSION}"

SRCREV = "c836eb8da28c8c088c61736c75e85947f8217987"

COMPATIBLE_MACHINE = "beagleboneblack-1"
COMPATIBLE_MACHINE .= "|phyboard-maia-am335x-1"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-1"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-2"
COMPATIBLE_MACHINE .= "|phycore-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-am335x-2"
COMPATIBLE_MACHINE .= "|phyflex-am335x-1"

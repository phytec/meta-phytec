# Copyright (C) 2015 PHYTEC Messtechnik GmbH,
# Author: Stefan Christ <s.christ@phytec.de>

inherit phygittag
inherit buildinfo
require common/recipes-bsp/barebox/barebox.inc

FILESEXTRAPATHS_prepend = "${THISDIR}/defconfigs:${THISDIR}/defconfigs/features:"
FILESEXTRAPATHS_prepend = "${THISDIR}/env-2015.02.0-phy:"

GIT_URL = "git://git.phytec.de/${PN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
SRC_URI_append = " \
    file://commonenv \
    file://environment \
"
S = "${WORKDIR}/git"

PR = "r0"

# NOTE: Keep version in filename in sync with commit id!
SRCREV = "b07f85c089be1e01184047e29a75529851d817da"

BAREBOX_LOCALVERSION = "-${BSP_VERSION}"

COMPATIBLE_MACHINE  =  "phyflex-imx6-1"
COMPATIBLE_MACHINE .= "|phyflex-imx6-2"
COMPATIBLE_MACHINE .= "|phycard-imx6-1"
COMPATIBLE_MACHINE .= "|phyboard-alcor-imx6-1"
COMPATIBLE_MACHINE .= "|phyboard-subra-imx6-1"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-1"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-2"

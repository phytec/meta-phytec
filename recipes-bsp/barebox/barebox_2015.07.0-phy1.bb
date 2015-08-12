# Copyright (C) 2015 PHYTEC Messtechnik GmbH,
# Author: Stefan Christ <s.christ@phytec.de>

inherit phygittag
inherit buildinfo
require recipes-bsp/barebox/barebox.inc

FILESEXTRAPATHS_prepend = "${THISDIR}/defconfigs:${THISDIR}/defconfigs/features:"
FILESEXTRAPATHS_prepend = "${THISDIR}/env-2015.07.0-phy:"

GIT_URL = "git://git.phytec.de/${PN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
SRC_URI_append = " \
    file://no-blspec.cfg \
    file://commonenv \
"
SRC_URI_append_phyboard-mira-imx6-3 = " file://environment"
SRC_URI_append_phyboard-mira-imx6-4 = " file://environment"

S = "${WORKDIR}/git"

PR = "${INC_PR}.0"

# NOTE: Keep version in filename in sync with commit id!
SRCREV = "acf66d87eeb4ec485617e2c3e1fc3c4b5719b853"

COMPATIBLE_MACHINE = "phyboard-mira-imx6-3"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-4"

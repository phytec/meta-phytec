# Copyright (C) 2019 PHYTEC Messtechnik GmbH,
# Author: Teresa Remmet <t.remmet@phytec.de>

inherit phygittag
inherit buildinfo
include linux-common.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

BRANCH = "v4.19.35_1.0.0-phy"
GIT_URL = "git://git.phytec.de/${PN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
PR = "${INC_PR}.0"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "41702ab06483d2066c0cf700e2ab78305ee511d8"

S = "${WORKDIR}/git"

INTREE_DEFCONFIG = "defconfig"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "phyboard-polis-imx8mm-1"
COMPATIBLE_MACHINE .= "|phyboard-polis-imx8mm-2"
COMPATIBLE_MACHINE .= ")$"

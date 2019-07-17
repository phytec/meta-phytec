# Copyright (C) 2018 PHYTEC Messtechnik GmbH,
# Author: Christian Hemp <c.hemp@phytec.de>

inherit phygittag
inherit buildinfo
include linux-common.inc

GIT_URL = "git://git.phytec.de/${PN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

PR = "${INC_PR}.0"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "1174895122027b197dd5789b6ee21af297746af4"

S = "${WORKDIR}/git"

INTREE_DEFCONFIG = "defconfig"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "phycore-imx8-1"
COMPATIBLE_MACHINE .= ")$"

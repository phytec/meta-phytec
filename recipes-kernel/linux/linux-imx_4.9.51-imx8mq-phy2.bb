# Copyright (C) 2017 PHYTEC Messtechnik GmbH,
# Author: Christian Hemp <c.hemp@phytec.de>

inherit phygittag
inherit buildinfo
include linux-common.inc


GIT_URL = "git://git.phytec.de/${PN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

PR = "${INC_PR}.0"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "ba9dacc42788b47404f9f2933dcad74b66ce5491"

S = "${WORKDIR}/git"

INTREE_DEFCONFIG = "defconfig"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "phyboard-polaris-imx8m-2"
COMPATIBLE_MACHINE .= ")$"

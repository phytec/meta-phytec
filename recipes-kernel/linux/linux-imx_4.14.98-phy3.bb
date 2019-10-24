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
SRCREV = "9e6c6924f0312393db786cd78e859a420375b6c4"

S = "${WORKDIR}/git"

INTREE_DEFCONFIG = "defconfig"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "phycore-imx8-2"
COMPATIBLE_MACHINE .= "|phyboard-polaris-imx8m-2"
COMPATIBLE_MACHINE .= "|phyboard-polaris-imx8m-3"
COMPATIBLE_MACHINE .= ")$"

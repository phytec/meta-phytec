# Copyright (C) 2019 PHYTEC Messtechnik GmbH,
# Author: Teresa Remmet <t.remmet@phytec.de>

inherit phygittag
inherit buildinfo
include linux-common.inc

BRANCH = "v4.19.35_1.1.0-phy"
GIT_URL = "git://git.phytec.de/${PN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
PR = "${INC_PR}.0"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "b97df2add8c08d204f0ebf47be7c723430fd9d75"

S = "${WORKDIR}/git"

INTREE_DEFCONFIG = "defconfig imx8_phytec_distro.config imx8_phytec_platform.config"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "phyboard-polis-imx8mm-2"
COMPATIBLE_MACHINE .= "|phyboard-polis-imx8mm-3"
COMPATIBLE_MACHINE .= ")$"

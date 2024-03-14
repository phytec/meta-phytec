# Copyright (C) 2024 PHYTEC Messtechnik GmbH

inherit phygittag
inherit buildinfo
include linux-common.inc

GIT_URL = "git://github.com/phytec/linux-phytec.git;protocol=https"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

PR = "${INC_PR}.0"

SRCREV = "2da1b824a19ac245cf9cec36ebf6a76bbeb23839"

S = "${WORKDIR}/git"

INTREE_DEFCONFIG = "defconfig"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "phyboard-pollux-imx8mp-3"
COMPATIBLE_MACHINE .= ")$"

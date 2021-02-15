# Copyright (C) 2017 PHYTEC Messtechnik GmbH,
# Author: Stefan Riedmueller <s.riedmueller@phytec.de>

inherit phygittag
inherit buildinfo
include linux-common.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

GIT_URL = "git://git.phytec.de/${BPN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

PR = "${INC_PR}.0"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "1752213696ec6ca110ee0c5f4e0fb65d64c69b37"

S = "${WORKDIR}/git"

INTREE_DEFCONFIG = "imx_v6_v7_defconfig"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .=  "phyboard-segin-imx6ul-2"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-3"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-4"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-5"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-6"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-7"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-8"
COMPATIBLE_MACHINE .= ")$"

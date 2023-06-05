# Copyright (C) 2023 PHYTEC Messtechnik GmbH,
# Author: Daniel Schultz <d.schultz@phytec.de>

inherit phygittag
inherit buildinfo
inherit fsl-vivante-kernel-driver-handler
include recipes-kernel/linux/linux-common.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

BRANCH = "v5.4.24-phy"
GIT_URL = "git://stash.phytec.com/scm/pub/linux-phytec-fsl.git;protocol=https"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
PR = "${INC_PR}.0"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "bcc95ebdeef7ba0d318764ac2c0cb49c034e820f"

S = "${WORKDIR}/git"

INTREE_DEFCONFIG:mx8x-nxp-bsp = "imx8x_phycore_kit_defconfig"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "phycore-imx8x-1"
COMPATIBLE_MACHINE .= ")$"

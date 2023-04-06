# Copyright (C) 2023 PHYTEC Messtechnik GmbH,
# Author: Daniel Schultz <d.schultz@phytec.de>

inherit phygittag
inherit buildinfo
inherit fsl-vivante-kernel-driver-handler
include recipes-kernel/linux/linux-common.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

BRANCH = "v5.4.70_2.3.2-phy"
GIT_URL = "git://git.phytec.de/${BPN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
PR = "${INC_PR}.0"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "39689fcf1514e532b789cf968eb5268361ffc57f"

S = "${WORKDIR}/git"

INTREE_DEFCONFIG:mx7-nxp-bsp = "imx7_phyboard_zeta_defconfig"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "phyboard-zeta-imx7d-1"
COMPATIBLE_MACHINE .= ")$"

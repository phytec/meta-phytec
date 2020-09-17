# Copyright (C) 2018 PHYTEC Messtechnik GmbH,
# Author: Christian Hemp <c.hemp@phytec.de>

inherit phygittag
inherit buildinfo
inherit fsl-vivante-kernel-driver-handler
include recipes-kernel/linux/linux-common.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

GIT_URL = "git://git.phytec.de/${PN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
PR = "${INC_PR}.0"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "4e2c230cd0508185a8b4555fe5f187d091fb6698"

S = "${WORKDIR}/git"

INTREE_DEFCONFIG = "defconfig"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "phycore-imx8-2"
COMPATIBLE_MACHINE .= "|phyboard-polaris-imx8m-3"
COMPATIBLE_MACHINE .= ")$"

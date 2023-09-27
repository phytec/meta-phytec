# Copyright (C) 2023 PHYTEC Messtechnik GmbH,
# Author: Christoph Stoidner <c.stoidner@phytec.de>

inherit phygittag
inherit buildinfo
inherit fsl-vivante-kernel-driver-handler
include recipes-kernel/linux/linux-common.inc

BRANCH = "v6.1.22_2.0.0-phy"
GIT_URL = "git://git.phytec.de/${BPN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
PR = "${INC_PR}.0"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "586f264a6ff32e96c32a4cf0c14a58d2e98dbbd4"

S = "${WORKDIR}/git"

INTREE_DEFCONFIG:mx93-generic-bsp = "imx_v8_defconfig imx9_phytec_platform.config"

EXTRA_DTC_ARGS += "DTC_FLAGS=-@"
KERNEL_EXTRA_ARGS += "${EXTRA_DTC_ARGS}"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "phyboard-segin-imx93-1"
COMPATIBLE_MACHINE .= ")$"

# Copyright (C) 2023 PHYTEC Messtechnik GmbH,
# Author: Christoph Stoidner <c.stoidner@phytec.de>

inherit phygittag
inherit buildinfo
inherit fsl-vivante-kernel-driver-handler
include recipes-kernel/linux/linux-common.inc

BRANCH = "v6.1.36_2.1.0-phy"
GIT_URL = "git://git.phytec.de/${BPN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
PR = "${INC_PR}.0"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "080354f9e53bdade96b250162430becfa34b2137"

S = "${WORKDIR}/git"

INTREE_DEFCONFIG:mx93-generic-bsp = "imx_v8_defconfig imx9_phytec_distro.config imx9_phytec_platform.config"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "phyboard-segin-imx93-2"
COMPATIBLE_MACHINE .= ")$"

# Copyright (C) 2019 PHYTEC Messtechnik GmbH,
# Author: Teresa Remmet <t.remmet@phytec.de>

inherit phygittag
inherit buildinfo
inherit fsl-vivante-kernel-driver-handler
include recipes-kernel/linux/linux-common.inc

BRANCH = "v5.4.70_2.3.2-phy"
GIT_URL = "git://git.phytec.de/${BPN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
PR = "${INC_PR}.0"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "a27242055e5d8a4ede1984a42cd71e6a55ea9b6f"

S = "${WORKDIR}/git"

INTREE_DEFCONFIG = "imx_v8_defconfig imx8_phytec_distro.config imx8_phytec_platform.config"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "phyboard-polaris-imx8m-3"
COMPATIBLE_MACHINE .= "|phyboard-polaris-imx8m-4"
COMPATIBLE_MACHINE .= "|phyboard-polis-imx8mm-3"
COMPATIBLE_MACHINE .= "|phyboard-polis-imx8mm-4"
COMPATIBLE_MACHINE .= "|phyboard-polis-imx8mn-1"
COMPATIBLE_MACHINE .= "|phyboard-pollux-imx8mp-1"
COMPATIBLE_MACHINE .= "|phygate-tauri-l-imx8mm-1"
COMPATIBLE_MACHINE .= "|phygate-tauri-l-imx8mm-2"
COMPATIBLE_MACHINE .= "|phyboard-pollux-imx8mp-2"
COMPATIBLE_MACHINE .= "|phyboard-pollux-imx8mp-3"
COMPATIBLE_MACHINE .= ")$"

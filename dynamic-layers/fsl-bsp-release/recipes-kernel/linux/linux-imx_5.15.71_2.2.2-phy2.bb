# Copyright (C) 2022 PHYTEC Messtechnik GmbH,
# Author: Teresa Remmet <t.remmet@phytec.de>

inherit phygittag
inherit buildinfo
inherit fsl-vivante-kernel-driver-handler
include recipes-kernel/linux/linux-common.inc

BRANCH = "v5.15.71_2.2.2-phy"
GIT_URL = "git://git.phytec.de/${BPN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
PR = "${INC_PR}.0"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "34e1d41e8a34941094a9313ecead6b53bd1afd7c"

S = "${WORKDIR}/git"

INTREE_DEFCONFIG = "imx_v8_defconfig imx8_phytec_distro.config imx8_phytec_platform.config"
INTREE_DEFCONFIG:mx7-nxp-bsp = "imx7_phyboard_zeta_defconfig"

RDEPENDS:${KERNEL_PACKAGE_NAME}-base:mx8m-nxp-bsp = ""

module_conf_imx8-media-dev:mx8mp-nxp-bsp = "install imx8-media-dev /sbin/modprobe ar0521 ; /sbin/modprobe ar0144 ; /sbin/modprobe --ignore-install imx8-media-dev"

KERNEL_MODULE_PROBECONF:mx8mp-nxp-bsp += "imx8-media-dev"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "phyboard-polis-imx8mm-5"
COMPATIBLE_MACHINE .= "|phyboard-polis-imx8mm-4"
COMPATIBLE_MACHINE .= "|phyboard-polis-imx8mn-2"
COMPATIBLE_MACHINE .= "|phygate-tauri-l-imx8mm-2"
COMPATIBLE_MACHINE .= "|phyboard-pollux-imx8mp-3"
COMPATIBLE_MACHINE .= "|phyboard-zeta-imx7d-1"
COMPATIBLE_MACHINE .= ")$"

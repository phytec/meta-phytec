# Copyright (C) 2022 PHYTEC Messtechnik GmbH,
# Author: Teresa Remmet <t.remmet@phytec.de>

inherit kernel
inherit phygittag buildinfo kconfig
inherit fsl-vivante-kernel-driver-handler
include recipes-kernel/linux/linux-common.inc

BRANCH = "v5.15.71_2.2.2-phy"
GIT_URL = "git://git.phytec.de/${BPN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
PR = "${INC_PR}.0"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-imx-5.15:"
SRC_URI:append = " \
  ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'file://lxc.cfg', '', d)} \
  ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'file://oci.cfg', '', d)} \
  ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'file://systemd.cfg', '', d)} \
  ${@bb.utils.contains('DEBUG_BUILD', '1', 'file://debugging.cfg', '', d)} \
  ${@bb.utils.contains('MACHINE_FEATURES', 'tpm2', 'file://tpm2.cfg', '', d)} \
  ${@bb.utils.contains('MACHINE_FEATURES', 'caam', 'file://caam.cfg', '',   d)} \
"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "e9e8ba9a4a23122d15d525c1fec74fff1e61036d"


INTREE_DEFCONFIG = "imx_v8_defconfig imx8_phytec_distro.config imx8_phytec_platform.config"
INTREE_DEFCONFIG:mx7-nxp-bsp = "imx7_phyboard_zeta_defconfig"

RDEPENDS:${KERNEL_PACKAGE_NAME}-base:mx8m-nxp-bsp = ""

module_conf_imx8-media-dev:mx8mp-nxp-bsp = "install imx8-media-dev /sbin/modprobe ar0521 ; /sbin/modprobe ar0144 ; /sbin/modprobe --ignore-install imx8-media-dev"

KERNEL_MODULE_PROBECONF:mx8mp-nxp-bsp += "imx8-media-dev"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "phyboard-zeta-imx7d-1"
COMPATIBLE_MACHINE .= ")$"

# Copyright (C) 2023 PHYTEC Messtechnik GmbH,
# Author: Christoph Stoidner <c.stoidner@phytec.de>

inherit kernel
inherit phygittag buildinfo kconfig
inherit fsl-vivante-kernel-driver-handler
include recipes-kernel/linux/linux-common.inc

BRANCH = "v6.1.55_2.2.0-phy"
GIT_URL = "git://git.phytec.de/${BPN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
PR = "${INC_PR}.0"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-imx-6.1:"
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
SRCREV = "c85ecd8ea011162a4c69e01410ac2c2e7a228789"

S = "${WORKDIR}/git"

INTREE_DEFCONFIG:mx93-generic-bsp = "imx_v8_defconfig imx9_phytec_distro.config imx9_phytec_platform.config"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "phyboard-segin-imx93-2"
COMPATIBLE_MACHINE .= "|phyboard-nash-imx93-1"
COMPATIBLE_MACHINE .= ")$"

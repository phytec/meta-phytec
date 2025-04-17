# Copyright (C) 2024 PHYTEC Messtechnik GmbH

inherit kernel
inherit phygittag buildinfo kconfig kernel-deploy-oftree
include linux-common.inc

GIT_URL = "git://github.com/phytec/linux-phytec.git;protocol=https"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

PR = "${INC_PR}.0"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-phytec-6.6:"
SRC_URI:append = " \
  ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'file://lxc.cfg', '', d)} \
  ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'file://oci.cfg', '', d)} \
  ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'file://systemd.cfg', '', d)} \
  ${@bb.utils.contains('DEBUG_BUILD', '1', 'file://debugging.cfg', '', d)} \
  ${@bb.utils.contains('MACHINE_FEATURES', 'tpm2', 'file://tpm2.cfg', '', d)} \
  ${@bb.utils.contains('MACHINE_FEATURES', 'caam', 'file://caam.cfg', '',   d)} \
  file://mtd-partitioned-master.cfg \
  file://0001-tty-vt-conmakehash-Don-t-mention-the-full-path-of-th.patch \
  file://0001-lib-build_OID_registry-don-t-mention-the-full-path-o.patch \
  file://0001-video-logo-Drop-full-path-of-the-input-filename-in-g.patch \
"

SRCREV = "2da1b824a19ac245cf9cec36ebf6a76bbeb23839"

S = "${WORKDIR}/git"

INTREE_DEFCONFIG = "defconfig"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "phyboard-pollux-imx8mp-3"
COMPATIBLE_MACHINE .= ")$"

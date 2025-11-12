# Copyright (C) 2024 PHYTEC Messtechnik GmbH

inherit kernel kernel-yocto
inherit phygittag buildinfo kernel-deploy-oftree
include linux-common.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-phytec-6.6:"
GIT_URL = "git://github.com/phytec/linux-phytec.git;protocol=https"
GIT_URL:phynext = "git://git@git.phytec.de/linux-phytec-dev.git;protocol=ssh"
SRC_URI = " \
    ${GIT_URL};name=machine;branch=${BRANCH} \
    git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-6.6;destsuffix=${KMETA};protocol=https \
    ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'file://oci.scc', '', d)} \
    ${@bb.utils.contains('DEBUG_BUILD', '1', 'file://debugging.scc', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'caam', 'file://caam.scc', '', d)} \
    file://mtd-partitioned-master.scc \
    file://0001-tty-vt-conmakehash-Don-t-mention-the-full-path-of-th.patch \
    file://0001-video-logo-Drop-full-path-of-the-input-filename-in-g.patch \
    file://0001-lib-build_OID_registry-don-t-mention-the-full-path-o.patch \
"

SRCREV = "83799a53ea51213958009d2a76d1d150b7bb3fa0"
SRCREV_machine = "${SRCREV}"
SRCREV_meta ?= "5b185a8716c8c62dc1c7751e6d12f8b67f58274f"

PR = "${INC_PR}.0"

KMETA = "kernel-meta"
ARCH:aarch64 = "arm64"

KBUILD_DEFCONFIG = "defconfig"
KCONFIG_MODE = "alldefconfig"

KERNEL_FEATURES = " \
    mtd-partitioned-master.scc \
    cfg/systemd.scc \
    ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'cfg/lxc.scc oci.scc', '', d)} \
    ${@bb.utils.contains('DEBUG_BUILD', '1', 'debugging.scc', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'wifi', 'features/wifi/wifi-sdio.scc', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'bluetooth', 'features/bluetooth/bluetooth.scc', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'tpm2', 'features/tpm/tpm-2.0.scc', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'caam', 'caam.scc', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'pci', 'features/pci/pci.scc', '', d)} \
    ${KERNEL_EXTRA_FEATURES} \
"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "phyboard-pollux-imx8mp-3"
COMPATIBLE_MACHINE .= ")$"

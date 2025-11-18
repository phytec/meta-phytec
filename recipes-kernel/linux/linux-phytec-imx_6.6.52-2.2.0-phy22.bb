# Copyright (C) 2024 PHYTEC Messtechnik GmbH,

inherit kernel kernel-yocto
inherit phygittag kernel-deploy-oftree
include recipes-kernel/linux/linux-common.inc
require linux-phytec-fitimage.inc

SRCREV = "40f262836510fec2a5fdfa445a25adb7bb201d91"
SRCREV_machine = "${SRCREV}"
SRCREV_meta ?= "5cefbe3e2770576771fe59b611d3b5fcf5860a1f"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-phytec-6.6:"
GIT_URL = "git://github.com/phytec/linux-phytec-imx.git;name=machine;protocol=https"
GIT_URL:phynext = "git://git@git.phytec.de/linux-imx-phytec-dev.git;protocol=ssh"
SRC_URI = " \
        ${GIT_URL};branch=${BRANCH} \
"

SRC_URI:append = " git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-6.6;destsuffix=${KMETA};protocol=https \
                   file://0001-tty-vt-conmakehash-Don-t-mention-the-full-path-of-th.patch \
                   ${@bb.utils.contains("DEBUG_BUILD", "1", "file://debugging.scc", "", d)} \
                   file://mtd-partitioned-master.scc \
"

PR = "${INC_PR}.0"

S = "${WORKDIR}/git"

KMETA = "kernel-meta"
ARCH:aarch64 = "arm64"

KERNEL_EXTRA_FEATURES = "cfg/systemd.scc mtd-partitioned-master.scc"
KERNEL_FEATURES = "${KERNEL_EXTRA_FEATURES}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("DISTRO_FEATURES", "virtualization", " cfg/lxc.scc oci.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("DEBUG_BUILD", "1", " debugging.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("MACHINE_FEATURES", "wifi", " features/wifi/wifi-sdio.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("MACHINE_FEATURES", "bluetooth", " features/bluetooth/bluetooth.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("MACHINE_FEATURES", "tpm2", " features/tpm/tpm-2.0.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("MACHINE_FEATURES", "pci", " features/pci/pci.scc", "", d)}"

KBUILD_DEFCONFIG ?= "imx8_phytec_defconfig"
KBUILD_DEFCONFIG:mx91-nxp-bsp = "imx9_phytec_defconfig"
KBUILD_DEFCONFIG:mx93-nxp-bsp = "imx9_phytec_defconfig"
KBUILD_DEFCONFIG:mx95-nxp-bsp = "imx_v8_defconfig"
KCONFIG_MODE = "alldefconfig"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "phyboard-pollux-imx8mp-3"
COMPATIBLE_MACHINE .= "|phyboard-polis-imx8mm-5"
COMPATIBLE_MACHINE .= "|phygate-tauri-l-imx8mm-2"
COMPATIBLE_MACHINE .= "|phycore-imx8x-1"
COMPATIBLE_MACHINE .= "|phyboard-nash-imx93-1"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx91-1"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx93-2"
COMPATIBLE_MACHINE .= "|imx8mp-libra-fpsc-1"
COMPATIBLE_MACHINE .= "|imx95-libra-fpsc-1"
COMPATIBLE_MACHINE .= ")$"

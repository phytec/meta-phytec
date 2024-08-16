# Copyright (C) 2024 PHYTEC Messtechnik GmbH,

inherit kernel kernel-yocto
inherit phygittag
include recipes-kernel/linux/linux-common.inc

SRCREV_machine = "f7e985204589f2b991e9d7fb90840c1e34c5e513"
SRCREV_meta ?= "da275b53b13faafa834352e3f9dd3f91a2c03bb8"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-phytec-6.6:"
GIT_URL = "git://git.phytec.de/linux-imx;name=machine"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
SRC_URI:append = " git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-6.6;destsuffix=${KMETA};protocol=https \
                   file://oci.scc \
                   file://debugging.scc"

PR = "${INC_PR}.0"

S = "${WORKDIR}/git"

KMETA = "kernel-meta"
ARCH:aarch64 = "arm64"

KERNEL_EXTRA_FEATURES = "cfg/systemd.scc"
KERNEL_FEATURES = "${KERNEL_EXTRA_FEATURES}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("DISTRO_FEATURES", "virtualization", " cfg/lxc.scc oci.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("DISTRO_FEATURES", "preempt-rt", "preempt-rt.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("DEBUG_BUILD", "1", " debugging.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("MACHINE_FEATURES", "wifi", " features/wifi/wifi-sdio.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("MACHINE_FEATURES", "bluetooth", " features/bluetooth/bluetooth.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("MACHINE_FEATURES", "tpm2", " features/tpm/tpm-2.0.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("MACHINE_FEATURES", "pci", " features/pci/pci.scc", "", d)}"
# TODO move sound to MACHINE_FEATURES
KERNEL_FEATURES:append:mx8m-nxp-bsp = " cfg/sound.scc"

KBUILD_DEFCONFIG ?= "imx8_phytec_defconfig"
KCONFIG_MODE="alldefconfig"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "phyboard-pollux-imx8mp-3"
COMPATIBLE_MACHINE .= "|phyboard-polis-imx8mm-5"
COMPATIBLE_MACHINE .= "|phygate-tauri-l-imx8mm-2"
COMPATIBLE_MACHINE .= ")$"

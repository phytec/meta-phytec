inherit kernel kernel-yocto
inherit phygittag kernel-deploy-oftree
include recipes-kernel/linux/linux-common.inc
require linux-phytec-fitimage.inc

SRCREV = "cb3bcde8af1e0ca6f4d3e08197421ecb7151f4d8"
SRCREV_machine = "${SRCREV}"
SRCREV_meta ?= "83fff3acfc84814b4da0cdb2a63d608d376c3cdd"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-phytec-6.18:"
GIT_URL = "git://github.com/phytec/linux-phytec-imx.git;name=machine;protocol=https"
GIT_URL:phynext = "git://git@git.phytec.de/linux-phytec-imx-dev.git;protocol=ssh"
SRC_URI = " \
        ${GIT_URL};branch=${BRANCH} \
"

SRC_URI:append = " git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-6.18;destsuffix=${KMETA};protocol=https \
        ${@bb.utils.contains("DEBUG_BUILD", "1", "file://debugging.scc", "", d)} \
        file://mtd-partitioned-master.scc \
        ${@bb.utils.contains('MACHINE_FEATURES', 'lwb5p', 'file://lwb5p_backports.scc', '', d)} \
"

PR = "${INC_PR}.0"

S = "${WORKDIR}/git"

KMETA = "kernel-meta"
ARCH:aarch64 = "arm64"

KERNEL_EXTRA_FEATURES = "cfg/systemd.scc mtd-partitioned-master.scc"
KERNEL_FEATURES = "${KERNEL_EXTRA_FEATURES}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("DISTRO_FEATURES", "virtualization", " cfg/lxc.scc oci.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("DISTRO_FEATURES", "preempt-rt", "preempt-rt.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("DEBUG_BUILD", "1", " debugging.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("MACHINE_FEATURES", "wifi", " features/wifi/wifi-sdio.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("MACHINE_FEATURES", "bluetooth", " features/bluetooth/bluetooth.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("MACHINE_FEATURES", "tpm2", " features/tpm/tpm-2.0.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("MACHINE_FEATURES", "pci", " features/pci/pci.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("MACHINE_FEATURES", "lwb5p", " lwb5p_backports.scc", "", d)}"
KERNEL_FEATURES:remove = "${@bb.utils.contains("MACHINE_FEATURES", "lwb5p", "features/bluetooth/bluetooth.scc", "", d)}"
KERNEL_FEATURES:remove = "${@bb.utils.contains("MACHINE_FEATURES", "lwb5p", "features/wifi/wifi-sdio.scc", "", d)}"

KBUILD_DEFCONFIG ?= "imx8_phytec_defconfig"
KBUILD_DEFCONFIG:mx9-nxp-bsp = "imx9_phytec_defconfig"
KCONFIG_MODE = "alldefconfig"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "imx95-phyflex-libra-rdk-2"
COMPATIBLE_MACHINE .= ")$"

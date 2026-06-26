inherit kernel kernel-yocto
inherit phygittag buildinfo kernel-deploy-oftree
include recipes-kernel/linux/linux-common.inc
include linux-barebox-dt-overlays.inc

GIT_URL = "git://github.com/phytec/linux-phytec.git;protocol=https"
GIT_URL:phynext = "git://git@git.phytec.de/linux-phytec-dev.git;protocol=ssh"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

PR = "${INC_PR}.0"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-phytec-6.6:"
SRC_URI:append = " \
  git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-6.6;destsuffix=${KMETA};protocol=https \
  file://mtd-partitioned-master.cfg \
  file://0001-tty-vt-conmakehash-Don-t-mention-the-full-path-of-th.patch \
"
KERNEL_EXTRA_FEATURES = "cfg/systemd.scc"
KERNEL_FEATURES = "${KERNEL_EXTRA_FEATURES}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("DISTRO_FEATURES", "virtualization", " cfg/lxc.scc oci.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("DEBUG_BUILD", "1", " debugging.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("MACHINE_FEATURES", "wifi", " features/wifi/wifi-sdio.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("MACHINE_FEATURES", "bluetooth", " features/bluetooth/bluetooth.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("MACHINE_FEATURES", "tpm2", " features/tpm/tpm-2.0.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("MACHINE_FEATURES", "caam", " caam.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("MACHINE_FEATURES", "pci", " features/pci/pci.scc", "", d)}"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "79fa29e3583bdf25e692e87d930a48aa0fb3edfb"
SRCREV_machine = "${SRCREV}"
SRCREV_meta ?= "b1108273b878547b3d3281f21aba44a8c41ca741"

KMETA = "kernel-meta"

S = "${WORKDIR}/git"

KBUILD_DEFCONFIG = "imx6_phytec_defconfig"
KCONFIG_MODE = "alldefconfig"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .=  "phyboard-segin-imx6ul-2"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-3"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-4"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-5"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-6"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-7"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-8"

COMPATIBLE_MACHINE .= "|phygate-tauri-s-imx6ul-1"
COMPATIBLE_MACHINE .= ")$"

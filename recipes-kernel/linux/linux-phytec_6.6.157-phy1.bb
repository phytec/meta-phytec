inherit kernel kernel-yocto
inherit phygittag buildinfo kernel-deploy-oftree
require linux-common.inc
include linux-barebox-dt-overlays.inc

GIT_URL = "git://github.com/phytec/linux-phytec.git;protocol=https"
GIT_URL:phynext = "git://git@git.phytec.de/linux-phytec-dev.git;protocol=ssh"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

BRANCH = "v6.6.y-phy"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-phytec-6.6:"
SRC_URI:append = " \
  git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-6.6;destsuffix=${KMETA};protocol=https \
  file://mtd-partitioned-master.scc \
  file://0001-tty-vt-conmakehash-Don-t-mention-the-full-path-of-th.patch \
  file://0002-tools-use-basename-to-identify-file-in-gen-mach-types.patch \
"

KERNEL_EXTRA_FEATURES = "cfg/systemd.scc mtd-partitioned-master.scc"
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
SRCREV = "7be15139b7b8a312cca653bf980628c978274ca1"
SRCREV_machine = "${SRCREV}"
SRCREV_meta ?= "7d4cafa710da4e27a86cb015e50d91cf458af06f"

KMETA = "kernel-meta"


KBUILD_DEFCONFIG = "imx6_phytec_defconfig"
KCONFIG_MODE = "alldefconfig"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .=  "phyboard-mira-imx6-3"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-5"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-14"
COMPATIBLE_MACHINE .= ")$"

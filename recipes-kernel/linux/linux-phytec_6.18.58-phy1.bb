inherit kernel kernel-yocto
inherit phygittag buildinfo kernel-deploy-oftree
require linux-common.inc
include linux-barebox-dt-overlays.inc

GIT_URL = "git://github.com/phytec/linux-phytec.git;protocol=https"
GIT_URL:phynext = "git://git@git.phytec.de/linux-phytec-dev.git;protocol=ssh"
SRC_URI = " \
	${GIT_URL};name=machine;branch=${BRANCH} \
	git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-6.18;destsuffix=${KMETA};protocol=https \
	file://systemd.scc \
	file://mtd-partitioned-master.scc \
	${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'file://oci.scc', '', d)} \
	${@bb.utils.contains('MACHINE_FEATURES', 'caam', 'file://caam.scc', '', d)} \
"

BRANCH = "v6.18.y-phy"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-phytec-6.18:"

KERNEL_FEATURES = " \
    systemd.scc \
    mtd-partitioned-master.scc \
    ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'cfg/lxc.scc oci.scc', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'wifi', 'features/wifi/wifi-sdio.scc', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'bluetooth', 'features/bluetooth/bluetooth.scc', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'tpm2', 'features/tpm/tpm-2.0.scc', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'caam', 'caam.scc', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'pci', 'features/pci/pci.scc', '', d)} \
"

KMETA = "kernel-meta"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "a9a633f7d70d69b168e34d3ec397a4e82d88952e"
SRCREV_machine = "${SRCREV}"
SRCREV_meta ?= "5978c5f530b91cfb62213506ee6f157897432ba3"

KBUILD_DEFCONFIG = "imx6_phytec_defconfig"
KCONFIG_MODE = "alldefconfig"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .=  "phyboard-mira-imx6-3"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-5"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-14"
COMPATIBLE_MACHINE .= ")$"

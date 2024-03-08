inherit phygittag
inherit buildinfo
require recipes-kernel/linux/linux-common.inc
require recipes-kernel/linux/kernel-rdepends.inc

#Skip this recipe if DISTRO_FEATURES contains the PREEMPT-RT value and
# a kernel with real-time is desired
python () {
    if 'preempt-rt' in d.getVar("DISTRO_FEATURES"):
        raise bb.parse.SkipPackage("Disable 'preempt-rt' in DISTRO_FEATURES!")
}

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

GIT_URL = "git://github.com/phytec/linux-phytec-ti.git;protocol=https"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
SRC_URI_append_phyboard-izar-am68x-1 = " \
	file://eth-module.cfg \
"

PR = "${INC_PR}.0"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
# NOTE: Keep version of TI_LINUX_FW_SRCREV in sync, configured in
#       dynamic-layers/meta-ti/recipes-bsp/ti-linux-fw/ti-linux-fw-freeze.inc
SRCREV = "dbb359bcd016dadff4358fa9dcd0a8ff3f98cf7d"
S = "${WORKDIR}/git"

# Special configuration for remoteproc/rpmsg IPC modules
module_conf_rpmsg_client_sample = "blacklist rpmsg_client_sample"
module_conf_ti_k3_r5_remoteproc = "softdep ti_k3_r5_remoteproc pre: virtio_rpmsg_bus"
module_conf_ti_k3_dsp_remoteproc = "softdep ti_k3_dsp_remoteproc pre: virtio_rpmsg_bus"
KERNEL_MODULE_PROBECONF += "rpmsg_client_sample ti_k3_r5_remoteproc ti_k3_dsp_remoteproc"

# Drop kernel-devicetree, added by TI's kernel-rdepends.inc for k3 platforms
RDEPENDS_${KERNEL_PACKAGE_NAME}-base_remove_k3 = "kernel-devicetree"
RDEPENDS_${KERNEL_PACKAGE_NAME}-base_append_phyboard-izar-am68x-1 = " cnm-wave-fw"

EXTRA_DTC_ARGS += "DTC_FLAGS=-@"
KERNEL_EXTRA_ARGS += "LOADADDR=${UBOOT_ENTRYPOINT} \
                      ${EXTRA_DTC_ARGS}"

FILES_${KERNEL_PACKAGE_NAME}-devicetree += "/${KERNEL_IMAGEDEST}/*.itb"

# Provide oftree in rootfs /boot directory on am57xx
do_install_append_am57xx() {
    dtb=`normalize_dtb "${@get_oftree(d)}"`
    dtb_path=`get_real_dtb_path_in_kernel "$dtb"`
    install -m 0644 $dtb_path ${D}/${KERNEL_IMAGEDEST}/oftree
}
FILES_${KERNEL_PACKAGE_NAME}-devicetree_append_am57xx = " /${KERNEL_IMAGEDEST}/oftree"

# Add PRU Ethernet firmware to the rootfs for am57xx
RRECOMMENDS_${KERNEL_PACKAGE_NAME}-base_append_am57xx = " prueth-fw prusw-fw pruhsr-fw pruprp-fw"

RDEPENDS_kernel-modules_am57xx += "\
    cryptodev-module \
"

INTREE_DEFCONFIG = "phytec_ti_defconfig phytec_ti_platform.config"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .=  "phyboard-lyra-am62xx-1"
COMPATIBLE_MACHINE .=  "|phyboard-lyra-am62xx-2"
COMPATIBLE_MACHINE .=  "|phyboard-lyra-am62xx-3"
COMPATIBLE_MACHINE .=  "|phyboard-lyra-am62axx-1"
COMPATIBLE_MACHINE .=  "|phyboard-electra-am64xx-1"
COMPATIBLE_MACHINE .=  "|phyboard-electra-am64xx-2"
COMPATIBLE_MACHINE .=  "|phyboard-izar-am68x-1"
COMPATIBLE_MACHINE .=  "|phycore-am57xx-1"
COMPATIBLE_MACHINE .=  "|phycore-am57xx-2"
COMPATIBLE_MACHINE .=  "|phycore-am57xx-3"
COMPATIBLE_MACHINE .=  "|phycore-am57xx-4"
COMPATIBLE_MACHINE .=  "|phycore-am57xx-5"
COMPATIBLE_MACHINE .=  "|phycore-am57xx-6"
COMPATIBLE_MACHINE .= ")$"

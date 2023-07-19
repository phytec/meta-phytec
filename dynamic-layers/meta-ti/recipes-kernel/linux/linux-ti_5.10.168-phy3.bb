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
SRCREV = "81adb4b8910319515414fad820563c0a86af5460"
S = "${WORKDIR}/git"

# Special configuration for remoteproc/rpmsg IPC modules
module_conf_rpmsg_client_sample = "blacklist rpmsg_client_sample"
module_conf_ti_k3_r5_remoteproc = "softdep ti_k3_r5_remoteproc pre: virtio_rpmsg_bus"
module_conf_ti_k3_dsp_remoteproc = "softdep ti_k3_dsp_remoteproc pre: virtio_rpmsg_bus"
KERNEL_MODULE_PROBECONF += "rpmsg_client_sample ti_k3_r5_remoteproc ti_k3_dsp_remoteproc"

# Drop kernel-devicetree, added by TI's kernel-rdepends.inc
RDEPENDS_${KERNEL_PACKAGE_NAME}-base_remove = "kernel-devicetree"
RDEPENDS_${KERNEL_PACKAGE_NAME}-base_append_phyboard-izar-am68x-1 = " cnm-wave-fw"

EXTRA_DTC_ARGS += "DTC_FLAGS=-@"
KERNEL_EXTRA_ARGS += "LOADADDR=${UBOOT_ENTRYPOINT} \
                      ${EXTRA_DTC_ARGS}"

FILES_${KERNEL_PACKAGE_NAME}-devicetree += "/${KERNEL_IMAGEDEST}/*.itb"

INTREE_DEFCONFIG = "phytec_ti_defconfig phytec_ti_platform.config"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .=  "phyboard-lyra-am62xx-1"
COMPATIBLE_MACHINE .=  "|phyboard-lyra-am62xx-2"
COMPATIBLE_MACHINE .=  "|phyboard-lyra-am62xx-3"
COMPATIBLE_MACHINE .=  "|phyboard-lyra-am62axx-1"
COMPATIBLE_MACHINE .=  "|phyboard-electra-am64xx-1"
COMPATIBLE_MACHINE .=  "|phyboard-electra-am64xx-2"
COMPATIBLE_MACHINE .=  "|phyboard-izar-am68x-1"
COMPATIBLE_MACHINE .= ")$"

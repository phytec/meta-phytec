inherit phygittag
inherit buildinfo
require recipes-kernel/linux/linux-common.inc
require recipes-kernel/linux/kernel-rdepends.inc

#Skip this recipe if DISTRO_FEATURES contains the PREEMPT-RT value and
# a kernel with real-time is desired
python () {
    if 'preempt-rt' not in d.getVar("DISTRO_FEATURES"):
        raise bb.parse.SkipPackage("Enable 'preempt-rt' in DISTRO_FEATURES!")
}

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

GIT_URL = "git://github.com/phytec/linux-phytec-ti.git;protocol=https"
SRC_URI = " \
	${GIT_URL};branch=${BRANCH} \
	${@bb.utils.contains('MACHINE_FEATURES', 'lwb5p', 'file://disable-configs-for-lwb5p-backports.cfg', '', d)} \
"
SRC_URI:append:phyboard-izar-am68x-1 = " \
	file://eth-module.cfg \
"

PR = "${INC_PR}.0"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
# NOTE: Keep version of TI_LINUX_FW_SRCREV in sync, configured in
#       dynamic-layers/meta-ti-bsp/recipes-bsp/ti-linux-fw/ti-linux-fw-freeze.inc
SRCREV = "5560215162c2f07ed526808b9bf3f0f49d992e9c"
S = "${WORKDIR}/git"

# Special configuration for remoteproc/rpmsg IPC modules
module_conf_rpmsg_client_sample = "blacklist rpmsg_client_sample"
module_conf_ti:k3_r5_remoteproc = "softdep ti:k3_r5_remoteproc pre: virtio_rpmsg_bus"
module_conf_ti:k3_dsp_remoteproc = "softdep ti:k3_dsp_remoteproc pre: virtio_rpmsg_bus"
KERNEL_MODULE_PROBECONF += "rpmsg_client_sample ti:k3_r5_remoteproc ti:k3_dsp_remoteproc"

# Drop kernel-devicetree, added by TI's kernel-rdepends.inc
RDEPENDS:${KERNEL_PACKAGE_NAME}-base:remove = "kernel-devicetree"
RDEPENDS:${KERNEL_PACKAGE_NAME}-base:append:phyboard-izar-am68x-1 = " cnm-wave-fw"

EXTRA_DTC_ARGS += "DTC_FLAGS=-@"
KERNEL_EXTRA_ARGS += "LOADADDR=${UBOOT_ENTRYPOINT} \
                      ${EXTRA_DTC_ARGS}"

FILES:${KERNEL_PACKAGE_NAME}-devicetree += "/${KERNEL_IMAGEDEST}/*.itb"

INTREE_DEFCONFIG = "phytec_ti_defconfig phytec_ti_platform.config phytec_ti_rt.config"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .=  "phyboard-lyra-am62xx-1"
COMPATIBLE_MACHINE .=  "|phyboard-lyra-am62xx-2"
COMPATIBLE_MACHINE .=  "|phyboard-lyra-am62xx-3"
COMPATIBLE_MACHINE .=  "|phyboard-lyra-am62axx-1"
COMPATIBLE_MACHINE .=  "|phyboard-electra-am64xx-1"
COMPATIBLE_MACHINE .=  "|phyboard-izar-am68x-1"
COMPATIBLE_MACHINE .= ")$"

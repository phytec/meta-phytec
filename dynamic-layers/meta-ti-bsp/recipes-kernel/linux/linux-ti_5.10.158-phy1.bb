inherit phygittag
inherit buildinfo
require recipes-kernel/linux/linux-common.inc
require recipes-kernel/linux/kernel-rdepends.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

GIT_URL = "git://github.com/phytec/linux-phytec-ti.git;protocol=https"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

PR = "${INC_PR}.0"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "eac5cd6e17c182e5bd27393e3f80c140f32ebd79"
S = "${WORKDIR}/git"

# Special configuration for remoteproc/rpmsg IPC modules
module_conf_rpmsg_client_sample = "blacklist rpmsg_client_sample"
module_conf_ti_k3_r5_remoteproc = "softdep ti_k3_r5_remoteproc pre: virtio_rpmsg_bus"
module_conf_ti_k3_dsp_remoteproc = "softdep ti_k3_dsp_remoteproc pre: virtio_rpmsg_bus"
KERNEL_MODULE_PROBECONF += "rpmsg_client_sample ti_k3_r5_remoteproc ti_k3_dsp_remoteproc"

# Drop kernel-devicetree, added by TI's kernel-rdepends.inc
RDEPENDS_${KERNEL_PACKAGE_NAME}-base:remove = "kernel-devicetree"

EXTRA_DTC_ARGS += "DTC_FLAGS=-@"
KERNEL_EXTRA_ARGS += "LOADADDR=${UBOOT_ENTRYPOINT} \
                      ${EXTRA_DTC_ARGS}"

FILES:${KERNEL_PACKAGE_NAME}-devicetree += "/${KERNEL_IMAGEDEST}/*.itb"

INTREE_DEFCONFIG = "phytec_ti_defconfig phytec_ti_platform.config"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .=  "phyboard-lyra-am62xx-1"
COMPATIBLE_MACHINE .=  "|phyboard-lyra-am62xx-2"
COMPATIBLE_MACHINE .=  "|phyboard-electra-am64xx-1"
COMPATIBLE_MACHINE .= ")$"

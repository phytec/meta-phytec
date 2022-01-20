inherit phygittag
inherit buildinfo
include linux-common.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

GIT_URL = "git://github.com/phytec/linux-phytec-ti.git;protocol=https"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

PR = "${INC_PR}.0"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "c7e5b190598fae2625da185485ec6e3979001556"
S = "${WORKDIR}/git"

# Pull in the devicetree files into the rootfs
# Add run-time dependency for PRU firmware to the rootfs
RDEPENDS_${KERNEL_PACKAGE_NAME}-base_append = "\
    kernel-devicetree \
    prueth-fw-am65x-sr2 \
"

# Special configuration for remoteproc/rpmsg IPC modules
module_conf_rpmsg_client_sample = "blacklist rpmsg_client_sample"
module_conf_ti_k3_r5_remoteproc = "softdep ti_k3_r5_remoteproc pre: virtio_rpmsg_bus"
module_conf_ti_k3_dsp_remoteproc = "softdep ti_k3_dsp_remoteproc pre: virtio_rpmsg_bus"
KERNEL_MODULE_PROBECONF += "rpmsg_client_sample ti_k3_r5_remoteproc ti_k3_dsp_remoteproc"

EXTRA_DTC_ARGS += "DTC_FLAGS=-@"
KERNEL_EXTRA_ARGS += "LOADADDR=${UBOOT_ENTRYPOINT} \
                      ${EXTRA_DTC_ARGS}"

FILES_${KERNEL_PACKAGE_NAME}-devicetree += "/${KERNEL_IMAGEDEST}/*.itb"

INTREE_DEFCONFIG = "am64xx_phytec_defconfig"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .=  "phyboard-electra-am64xx-1"
COMPATIBLE_MACHINE .= ")$"

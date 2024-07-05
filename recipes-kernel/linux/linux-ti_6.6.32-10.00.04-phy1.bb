inherit kernel kernel-yocto
inherit phygittag buildinfo
require recipes-kernel/linux/linux-common-non-rt.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-ti-6.6:${THISDIR}/linux-phytec-6.6:"
GIT_URL = "git://github.com/phytec/linux-phytec-ti.git;protocol=https"
SRC_URI = " \
	${GIT_URL};branch=${BRANCH} \
        file://lxc.scc \
        file://oci.scc \
"
SRC_URI:append:phyboard-izar-am68x-1 = " file://eth-module.scc"

KERNEL_FEATURES = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'lxc.scc oci.scc', '', d)} \
"
KERNEL_FEATURES:append:phyboard-izar-am68x-1 = " eth-module.scc"

KBUILD_DEFCONFIG ?= "phytec_ti_defconfig"
KCONFIG_MODE="alldefconfig"

PR = "${INC_PR}.0"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
# NOTE: Keep version of TI_LINUX_FW_SRCREV in sync, configured in
#       dynamic-layers/meta-ti-bsp/recipes-bsp/ti-linux-fw/ti-linux-fw-freeze.inc
SRCREV = "312e23dfbf24d5415698e38696f7a1936fbf3a69"
S = "${WORKDIR}/git"

# Special configuration for remoteproc/rpmsg IPC modules
module_conf_rpmsg_client_sample = "blacklist rpmsg_client_sample"
module_conf_ti_k3_r5_remoteproc = "softdep ti_k3_r5_remoteproc pre: virtio_rpmsg_bus"
module_conf_ti_k3_dsp_remoteproc = "softdep ti_k3_dsp_remoteproc pre: virtio_rpmsg_bus"
KERNEL_MODULE_PROBECONF += "rpmsg_client_sample ti_k3_r5_remoteproc ti_k3_dsp_remoteproc"

EXTRA_DTC_ARGS += "DTC_FLAGS=-@"
KERNEL_EXTRA_ARGS += "LOADADDR=${UBOOT_ENTRYPOINT} \
                      ${EXTRA_DTC_ARGS}"

# Provide oftree in rootfs /boot directory on am57xx
do_install:append:am57xx() {
	dtb=`normalize_dtb "${@get_oftree(d)}"`
	dtb_path=`get_real_dtb_path_in_kernel "$dtb"`
	install -m 0644 $dtb_path ${D}/${KERNEL_IMAGEDEST}/oftree
}
FILES:${KERNEL_PACKAGE_NAME}-devicetree:append:am57xx = " /${KERNEL_IMAGEDEST}/oftree"


KERNEL_LOCALVERSION = "-${@legitimize_package_name(d.getVar('DISTRO_VERSION'))}"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .=  "phyboard-lyra-am62xx-2"
COMPATIBLE_MACHINE .= "|phyboard-lyra-am62xx-3"

COMPATIBLE_MACHINE .= "|phyboard-lyra-am62axx-1"
COMPATIBLE_MACHINE .= "|phyboard-lyra-am62axx-2"

COMPATIBLE_MACHINE .= "|phyboard-electra-am64xx-1"
COMPATIBLE_MACHINE .= "|phyboard-electra-am64xx-2"

COMPATIBLE_MACHINE .= "|phyboard-izar-am68x-1"
COMPATIBLE_MACHINE .= "|phyboard-izar-am68x-2"
COMPATIBLE_MACHINE .= "|phyboard-izar-am68x-3"

COMPATIBLE_MACHINE .= "|phycore-am57xx-1"
COMPATIBLE_MACHINE .= "|phycore-am57xx-2"
COMPATIBLE_MACHINE .= "|phycore-am57xx-3"
COMPATIBLE_MACHINE .= "|phycore-am57xx-4"
COMPATIBLE_MACHINE .= "|phycore-am57xx-5"
COMPATIBLE_MACHINE .= "|phycore-am57xx-6"
COMPATIBLE_MACHINE .= ")$"

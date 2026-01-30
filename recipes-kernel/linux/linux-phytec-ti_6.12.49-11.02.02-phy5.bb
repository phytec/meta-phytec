inherit kernel kernel-yocto
inherit phygittag buildinfo kernel-deploy-oftree
require linux-common.inc
require linux-phytec-fitimage.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-phytec-ti-6.12:${THISDIR}/linux-phytec-6.12:"
GIT_URL = "git://github.com/phytec/linux-phytec-ti.git;protocol=https"
GIT_URL:phynext = "git://git@github.com/phytec/linux-phytec-ti-dev.git;protocol=ssh"
SRC_URI = " \
	${GIT_URL};branch=${BRANCH} \
	file://systemd.scc \
	${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'file://lxc.scc', '', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'file://oci.scc', '', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'preempt-rt', 'file://preempt-rt.scc', '', d)} \
"

SRC_URI:append:am62lx-libra = " \
    file://tmp102-built-in.cfg \
"

KERNEL_FEATURES = " \
    systemd.scc \
    ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'lxc.scc oci.scc', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'preempt-rt', 'preempt-rt.scc', '', d)} \
"

KBUILD_DEFCONFIG ?= "phytec_ti_defconfig"
KCONFIG_MODE = "alldefconfig"

PR = "${INC_PR}.0"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
# NOTE: Keep version of TI_LINUX_FW_SRCREV in sync, configured in
#       dynamic-layers/meta-ti-bsp/recipes-bsp/ti-linux-fw/ti-linux-fw-freeze.inc
SRCREV = "30ff01cd5225acc7e9a9b5b3c5e5f215bca30e3e"

# Special configuration for remoteproc/rpmsg IPC modules
module_conf_rpmsg_client_sample = "blacklist rpmsg_client_sample"
module_conf_ti_k3_r5_remoteproc = "softdep ti_k3_r5_remoteproc pre: virtio_rpmsg_bus"
module_conf_ti_k3_dsp_remoteproc = "softdep ti_k3_dsp_remoteproc pre: virtio_rpmsg_bus"
KERNEL_MODULE_PROBECONF += "rpmsg_client_sample ti_k3_r5_remoteproc ti_k3_dsp_remoteproc"

# Blacklist the upstream/PowerVR driver
module_conf_powervr = "blacklist powervr"
KERNEL_MODULE_PROBECONF += "powervr"

KERNEL_VERSION_SANITY_SKIP = "1"

EXTRA_DTC_ARGS += "DTC_FLAGS=-@"
KERNEL_EXTRA_ARGS += "LOADADDR=${UBOOT_ENTRYPOINT} \
                      ${EXTRA_DTC_ARGS}"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .=  "phyboard-lyra"
COMPATIBLE_MACHINE .= "|phyboard-electra"
COMPATIBLE_MACHINE .= "|phyboard-lynx"
COMPATIBLE_MACHINE .= "|phyboard-rigel"
COMPATIBLE_MACHINE .= "|am62lx-libra"
COMPATIBLE_MACHINE .= "|phyboard-izar"
COMPATIBLE_MACHINE .= ")$"

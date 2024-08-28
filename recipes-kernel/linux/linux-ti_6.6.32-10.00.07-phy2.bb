inherit kernel kernel-yocto
inherit phygittag buildinfo
require recipes-kernel/linux/linux-common.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-ti-6.6:${THISDIR}/linux-phytec-6.6:"
GIT_URL = "git://github.com/phytec/linux-phytec-ti.git;protocol=https"
SRC_URI = " \
	${GIT_URL};branch=${BRANCH} \
	file://systemd.scc \
	file://phytec_ti_platform.scc \
	${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'file://lxc.scc', '', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'file://oci.scc', '', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'preempt-rt', 'file://preempt-rt.scc', '', d)} \
"

# Apply rt patch in case of preempt-rt
RT_PATCH = "${KERNELORG_MIRROR}/linux/kernel/projects/rt/6.6/older/patch-6.6.32-rt32.patch.xz;name=rt-patch"
SRC_URI:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'preempt-rt', "${RT_PATCH}", '', d)} \
"
SRC_URI[rt-patch.sha256sum] = "058b73df1634b54d3b0ca7d7d5998b9ecc1c69fff935c3bfc3f0f0279af96243"

KERNEL_FEATURES = " \
    systemd.scc \
    phytec_ti_platform.scc \
    ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'lxc.scc oci.scc', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'preempt-rt', 'preempt-rt.scc', '', d)} \
"

KBUILD_DEFCONFIG ?= "phytec_ti_defconfig"
KCONFIG_MODE="alldefconfig"

PR = "${INC_PR}.0"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
# NOTE: Keep version of TI_LINUX_FW_SRCREV in sync, configured in
#       dynamic-layers/meta-ti-bsp/recipes-bsp/ti-linux-fw/ti-linux-fw-freeze.inc
SRCREV = "4df49276afa58fbebc50092035d8367b8b4c2540"
S = "${WORKDIR}/git"

# Special configuration for remoteproc/rpmsg IPC modules
module_conf_rpmsg_client_sample = "blacklist rpmsg_client_sample"
module_conf_ti_k3_r5_remoteproc = "softdep ti_k3_r5_remoteproc pre: virtio_rpmsg_bus"
module_conf_ti_k3_dsp_remoteproc = "softdep ti_k3_dsp_remoteproc pre: virtio_rpmsg_bus"
KERNEL_MODULE_PROBECONF += "rpmsg_client_sample ti_k3_r5_remoteproc ti_k3_dsp_remoteproc"

EXTRA_DTC_ARGS += "DTC_FLAGS=-@"
KERNEL_EXTRA_ARGS += "LOADADDR=${UBOOT_ENTRYPOINT} \
                      ${EXTRA_DTC_ARGS}"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .=  "phyboard-lyra-am62xx-2"
COMPATIBLE_MACHINE .= "|phyboard-lyra-am62xx-3"
COMPATIBLE_MACHINE .= "|phyboard-lyra-am62xx-4"

COMPATIBLE_MACHINE .= "|phyboard-lyra-am62axx-1"
COMPATIBLE_MACHINE .= "|phyboard-lyra-am62axx-2"

COMPATIBLE_MACHINE .= "|phyboard-electra-am64xx-1"
COMPATIBLE_MACHINE .= "|phyboard-electra-am64xx-2"

COMPATIBLE_MACHINE .= "|phyboard-izar-am68x-2"
COMPATIBLE_MACHINE .= "|phyboard-izar-am68x-3"
COMPATIBLE_MACHINE .= ")$"

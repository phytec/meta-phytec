inherit kernel kernel-yocto
inherit phygittag buildinfo kernel-deploy-oftree
require linux-common.inc
require linux-phytec-ti.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-phytec-ti-6.6:${THISDIR}/linux-phytec-6.6:"
GIT_URL = "git://github.com/phytec/linux-phytec-ti.git;protocol=https"
SRC_URI = " \
	${GIT_URL};branch=${BRANCH} \
	file://systemd.scc \
	file://phytec_ti_platform.scc \
	file://mtd-partitioned-master.scc \
	${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'file://lxc.scc', '', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'file://oci.scc', '', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'preempt-rt', 'file://preempt-rt.scc', '', d)} \
	${@bb.utils.contains('MACHINE_FEATURES', 'lwb5p', 'file://lwb5p_backports.scc', '', d)} \
"

# Apply rt patch in case of preempt-rt
RT_PATCH = "${KERNELORG_MIRROR}/linux/kernel/projects/rt/6.6/older/patch-6.6.58-rt45.patch.xz;name=rt-patch"
SRC_URI:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'preempt-rt', "${RT_PATCH}", '', d)} \
"
SRC_URI[rt-patch.sha256sum] = "ec3089ab5ebf326fc1a015bb6de5ce451702fcec613c887b61637c3169f3fb5a"

KERNEL_FEATURES = " \
    systemd.scc \
    phytec_ti_platform.scc \
    mtd-partitioned-master.scc \
    ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'lxc.scc oci.scc', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'preempt-rt', 'preempt-rt.scc', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'lwb5p', 'lwb5p_backports.scc', '', d)} \
"

KBUILD_DEFCONFIG ?= "phytec_ti_defconfig"
KCONFIG_MODE="alldefconfig"

PR = "${INC_PR}.0"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
# NOTE: Keep version of TI_LINUX_FW_SRCREV in sync, configured in
#       dynamic-layers/meta-ti-bsp/recipes-bsp/ti-linux-fw/ti-linux-fw-freeze.inc
SRCREV = "352cc2a50d817cca10224d60ef6198f1c7c41e0d"
S = "${WORKDIR}/git"

# Special configuration for remoteproc/rpmsg IPC modules
module_conf_rpmsg_client_sample = "blacklist rpmsg_client_sample"
module_conf_ti_k3_r5_remoteproc = "softdep ti_k3_r5_remoteproc pre: virtio_rpmsg_bus"
module_conf_ti_k3_dsp_remoteproc = "softdep ti_k3_dsp_remoteproc pre: virtio_rpmsg_bus"
KERNEL_MODULE_PROBECONF += "rpmsg_client_sample ti_k3_r5_remoteproc ti_k3_dsp_remoteproc"

KERNEL_VERSION_SANITY_SKIP = "1"

EXTRA_DTC_ARGS += "DTC_FLAGS=-@"
KERNEL_EXTRA_ARGS += "LOADADDR=${UBOOT_ENTRYPOINT} \
                      ${EXTRA_DTC_ARGS}"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .=  "phyboard-lyra"
COMPATIBLE_MACHINE .= "|phyboard-electra"
COMPATIBLE_MACHINE .= "|phyboard-izar"
COMPATIBLE_MACHINE .= "|phyboard-lynx"
COMPATIBLE_MACHINE .= "|phyboard-rigel"
COMPATIBLE_MACHINE .= ")$"

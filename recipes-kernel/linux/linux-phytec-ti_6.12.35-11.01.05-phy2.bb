inherit kernel kernel-yocto
inherit phygittag buildinfo kernel-deploy-oftree
require linux-common.inc
require linux-phytec-fitimage.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-phytec-ti-6.12:${THISDIR}/linux-phytec-6.12:"
GIT_URL = "git://github.com/phytec/linux-phytec-ti.git;protocol=https"
SRC_URI = " \
	${GIT_URL};branch=${BRANCH} \
	file://systemd.scc \
	${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'file://lxc.scc', '', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'file://oci.scc', '', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'preempt-rt', 'file://preempt-rt.scc', '', d)} \
"

# Apply rt patch in case of preempt-rt
RT_PATCH = "${KERNELORG_MIRROR}/linux/kernel/projects/rt/6.12/older/patch-6.12.16-rt9.patch.xz;name=rt-patch"
SRC_URI:append:preempt-rt = " ${RT_PATCH}"
SRC_URI[rt-patch.sha256sum] = "d78490bb1aea4d36d263d104839556b30a85113cb30aeb74c61c1331d104dac1"
LINUX_KERNEL_TYPE:preempt-rt = "preempt-rt"
LINUX_VERSION:preempt-rt = "6.12.16-rt9"

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
SRCREV = "0c8cbb5e045b98463495f59a64b3cc166ca3b1ee"

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
COMPATIBLE_MACHINE .= "|phyboard-lynx"
COMPATIBLE_MACHINE .= "|phyboard-rigel"
COMPATIBLE_MACHINE .= "|am62lx-libra"
COMPATIBLE_MACHINE .= ")$"

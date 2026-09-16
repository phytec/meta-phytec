inherit kernel kernel-yocto
inherit phygittag buildinfo kernel-deploy-oftree
require linux-common.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-phytec-6.18:"
GIT_URL = "git://github.com/phytec/linux-phytec.git;protocol=https"
GIT_URL:phynext = "git://git@git.phytec.de/linux-phytec-dev.git;protocol=ssh"
SRC_URI = " \
	${GIT_URL};name=machine;branch=${BRANCH} \
	git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-6.18;destsuffix=${KMETA};protocol=https \
	file://systemd.scc \
	${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'file://podman-extra.scc', '', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'preempt-rt', 'file://preempt-rt.scc', '', d)} \
"

KMETA = "kernel-meta"

KERNEL_FEATURES = " \
    systemd.scc \
    ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'cfg/docker.scc podman-extra.scc', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'preempt-rt', 'preempt-rt.scc', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'tpm2', 'features/tpm/tpm-2.0.scc', '', d)} \
"

KBUILD_DEFCONFIG ?= "phytec_ti_defconfig"
KCONFIG_MODE = "alldefconfig"

PR = "${INC_PR}.0"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "f09574f5e1623cf39bbed6512e268fa45e33e705"
SRCREV_machine = "${SRCREV}"
SRCREV_meta ?= "4dafe0e420087b6381728e68eeeff6d9af0a32e7"

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
COMPATIBLE_MACHINE .= ")$"

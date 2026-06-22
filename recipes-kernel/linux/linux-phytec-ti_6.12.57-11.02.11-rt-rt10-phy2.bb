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
	file://preempt-rt.scc \
"

KERNEL_FEATURES = " \
    systemd.scc \
    preempt-rt.scc \
"

KBUILD_DEFCONFIG ?= "phytec_ti_defconfig"
KCONFIG_MODE = "alldefconfig"

PR = "${INC_PR}.0"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
# NOTE: Keep version of TI_LINUX_FW_SRCREV in sync, configured in
#       dynamic-layers/meta-ti-bsp/recipes-bsp/ti-linux-fw/ti-linux-fw-freeze.inc
SRCREV = "6d1fd7c754bffc63abd40e53b6536a4f27ab0225"
S = "${WORKDIR}/git"

KERNEL_VERSION_SANITY_SKIP = "1"

EXTRA_DTC_ARGS += "DTC_FLAGS=-@"
KERNEL_EXTRA_ARGS += "LOADADDR=${UBOOT_ENTRYPOINT} \
                      ${EXTRA_DTC_ARGS}"

# Provide oftree symlink in rootfs /boot directory on am57xx
do_install:append:am57xx() {
    dtb=`normalize_dtb "${@get_oftree(d)}"`
    ln -sf $dtb ${D}/${KERNEL_IMAGEDEST}/oftree
}
FILES:${KERNEL_PACKAGE_NAME}-devicetree:append:am57xx = " /${KERNEL_IMAGEDEST}/oftree"

# Skip this recipe if DISTRO_FEATURES doesn't contain the PREEMPT-RT value and
# a kernel without real-time is desired
python () {
    if 'preempt-rt' not in d.getVar("DISTRO_FEATURES"):
        raise bb.parse.SkipPackage("Enable 'preempt-rt' in DISTRO_FEATURES!")
}

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .=  "phycore-am57xx-1"
COMPATIBLE_MACHINE .= "|phycore-am57xx-2"
COMPATIBLE_MACHINE .= "|phycore-am57xx-3"
COMPATIBLE_MACHINE .= "|phycore-am57xx-4"
COMPATIBLE_MACHINE .= "|phycore-am57xx-5"
COMPATIBLE_MACHINE .= "|phycore-am57xx-6"
COMPATIBLE_MACHINE .= ")$"

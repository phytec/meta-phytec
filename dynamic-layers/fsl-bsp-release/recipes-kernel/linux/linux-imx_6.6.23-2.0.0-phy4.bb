# Copyright (C) 2024 PHYTEC Messtechnik GmbH,

inherit kernel
inherit phygittag buildinfo kconfig
inherit fsl-vivante-kernel-driver-handler
include recipes-kernel/linux/linux-common.inc

BRANCH = "v6.6.23-2.0.0-phy"
GIT_URL = "git://git.phytec.de/${BPN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
PR = "${INC_PR}.0"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-imx-6.6:"
SRC_URI:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'file://lxc.cfg', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'file://oci.cfg', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'file://systemd.cfg', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'rauc', 'file://rauc.cfg', '', d)} \
    ${@bb.utils.contains('DEBUG_BUILD', '1', 'file://debugging.cfg', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'tpm2', 'file://tpm2.cfg', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'caam', 'file://caam.cfg', '', d)} \
"

SRCREV = "de0f80cb0116beb2a995a2b4c651872a15cd07c8"

S = "${WORKDIR}/git"

INTREE_DEFCONFIG = "imx_v8_defconfig imx8_phytec_distro.config imx8_phytec_platform.config"
INTREE_DEFCONFIG:mx93-generic-bsp = "imx_v8_defconfig imx9_phytec_distro.config imx9_phytec_platform.config"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "phycore-imx8x-1"
COMPATIBLE_MACHINE .= "|phyboard-nash-imx93-1"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx93-2"
COMPATIBLE_MACHINE .= ")$"

do_deploy:append() {
    if echo ${KERNEL_IMAGETYPES} | grep -wq "fitImage"; then
        if [ -n "${INITRAMFS_IMAGE}" -a "${INITRAMFS_IMAGE_BUNDLE}" != "1" ]; then
            # remove symlink to fitImage without initramfs
            rm -f $deployDir/fitImage
            # create symlink to fitImage with initramfs
            ln -snf fitImage-${INITRAMFS_IMAGE_NAME}-${KERNEL_FIT_NAME}${KERNEL_FIT_BIN_EXT} "$deployDir/fitImage"
        fi
    fi
}

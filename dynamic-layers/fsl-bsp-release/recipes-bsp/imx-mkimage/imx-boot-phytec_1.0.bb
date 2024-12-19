# Copyright 2017-2020 NXP

require recipes-bsp/imx-mkimage/imx-mkimage_git.inc

DESCRIPTION = "Generate Boot Loader for i.MX 8 device"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"
SECTION = "BSP"

FILESEXTRAPATHS:prepend := "${OEROOT}/../meta-imx/meta-imx-bsp/recipes-bsp/imx-mkimage/files:"

inherit use-imx-security-controller-firmware

IMX_EXTRA_FIRMWARE      = "firmware-imx-8 imx-sc-firmware imx-seco"
IMX_EXTRA_FIRMWARE:mx8x-nxp-bsp = "imx-sc-firmware-phytec imx-seco"
DEPENDS += " \
    u-boot \
    ${IMX_EXTRA_FIRMWARE} \
    imx-atf \
    ${@bb.utils.contains('MACHINE_FEATURES', 'optee', 'optee-os', '', d)} \
"
DEPENDS:append:mx8m-nxp-bsp = " u-boot-mkimage-native dtc-native"
BOOT_NAME = "imx-boot"
PROVIDES = "${BOOT_NAME}"

inherit deploy

# Add CFLAGS with native INCDIR & LIBDIR for imx-mkimage build
CFLAGS = "-O2 -Wall -std=c99 -I ${STAGING_INCDIR_NATIVE} -L ${STAGING_LIBDIR_NATIVE}"

IMX_M4_DEMOS        = ""
IMX_M4_DEMOS:mx8qm-nxp-bsp  = "imx-m4-demos:do_deploy"
IMX_M4_DEMOS:mx8x-nxp-bsp   = "imx-m4-demos:do_deploy"
IMX_M4_DEMOS:mx8dxl-nxp-bsp = "imx-m4-demos:do_deploy"

M4_DEFAULT_IMAGE ?= "m4_image.bin"
M4_DEFAULT_IMAGE:mx8qxp-nxp-bsp = "imx8qx_m4_TCM_power_mode_switch.bin"
M4_DEFAULT_IMAGE:mx8phantomdxl-nxp-bsp = "imx8dxl-phantom_m4_TCM_srtm_demo.bin"
M4_DEFAULT_IMAGE:mx8dxl-nxp-bsp = "imx8dxl_m4_TCM_power_mode_switch.bin"
M4_DEFAULT_IMAGE:mx8dx-nxp-bsp = "imx8qx_m4_TCM_power_mode_switch.bin"

include imx-boot-phytec-secureboot.inc

# This package aggregates output deployed by other packages,
# so set the appropriate dependencies
do_compile[depends] += " \
    virtual/bootloader:do_deploy \
    ${@' '.join('%s:do_deploy' % r for r in '${IMX_EXTRA_FIRMWARE}'.split() )} \
    imx-atf:do_deploy \
    ${IMX_M4_DEMOS} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'optee', 'optee-os:do_deploy', '', d)} \
"

SC_FIRMWARE_NAME ?= "scfw_tcm.bin"

ATF_MACHINE_NAME ?= "bl31-imx8qm.bin"
ATF_MACHINE_NAME:mx8qm-nxp-bsp = "bl31-imx8qm.bin"
ATF_MACHINE_NAME:mx8x-nxp-bsp = "bl31-imx8qx.bin"
ATF_MACHINE_NAME:mx8mq-nxp-bsp = "bl31-imx8mq.bin"
ATF_MACHINE_NAME:mx8mm-nxp-bsp = "bl31-imx8mm.bin"
ATF_MACHINE_NAME:mx8mn-nxp-bsp = "bl31-imx8mn.bin"
ATF_MACHINE_NAME:mx8mp-nxp-bsp = "bl31-imx8mp.bin"
ATF_MACHINE_NAME:mx8phantomdxl-nxp-bsp = "bl31-imx8qx.bin"
ATF_MACHINE_NAME:mx8dxl-nxp-bsp = "bl31-imx8dxl.bin"
ATF_MACHINE_NAME:mx8dx-nxp-bsp = "bl31-imx8dx.bin"
ATF_MACHINE_NAME:append = "${@bb.utils.contains('MACHINE_FEATURES', 'optee', '-optee', '', d)}"

TOOLS_NAME ?= "mkimage_imx8"

SOC_TARGET       ?= "INVALID"
SOC_TARGET:mx8qm-nxp-bsp = "iMX8QM"
SOC_TARGET:mx8x-nxp-bsp  = "iMX8QX"
SOC_TARGET:mx8mq-nxp-bsp = "iMX8M"
SOC_TARGET:mx8mm-nxp-bsp = "iMX8MM"
SOC_TARGET:mx8mn-nxp-bsp = "iMX8MN"
SOC_TARGET:mx8mp-nxp-bsp = "iMX8MP"
SOC_TARGET:mx8dxl-nxp-bsp = "iMX8DXL"
SOC_TARGET:mx8phantomdxl-nxp-bsp = "iMX8QX"
SOC_TARGET:mx8dx-nxp-bsp = "iMX8DX"

DEPLOY_OPTEE = "${@bb.utils.contains('MACHINE_FEATURES', 'optee', 'true', 'false', d)}"

IMXBOOT_TARGETS ?= \
    "${@bb.utils.contains('UBOOT_CONFIG', 'fspi', 'flash_flexspi', \
        bb.utils.contains('UBOOT_CONFIG', 'nand', 'flash_nand', \
                                                  'flash_multi_cores flash_dcd', d), d)}"

BOOT_STAGING       = "${S}/${SOC_TARGET}"
BOOT_STAGING:mx8m-nxp-bsp  = "${S}/iMX8M"
BOOT_STAGING:mx8x-nxp-bsp = "${S}/iMX8QX"

SOC_FAMILY      = "INVALID"
SOC_FAMILY:mx8-nxp-bsp  = "mx8"
SOC_FAMILY:mx8m-nxp-bsp = "mx8m"
SOC_FAMILY:mx8x-nxp-bsp = "mx8x"

REV_OPTION ?= ""
REV_OPTION:mx8qxp-generic-bsp = " \
    ${@bb.utils.contains('MACHINE_FEATURES', 'soc-revb0', '', 'REV=C0', d)} \
"
REV_OPTION:mx8qxpc0-nxp-bsp = "REV=C0"

TEE_LOAD_ADDRESS ?= ""
TEE_LOAD_ADDRESS:mx8mm-nxp-bsp = "TEE_LOAD_ADDR=0x56000000"

compile_mx8m() {
    bbnote 8MQ/8MM boot binary build
    for ddr_firmware in ${DDR_FIRMWARE_NAME}; do
        bbnote "Copy ddr_firmware: ${ddr_firmware} from ${DEPLOY_DIR_IMAGE} -> ${BOOT_STAGING} "
        cp ${DEPLOY_DIR_IMAGE}/${ddr_firmware}               ${BOOT_STAGING}
    done
    cp ${DEPLOY_DIR_IMAGE}/signed_dp_imx8m.bin               ${BOOT_STAGING}
    cp ${DEPLOY_DIR_IMAGE}/signed_hdmi_imx8m.bin             ${BOOT_STAGING}
    cp ${DEPLOY_DIR_IMAGE}/${BOOT_TOOLS}/${UBOOT_DTB_NAME}   ${BOOT_STAGING}
    bbnote "\
Using standard mkimage from u-boot-tools for FIT image builds. The standard \
mkimage is compatible for this use, and using it saves us from having to \
maintain a custom recipe."
    ln -sf ${STAGING_DIR_NATIVE}${bindir}/mkimage            ${BOOT_STAGING}/mkimage_uboot
    cp ${DEPLOY_DIR_IMAGE}/${BOOT_TOOLS}/${ATF_MACHINE_NAME} ${BOOT_STAGING}/bl31.bin
    for type in ${UBOOT_CONFIG}; do
        cp ${DEPLOY_DIR_IMAGE}/u-boot-spl.bin-${MACHINE}-${type} \
                                                             ${BOOT_STAGING}/u-boot-spl.bin-${type}
        cp ${DEPLOY_DIR_IMAGE}/${BOOT_TOOLS}/u-boot-nodtb.bin-${MACHINE}-${type} \
                                                             ${BOOT_STAGING}/u-boot-nodtb.bin-${type}
        cp ${DEPLOY_DIR_IMAGE}/u-boot-${MACHINE}.bin-${type} ${BOOT_STAGING}/u-boot.bin-${type}
    done
}

compile_mx8() {
    bbnote 8QM boot binary build
    cp ${DEPLOY_DIR_IMAGE}/${BOOT_TOOLS}/${SC_FIRMWARE_NAME} ${BOOT_STAGING}/scfw_tcm.bin
    cp ${DEPLOY_DIR_IMAGE}/${BOOT_TOOLS}/${ATF_MACHINE_NAME} ${BOOT_STAGING}/bl31.bin
    for type in ${UBOOT_CONFIG}; do
        cp ${DEPLOY_DIR_IMAGE}/u-boot-${MACHINE}.bin-${type} ${BOOT_STAGING}/u-boot.bin-${type}
        if [ -e ${DEPLOY_DIR_IMAGE}/u-boot-spl.bin-${MACHINE}-${type} ] ; then
            cp ${DEPLOY_DIR_IMAGE}/u-boot-spl.bin-${MACHINE}-${type} \
                                                             ${BOOT_STAGING}/u-boot-spl.bin-${type}
        fi
    done
    cp ${DEPLOY_DIR_IMAGE}/imx8qm_m4_TCM_power_mode_switch_m40.bin \
                                                             ${BOOT_STAGING}/m4_image.bin
    cp ${DEPLOY_DIR_IMAGE}/imx8qm_m4_TCM_power_mode_switch_m41.bin \
                                                             ${BOOT_STAGING}/m4_1_image.bin
    cp ${DEPLOY_DIR_IMAGE}/${SECO_FIRMWARE_NAME}             ${BOOT_STAGING}
}

compile_mx8x() {
    bbnote 8QX boot binary build
    cp ${DEPLOY_DIR_IMAGE}/mcore-demos/${M4_DEFAULT_IMAGE}   ${BOOT_STAGING}/m4_image.bin
    cp ${DEPLOY_DIR_IMAGE}/${SECO_FIRMWARE_NAME}             ${BOOT_STAGING}
    cp ${DEPLOY_DIR_IMAGE}/${BOOT_TOOLS}/${SC_FIRMWARE_NAME} ${BOOT_STAGING}/scfw_tcm.bin
    cp ${DEPLOY_DIR_IMAGE}/${BOOT_TOOLS}/${ATF_MACHINE_NAME} ${BOOT_STAGING}/bl31.bin
    for type in ${UBOOT_CONFIG}; do
        cp ${DEPLOY_DIR_IMAGE}/u-boot-${MACHINE}.bin-${type} ${BOOT_STAGING}/u-boot.bin-${type}
        if [ -e ${DEPLOY_DIR_IMAGE}/u-boot-spl.bin-${MACHINE}-${type} ] ; then
            cp ${DEPLOY_DIR_IMAGE}/u-boot-spl.bin-${MACHINE}-${type} \
                                                             ${BOOT_STAGING}/u-boot-spl.bin-${type}
        fi
    done
}

do_compile() {
    # Copy TEE binary to SoC target folder to mkimage
    if ${DEPLOY_OPTEE}; then
        cp ${DEPLOY_DIR_IMAGE}/tee.bin                       ${BOOT_STAGING}
    fi
    # mkimage for i.MX8
    for target in ${IMXBOOT_TARGETS}; do
        for config in ${UBOOT_CONFIG}; do
            compile_${SOC_FAMILY}
            allbins="u-boot.bin u-boot-nodtb.bin u-boot-spl.bin"
            for bin in ${allbins} ; do
                if [ -e "${BOOT_STAGING}/${bin}" ]; then
                    rm ${BOOT_STAGING}/${bin}
                fi
                if [ -e "${BOOT_STAGING}/${bin}-${config}" ]; then
                    ln -s ${bin}-${config} ${BOOT_STAGING}/${bin}
                fi
            done
            make clean
            if [ "$target" = "flash_linux_m4_no_v2x" ]; then
               # Special target build for i.MX 8DXL with V2X off
               bbnote "building ${SOC_TARGET} - ${REV_OPTION} V2X=NO ${target}"
               make SOC=${SOC_TARGET} dtbs=${UBOOT_DTB_NAME} ${REV_OPTION} ${TEE_LOAD_ADDRESS} V2X=NO  flash_linux_m4 2>&1 | tee ${WORKDIR}/make_output_${SOC_TARGET}_${target}_${config}.log
            else
               bbnote "building ${SOC_TARGET} - ${REV_OPTION} ${target}"
               make SOC=${SOC_TARGET} dtbs=${UBOOT_DTB_NAME} ${REV_OPTION} ${TEE_LOAD_ADDRESS} ${target} 2>&1 | tee ${WORKDIR}/make_output_${SOC_TARGET}_${target}_${config}.log
            fi
            if [ -e "${BOOT_STAGING}/flash.bin" ]; then
                cp ${BOOT_STAGING}/flash.bin ${S}/${BOOT_NAME}-${MACHINE}-${config}.bin-${target}
            fi
        done
    done
}

do_install () {
    install -d ${D}/boot
    for target in ${IMXBOOT_TARGETS}; do
        for type in ${UBOOT_CONFIG}; do
            install -m 0644 ${S}/${BOOT_NAME}-${MACHINE}-${type}.bin-${target} ${D}/boot/
        done
    done
}

deploy_mx8m() {
    install -d ${DEPLOYDIR}/${BOOT_TOOLS}
    for type in ${UBOOT_CONFIG}; do
        install -m 0644 ${DEPLOY_DIR_IMAGE}/u-boot-spl.bin-${MACHINE}-${type} \
                                                             ${DEPLOYDIR}/${BOOT_TOOLS}
    done
    for ddr_firmware in ${DDR_FIRMWARE_NAME}; do
        install -m 0644 ${DEPLOY_DIR_IMAGE}/${ddr_firmware}  ${DEPLOYDIR}/${BOOT_TOOLS}
    done
    install -m 0644 ${BOOT_STAGING}/signed_dp_imx8m.bin      ${DEPLOYDIR}/${BOOT_TOOLS}
    install -m 0644 ${BOOT_STAGING}/signed_hdmi_imx8m.bin    ${DEPLOYDIR}/${BOOT_TOOLS}
    install -m 0755 ${BOOT_STAGING}/${TOOLS_NAME}            ${DEPLOYDIR}/${BOOT_TOOLS}
    install -m 0755 ${BOOT_STAGING}/mkimage_fit_atf.sh       ${DEPLOYDIR}/${BOOT_TOOLS}
    install -m 0755 ${BOOT_STAGING}/mkimage_uboot            ${DEPLOYDIR}/${BOOT_TOOLS}
}

deploy:mx8-nxp-bsp() {
    install -d ${DEPLOYDIR}/${BOOT_TOOLS}
    install -m 0644 ${BOOT_STAGING}/${SECO_FIRMWARE_NAME}    ${DEPLOYDIR}/${BOOT_TOOLS}
    install -m 0644 ${BOOT_STAGING}/m4_image.bin             ${DEPLOYDIR}/${BOOT_TOOLS}
    install -m 0644 ${BOOT_STAGING}/m4_1_image.bin           ${DEPLOYDIR}/${BOOT_TOOLS}
    install -m 0755 ${S}/${TOOLS_NAME}                       ${DEPLOYDIR}/${BOOT_TOOLS}
    for type in ${UBOOT_CONFIG}; do
        if [ -e ${DEPLOY_DIR_IMAGE}/u-boot-spl.bin-${MACHINE}-${type} ] ; then
            install -m 0644 ${DEPLOY_DIR_IMAGE}/u-boot-spl.bin-${MACHINE}-${type} \
                                                             ${DEPLOYDIR}/${BOOT_TOOLS}
        fi
    done
}

deploy_mx8x() {
    install -d ${DEPLOYDIR}/${BOOT_TOOLS}
    install -m 0644 ${BOOT_STAGING}/${SECO_FIRMWARE_NAME}    ${DEPLOYDIR}/${BOOT_TOOLS}
    install -m 0644 ${BOOT_STAGING}/m4_image.bin             ${DEPLOYDIR}/${BOOT_TOOLS}
    install -m 0755 ${S}/${TOOLS_NAME}                       ${DEPLOYDIR}/${BOOT_TOOLS}
    for type in ${UBOOT_CONFIG}; do
        if [ -e ${DEPLOY_DIR_IMAGE}/u-boot-spl.bin-${MACHINE}-${type} ] ; then
            install -m 0644 ${DEPLOY_DIR_IMAGE}/u-boot-spl.bin-${MACHINE}-${type} \
                                                             ${DEPLOYDIR}/${BOOT_TOOLS}
        fi
    done
}

do_deploy() {
    deploy_${SOC_FAMILY}
    # copy tee.bin to deploy path
    if "${DEPLOY_OPTEE}"; then
        install -m 0644 ${DEPLOY_DIR_IMAGE}/tee.bin          ${DEPLOYDIR}/${BOOT_TOOLS}
    fi
    # copy the tool mkimage to deploy path and sc fw, dcd and uboot
    for type in ${UBOOT_CONFIG}; do
        install -m 0644 ${DEPLOY_DIR_IMAGE}/u-boot-${MACHINE}.bin-${type} \
                                                             ${DEPLOYDIR}/${BOOT_TOOLS}
    done
    # copy makefile (soc.mak) for reference
    install -m 0644 ${BOOT_STAGING}/soc.mak                  ${DEPLOYDIR}/${BOOT_TOOLS}
    # copy the generated boot image to deploy path
    for target in ${IMXBOOT_TARGETS}; do
        # Use first "target" as IMAGE_IMXBOOT_TARGET
        if [ "$IMAGE_IMXBOOT_TARGET" = "" ]; then
            IMAGE_IMXBOOT_TARGET="$target"
            echo "Set boot target as $IMAGE_IMXBOOT_TARGET"
        fi
        for type in ${UBOOT_CONFIG}; do
            install -m 0644 ${S}/${BOOT_NAME}-${MACHINE}-${type}.bin-${target} \
                                                             ${DEPLOYDIR}
            # Link last type
            ln -sf ${BOOT_NAME}-${MACHINE}-${type}.bin-${IMAGE_IMXBOOT_TARGET} \
                                                             ${DEPLOYDIR}/${BOOT_NAME}
        done
    done
}
addtask deploy before do_build after do_compile

PACKAGE_ARCH = "${MACHINE_ARCH}"
FILES:${PN} = "/boot"

COMPATIBLE_MACHINE = "(mx8-nxp-bsp)"

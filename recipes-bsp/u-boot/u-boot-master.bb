require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc

DEPENDS += "bc-native dtc-native gnutls-native u-boot-mkimage-native \
    ${@bb.utils.contains('MACHINE_FEATURES', 'optee', 'optee-os', '', d)} \
"
DEPENDS:append:imx-generic-bsp = " \
    ${@' '.join('%s' % r for r in '${IMX_EXTRA_FIRMWARE}'.split() )} \
    ${IMX_DEFAULT_ATF_PROVIDER} \
"

GIT_URL = "git://source.denx.de/u-boot/u-boot.git;protocol=https"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=2ca5f2c35c8cc335f0a19756634782f1"

PR = "r0"
S = "${WORKDIR}/git"

SRCREV = "${AUTOREV}"
BRANCH = "master"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append = " \
    file://boot.cmd \
    file://boot.its \
    "

DEFAULT_PREFERENCE = "-1"

PROVIDES += "u-boot"

UBOOT_ENV_FIT_SRC = "boot.its"
UBOOT_ENV_FIT_BINARY = "boot.scr.uimg"

# Used by imx
ATF_MACHINE_NAME = "bl31-${ATF_PLATFORM}.bin"
ATF_MACHINE_NAME:append = "${@bb.utils.contains('MACHINE_FEATURES', 'optee', '-optee', '', d)}"

IMX_BOOT_CONTAINER_FIRMWARE_SOC ?= ""
IMX_BOOT_CONTAINER_FIRMWARE ?= " \
    ${IMX_BOOT_CONTAINER_FIRMWARE_SOC} \
    ${DDR_FIRMWARE_NAME} \
"

# Define an additional task that collects binary output from dependent packages
# and deploys them into the U-Boot build directory
do_configure:append:imx-generic-bsp() {
    if [ -n "${UBOOT_CONFIG}" ]; then
        for config in ${UBOOT_MACHINE}; do
            i=$(expr $i + 1);
            for type in ${UBOOT_CONFIG}; do
                j=$(expr $j + 1);
                if [ $j -eq $i ]; then
                    for firmware in ${IMX_BOOT_CONTAINER_FIRMWARE}; do
                        bbnote "Copy firmware: ${firmware} from ${DEPLOY_DIR_IMAGE} -> ${B}/${config}/"
                        cp ${DEPLOY_DIR_IMAGE}/${firmware} ${B}/${config}/
                    done
                    if [ -n "${ATF_MACHINE_NAME}" ]; then
                        cp ${DEPLOY_DIR_IMAGE}/${ATF_MACHINE_NAME} ${B}/${config}/bl31.bin
                    else
                        bberror "ATF binary is undefined, result binary would be unusable!"
                    fi
                fi
            done
            unset  j
        done
        unset  i
    fi
}

# Use FIT image boot script
do_compile:append() {
    if [ -n "${UBOOT_ENV_FIT_SRC}" ]
    then
        ${UBOOT_MKIMAGE} -C none -A ${UBOOT_ARCH} -f ${UNPACKDIR}/${UBOOT_ENV_FIT_SRC} ${WORKDIR}/${UBOOT_ENV_FIT_BINARY}
    fi
}

do_deploy:append() {
    install -m 644 ${WORKDIR}/${UBOOT_ENV_FIT_BINARY} ${DEPLOYDIR}/${UBOOT_ENV_FIT_BINARY}
}

# Needed for interoperability between NXP BSP and Mainline BSP. wks files hardcode the name
do_deploy:append:imx-generic-bsp() {
    ln -sf ${UBOOT_BINARY} imx-boot
}

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .=  "phyboard-polis-imx8mm-5"
COMPATIBLE_MACHINE .= "|phyboard-pollux-imx8mp-3"
COMPATIBLE_MACHINE .= "|phygate-tauri-l-imx8mm-2"
COMPATIBLE_MACHINE .= "|imx8mp-libra-fpsc-1"
COMPATIBLE_MACHINE .= ")$"

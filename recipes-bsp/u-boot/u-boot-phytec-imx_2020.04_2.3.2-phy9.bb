DESCRIPTION = "i.MX U-Boot suppporting i.MX reference boards."

inherit phygittag
require recipes-bsp/u-boot/u-boot.inc
inherit python3native

include u-boot-secureboot.inc
include u-boot-protectionshield.inc
include u-boot-rauc.inc

PROVIDES += "u-boot"
DEPENDS:append = " python3 dtc-native bison-native"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://Licenses/gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263"
BRANCH = "v2020.04_2.3.2-phy"
GIT_URL = "git://github.com/phytec/${BPN};protocol=https"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

PR = "r0"
SRCREV = "d84e9465a7a6a57da3b0f733d65ccc2d417780e7"


BOOT_TOOLS = "imx-boot-tools"

do_deploy:append:mx8m-nxp-bsp () {
    # Deploy the u-boot-nodtb.bin and fsl-imx8mq-XX.dtb, to be packaged in boot binary by imx-boot
    if [ -n "${UBOOT_CONFIG}" ]
    then
        for config in ${UBOOT_MACHINE}; do
            i=$(expr $i + 1);
            for type in ${UBOOT_CONFIG}; do
                j=$(expr $j + 1);
                if [ $j -eq $i ]
                then
                    install -d ${DEPLOYDIR}/${BOOT_TOOLS}
                    install -m 0777 ${B}/${config}-${type}/arch/arm/dts/${UBOOT_DTB_NAME}  ${DEPLOYDIR}/${BOOT_TOOLS}
                    install -m 0777 ${B}/${config}-${type}/u-boot-nodtb.bin  ${DEPLOYDIR}/${BOOT_TOOLS}/u-boot-nodtb.bin-${MACHINE}-${type}
                fi
            done
            unset  j
        done
        unset  i
    fi

}

PACKAGE_ARCH = "${MACHINE_ARCH}"

COMPATIBLE_MACHINE = "^("
COMPATIBLE_MACHINE .= "phyboard-polaris-imx8m-3"
COMPATIBLE_MACHINE .= "|phyboard-polaris-imx8m-4"
COMPATIBLE_MACHINE .= ")$"

UBOOT_NAME:mx8-nxp-bsp = "u-boot-${MACHINE}.bin-${UBOOT_CONFIG}"

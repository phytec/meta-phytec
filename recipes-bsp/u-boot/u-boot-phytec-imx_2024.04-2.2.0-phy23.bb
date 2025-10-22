DESCRIPTION = "i.MX U-Boot supporting i.MX reference boards."

inherit phygittag
require recipes-bsp/u-boot/u-boot.inc
inherit python3native
include u-boot-rauc.inc
include u-boot-securiphy.inc
include u-boot-hardening.inc
include u-boot-imx-remove-symlinks.inc

PROVIDES += "u-boot"
DEPENDS += "flex-native bison-native bc-native dtc-native gnutls-native python3-setuptools-native"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://Licenses/gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263"
BRANCH = "v2024.04-2.2.0-phy"
GIT_URL = "git://github.com/phytec/${BPN};protocol=https"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

PR = "r0"
SRCREV = "a429a8f5162ad1e90aef9ebb6ed79e90cc2cbf11"

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MACHINE_ARCH}"

BOOT_TOOLS = "imx-boot-tools"

do_deploy:append:mx8m-generic-bsp() {
    # Deploy u-boot-nodtb.bin and fsl-imx8m*-XX.dtb for mkimage to generate boot binary
    if [ -n "${UBOOT_CONFIG}" ]
    then
        for config in ${UBOOT_MACHINE}; do
            i=$(expr $i + 1);
            for type in ${UBOOT_CONFIG}; do
                j=$(expr $j + 1);
                if [ $j -eq $i ]
                then
                    install -d ${DEPLOYDIR}/imx-boot-tools
                    install -m 0777 ${B}/${config}/arch/arm/dts/${UBOOT_DTB_NAME} ${DEPLOYDIR}/imx-boot-tools
                    install -m 0777 ${B}/${config}/u-boot-nodtb.bin  ${DEPLOYDIR}/${BOOT_TOOLS}/u-boot-nodtb.bin-${MACHINE}-${type}
                fi
            done
            unset  j
        done
        unset  i
    fi

    # Deploy CRT.* from u-boot for stmm
    install -m 0644 ${S}/CRT.*     ${DEPLOYDIR}
}

do_deploy:append:mx93-generic-bsp() {
    # Deploy CRT.* from u-boot for stmm
    install -m 0644 ${S}/CRT.*     ${DEPLOYDIR}
}

COMPATIBLE_MACHINE = "^("
COMPATIBLE_MACHINE .= "phyboard-pollux-imx8mp-3"
COMPATIBLE_MACHINE .= "|phyboard-polis-imx8mm-5"
COMPATIBLE_MACHINE .= "|phygate-tauri-l-imx8mm-2"
COMPATIBLE_MACHINE .= "|phycore-imx8x-1"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx91-1"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx93-2"
COMPATIBLE_MACHINE .= "|phyboard-nash-imx93-1"
COMPATIBLE_MACHINE .= "|imx8mp-libra-fpsc-1"
COMPATIBLE_MACHINE .= "|imx95-libra-fpsc-1"
COMPATIBLE_MACHINE .= ")$"

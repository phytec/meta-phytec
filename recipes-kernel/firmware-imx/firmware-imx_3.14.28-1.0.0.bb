# Copyright (C) 2015 PHYTEC Messtechnik GmbH,
# Author: Stefan Christ <s.christ@phytec.de>

inherit fsl-bin-unpack

SUMMARY = "Freescale i.MX firmware files"
DESCRIPTION = "Freescale i.MX firmware files for the VPU and SDMA"
SECTION = "base"

# Year and version are from file COPYING in binary archive
LICENSE_FLAGS = "license-freescale_v6-february-2015"
LICENSE = "Proprietary & LICENCE.freescale-v6-february-2015"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=acdb807ac7275fe32f9f64992e111241 \
"

SRC_URI = "${FSL_MIRROR}/${PN}-${PV}.bin;fsl-bin=true"
SRC_URI[md5sum] = "6e700f3d3a6482db08d5aabee7751630"
SRC_URI[sha256sum] = "1f09acd4d605efc78a0672068a658cb16274811d2f444cf3ae7aaa075266746f"

PR = "r0"

PACKAGES = " \
    ${PN}-freescale-imx-license \
    ${PN}-vpu-mx6q \
    ${PN}-vpu-mx6dl \
"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install () {
    install -d ${D}/lib/firmware
    # Only install i.MX6 VPU firmware for now.
    install -m 644 ${S}/firmware/vpu/vpu_fw_imx6q.bin ${D}/lib/firmware/
    install -m 644 ${S}/firmware/vpu/vpu_fw_imx6d.bin ${D}/lib/firmware/
    install -m 644 ${S}/COPYING "${D}/lib/firmware/LICENCE.freescale-v6-february-2015"
}


# Use same license scheme as in recipe linux-firmware
FILES_${PN}-freescale-imx-license = "/lib/firmware/LICENCE.freescale-v6-february-2015"

LICENSE_${PN}-vpu-mx6q = "LICENCE.freescale-v6-february-2015"
FILES_${PN}-vpu-mx6q = "/lib/firmware/vpu_fw_imx6q.bin"
RDEPENDS_${PN}-vpu-mx6q += "${PN}-freescale-imx-license"

LICENSE_${PN}-vpu-mx6dl = "LICENCE.freescale-v6-february-2015"
FILES_${PN}-vpu-mx6dl = "/lib/firmware/vpu_fw_imx6d.bin"
RDEPENDS_${PN}-vpu-mx6dl += "${PN}-freescale-imx-license"

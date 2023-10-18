# Copyright (C) 2015 PHYTEC Messtechnik GmbH,
# Author: Stefan Christ <s.christ@phytec.de>

inherit fsl-bin-unpack

SUMMARY = "Freescale i.MX firmware files"
DESCRIPTION = "Freescale i.MX firmware files for the VPU and SDMA"
SECTION = "base"

# Year and version are from file COPYING in binary archive
LICENSE_FLAGS = "license-freescale_v12-march-2016"
LICENSE = "Proprietary & LICENCE.freescale-v12-march-2016"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=8cf95184c220e247b9917e7244124c5a \
"

SRC_URI = "${FSL_MIRROR}/${BPN}-${PV}.bin;fsl-bin=true"
SRC_URI[md5sum] = "dae846ca2fc4504067f725f501491adf"
SRC_URI[sha256sum] = "c5bd4bff48cce9715a5d6d2c190ff3cd2262c7196f7facb9b0eda231c92cc223"

PR = "r0"

PACKAGES = " \
    ${PN}-freescale-imx-license \
    ${PN}-vpu-mx6q \
    ${PN}-vpu-mx6dl \
"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install () {
    install -d ${D}${nonarch_base_libdir}/firmware
    # Only install i.MX6 VPU firmware for now.
    install -m 644 ${S}/firmware/vpu/vpu_fw_imx6q.bin ${D}${nonarch_base_libdir}/firmware/
    install -m 644 ${S}/firmware/vpu/vpu_fw_imx6d.bin ${D}${nonarch_base_libdir}/firmware/
    install -m 644 ${S}/COPYING "${D}${nonarch_base_libdir}/firmware/LICENCE.freescale-v12-march-2016"
}


# Use same license scheme as in recipe linux-firmware
FILES:${PN}-freescale-imx-license = "${nonarch_base_libdir}/firmware/LICENCE.freescale-v12-march-2016"

LICENSE:${PN}-vpu-mx6q = "LICENCE.freescale-v12-march-2016"
FILES:${PN}-vpu-mx6q = "${nonarch_base_libdir}/firmware/vpu_fw_imx6q.bin"
RDEPENDS:${PN}-vpu-mx6q += "${PN}-freescale-imx-license"

LICENSE:${PN}-vpu-mx6dl = "LICENCE.freescale-v12-march-2016"
FILES:${PN}-vpu-mx6dl = "${nonarch_base_libdir}/firmware/vpu_fw_imx6d.bin"
RDEPENDS:${PN}-vpu-mx6dl += "${PN}-freescale-imx-license"

COMPATIBLE_MACHINE = "phy.*imx6.*"

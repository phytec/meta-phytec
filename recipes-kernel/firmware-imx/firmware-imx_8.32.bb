# Copyright (C) 2015 PHYTEC Messtechnik GmbH,
# Author: Stefan Christ <s.christ@phytec.de>

inherit fsl-bin-unpack

SUMMARY = "Freescale i.MX firmware files"
DESCRIPTION = "Freescale i.MX firmware files for the VPU and SDMA"
SECTION = "base"

# Year and version are from file COPYING in binary archive
LICENSE_FLAGS = "license-freescale_v63-may-2025"
LICENSE = "LicenseRef-LICENCE.freescale-v63-may-2025 AND LicenseRef-Proprietary"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=bc649096ad3928ec06a8713b8d787eac \
"

IMX_SRCREV_ABBREV = "1991416"

SRC_URI = "${FSL_MIRROR}/${BPN}-${PV}-${IMX_SRCREV_ABBREV}.bin;fsl-bin=true"
SRC_URI[md5sum] = "8ed269b45edadaff793617e85d2a8966"
SRC_URI[sha256sum] = "11396e5798b62cd61963db806c0c05500887bc62a98e1d16dbc3014aa0c21a2a"

# The self-extracting archive unpacks into a directory that includes the
# abbreviated source revision, not just ${BPN}-${PV}.
S = "${UNPACKDIR}/${BPN}-${PV}-${IMX_SRCREV_ABBREV}"

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
    install -m 644 ${S}/COPYING "${D}${nonarch_base_libdir}/firmware/LICENCE.freescale-v63-may-2025"
}


# Use same license scheme as in recipe linux-firmware
FILES:${PN}-freescale-imx-license = "${nonarch_base_libdir}/firmware/LICENCE.freescale-v63-may-2025"

LICENSE:${PN}-vpu-mx6q = "LicenseRef-LICENCE.freescale-v63-may-2025"
FILES:${PN}-vpu-mx6q = "${nonarch_base_libdir}/firmware/vpu_fw_imx6q.bin"
RDEPENDS:${PN}-vpu-mx6q += "${PN}-freescale-imx-license"

LICENSE:${PN}-vpu-mx6dl = "LicenseRef-LICENCE.freescale-v63-may-2025"
FILES:${PN}-vpu-mx6dl = "${nonarch_base_libdir}/firmware/vpu_fw_imx6d.bin"
RDEPENDS:${PN}-vpu-mx6dl += "${PN}-freescale-imx-license"

COMPATIBLE_MACHINE = "phy.*imx6.*"

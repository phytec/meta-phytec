# Copyright (C) 2021, Phytec - All Rights Reserved

SUMMARY = "Bluetooth firmware for BCM4335"
HOMEPAGE = "https://github.com/murata-wireless/cyw-bt-patch"
LICENSE = "Firmware-cypress-bcm4335"
LIC_FILES_CHKSUM = "file://LICENCE.cypress;md5=cbc5f665d04f741f1e006d2096236ba7"

NO_GENERIC_LICENSE[Firmware-cypress-bcm4335] = "LICENCE.cypress"

inherit allarch

SRC_URI = "git://github.com/murata-wireless/cyw-bt-patch;protocol=https"
SRCREV = "c5f1b13697d4ac8eec2cb6f21636085fbb55acd1"

PV = "1.0"

S = "${WORKDIR}/git"

PACKAGES =+ "${PN}-cypress-license"

do_install() {
    install -d ${D}${nonarch_base_libdir}/firmware/brcm/

    install -m 644 ${S}/CYW4335C0.ZP.hcd ${D}${nonarch_base_libdir}/firmware/brcm/BCM4335C0.hcd
    install -m 644 ${S}/LICENCE.cypress ${D}${nonarch_base_libdir}/firmware/LICENCE.cypress_bcm4335
}

LICENSE_${PN} = "Firmware-cypress-bcm4335"
LICENSE_${PN}-cypress-license = "Firmware-cypress-bcm4335"

FILES_${PN}-cypress-license = "${nonarch_base_libdir}/firmware/LICENCE.cypress_bcm4335"
FILES_${PN} = "${nonarch_base_libdir}/firmware/"

RDEPENDS_${PN} += "${PN}-cypress-license"

RRECOMMENDS_${PN}_append_stm32mpcommon = " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'bluetooth-suspend', '', d)}"

# Firmware files are generally not ran on the CPU, so they can be
# allarch despite being architecture specific
INSANE_SKIP = "arch"

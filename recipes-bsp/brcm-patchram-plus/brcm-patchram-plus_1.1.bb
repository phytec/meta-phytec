SUMMARY = "Broadcom patchram plus utility provided by Laird"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=691691b063f1b4034300dc452e36b68d"

DEPENDS = "bluez5"

SRC_URI = "https://github.com/LairdCP/brcm_patchram/archive/brcm_patchram_plus_1.1.tar.gz"

SRC_URI[md5sum] = "3c03e03ce4ce11ea131702779906c6b3"
SRC_URI[sha256sum] = "02397334a7a797c936ae5739beccb5f2a3dc6e512f685cbc27fc944d17cc4f79"

S = "${WORKDIR}/brcm_patchram-brcm_patchram_plus_${PV}"

CLEANBROKEN ="1"

do_install_append() {
	install -d ${D}/usr/bin
	install -m 755 brcm_patchram_plus ${D}/usr/bin
}

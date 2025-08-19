FILESEXTRAPATHS:prepend := "${THISDIR}/optee-client-4.4.0:"
require recipes-security/optee/optee-client.inc

SRCREV = "d221676a58b305bddbf97db00395205b3038de8e"

inherit pkgconfig
DEPENDS += "util-linux"
EXTRA_OEMAKE += "PKG_CONFIG=pkg-config"

# Diff between inc file from 4.1.0 to 4.4.0

inherit useradd
SRC_URI += " file://optee-udev.rules "

do_install:append() {
    install -d ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${WORKDIR}/optee-udev.rules ${D}${sysconfdir}/udev/rules.d/optee.rules
}

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system teeclnt"

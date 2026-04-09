FILESEXTRAPATHS:prepend := "${THISDIR}/optee-client-4.1.0:"
require optee-compatible-4.1.0.inc
inherit useradd

SRC_URI:append = " file://optee-udev.rules"

do_install:append() {
    install -d ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${WORKDIR}/optee-udev.rules ${D}${sysconfdir}/udev/rules.d
}

USERADD_PACKAGES = "${PN}"

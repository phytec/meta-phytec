SUMMARY = "fbtest"
DESCRIPTION = " This program supports you with adjusting display settings."
HOMEPAGE = "http://www.pengutronix.de/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=ea5bed2f60d357618ca161ad539f7c0a"

PR = "r1"

DEPENDS += "linux-libc-headers"

SRC_URI = "http://www.pengutronix.de/software/ptxdist/temporary-src/${BPN}-${PV}.tar.gz \
           file://0001-provide-a-pre-generated-penguin.c-to-get-rid-of-this.patch \
           file://0004-remove-asm-page.h-use-sysconf.patch \
           file://repair-make-rules.patch \
"

SRC_URI[md5sum] = "d9dc61e96edb60dc52491ce3a5d5185c"
SRC_URI[sha256sum] = "d215a038eca53c99d0a8ef2b689b3b940b08c39516aa472afbfb5f3cd744118f"

do_compile () {
    make
}

do_install() {
    install -d ${D}${base_sbindir}
    install -m 744 ${B}/fbtest ${D}${base_sbindir}/fbtest
}

DESCRIPTION = "bumbRTS is a test app for uart flow control testing"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING.GPLv2;md5=751419260aa954499f7abaabaa882bbe"
SECTION = "devel"

SRC_URI = "\
    file://bumpRTS.c \
    file://COPYING.GPLv2 \
"
S = "${UNPACKDIR}"

do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} -o bumpRTS bumpRTS.c
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 bumpRTS ${D}${bindir}
}
FILES:${PN} = "${bindir}/bumpRTS"

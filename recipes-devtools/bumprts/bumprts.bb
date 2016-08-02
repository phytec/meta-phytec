# Copyright (C) 2014 Stefan Müller-Klieser <s.mueller-klieser@phytec.de>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "bumbRTS is a test app for uart flow control testing"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING.GPLv2;md5=751419260aa954499f7abaabaa882bbe"
SECTION = "devel"

SRC_URI = "\
    file://bumpRTS.c \
    file://COPYING.GPLv2 \
"
S = "${WORKDIR}"

do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} -o bumpRTS bumpRTS.c
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 bumpRTS ${D}${bindir}
}
FILES_${PN} = "${bindir}/bumpRTS"

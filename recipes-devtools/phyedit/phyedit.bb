# Copyright (C) 2014 Stefan Müller-Klieser <s.mueller-klieser@phytec.de>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "phyedit is a tool for am335x to access the ethernet phy's \
                mdio registers."
HOMEPAGE = "http://www.phytec.de"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING.GPLv2;md5=751419260aa954499f7abaabaa882bbe"
SECTION = "devel"
PR = "r0"

SRC_URI = "\
file://phyedit.c \
file://COPYING.GPLv2 \
"
S = "${WORKDIR}"

do_compile() {
	${CC} ${CFLAGS} ${LDFLAGS} -o phyedit phyedit.c
}

do_install() {
	install -d ${D}${bindir}
	install -m 0755 phyedit ${D}${bindir}
}

FILES_${PN} = "${bindir}"

COMPATIBLE_MACHINE = "ti33x"

DESCRIPTION = "Tool to test RS485 interface in half duplex mode on target side"
HOMEPAGE = "http://www.phytec.de"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c748d81368c9a87fff9317b09edacfee"
SECTION = "devel"

SRC_URI = " \
    file://rs485test.c \
    file://LICENSE \
"

S = "${WORKDIR}"

do_compile() {
    ${CC} ${CFLAGS} rs485test.c -o rs485test ${LDFLAGS}
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 rs485test ${D}${bindir}
}

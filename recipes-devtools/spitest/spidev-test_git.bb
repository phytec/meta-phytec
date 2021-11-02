DESCRIPTION = "Spidev test application"
HOMEPAGE = "https://github.com/rm-hull/spidev-test"
SECTION = "utils"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a23a74b3f4caf9616230789d94217acb"
SRCREV = "0b7ecd60c56de6eb36ed553cfa9ebecf34aea8c1"

SRC_URI = "git://github.com/rm-hull/spidev-test.git;branch=master;protocol=https"

S = "${WORKDIR}/git"


do_compile() {
    ${CC} ${CFLAGS} spidev_test.c -o spidev_test ${LDFLAGS}
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 spidev_test ${D}${bindir}
}


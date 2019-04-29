DESCRIPTION = "A Freescale tool to profile memory controller bandwidth on i.MX6"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://mmdc.c;beginline=5;endline=12;md5=49e09642208485988b5c22074c896653"

SRC_URI = "git://github.com/FrankBau/mmdc;protocol=https \
           file://0001-mmdc.c-Fix-revision-check-against-old-kernel-version.patch \
           file://0002-Makefile-fix-build-error-for-clean-task.patch \
           "

PV = "1.0+git${SRCPV}"
SRCREV = "603e23e1c8e34773218e1820a1d17164ab9d9732"

S = "${WORKDIR}/git"

PATCHTOOL = "git"

do_compile () {
    ${CC} ${CFLAGS} ${LDFLAGS} -o mmdc mmdc.c
}

do_install () {
    install -d ${D}${bindir}
    install -m 0755 mmdc ${D}${bindir}
}

COMPATIBLE_MACHINE = "mx6"

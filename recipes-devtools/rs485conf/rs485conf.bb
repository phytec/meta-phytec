DESCRIPTION = "Application to configure RS485 specific settings"
HOMEPAGE = "https://github.com/mniestroj/rs485conf"
SECTION = "utils"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ccd0d515ae4a40d00b51fa2f87043685"
SRCREV = "5c8d00cf70950fab3454549b81dea843d844492a"

SRC_URI = "git://github.com/mniestroj/rs485conf.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

do_compile() {
    oe_runmake CC="${CC}" all
}

do_install() {
    # project's makefile uses configurable PREFIX, but has 'sbin' hardcoded, so replace
    sed -i "s#\$(PREFIX)/sbin#${D}${bindir}#g" "${S}/Makefile"
    mkdir -p "${D}${bindir}"
    oe_runmake install
}

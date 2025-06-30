DESCRIPTION = "Application to configure RS485 specific settings"
HOMEPAGE = "https://github.com/mniestroj/rs485conf"
SECTION = "utils"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ccd0d515ae4a40d00b51fa2f87043685"
SRCREV = "37a54432f5b4aae4ab100125f9ec0b42c72e6e5b"

SRC_URI = "git://github.com/mniestroj/rs485conf.git;branch=master;protocol=https"

do_compile() {
    oe_runmake CC="${CC}" all
}

do_install() {
    # project's makefile uses configurable PREFIX, but has 'sbin' hardcoded, so replace
    sed -i "s#\$(PREFIX)/sbin#${D}${bindir}#g" "${S}/Makefile"
    mkdir -p "${D}${bindir}"
    oe_runmake install
}

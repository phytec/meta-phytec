DESCRIPTION = "Simple serial tester"
HOMEPAGE = "https://github.com/nsekhar/serialcheck"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/nsekhar/serialcheck;protocol=https"

SRCREV = "45eb2ffa5378396e85432872833890b0a1cba872"

S = "${WORKDIR}/git"

inherit autotools

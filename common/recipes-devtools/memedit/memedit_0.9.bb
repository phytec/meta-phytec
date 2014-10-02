SUMMARY = "Memedit"
DESCRIPTION = " This program allows you to quickly display and change \
                memory content for testing purpose as well as for various \
                other development tasks."
HOMEPAGE = "http://www.pengutronix.de/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit autotools 

DEPENDS = "readline genparse-native libtool"

SRC_URI = "http://www.pengutronix.de/software/memedit/downloads/memedit-0.9.tar.gz"
SRC_URI[md5sum] = "fd8eb827c3072baf8678d9d33e5d6458"
SRC_URI[sha256sum] = "3ec778338d0d4f2351ad291b607ea1a4ac2391763cbf7870ff5357305c4a86ae"

do_configure_prepend () {
	( cd ${S}; ${S}/bootstrap )
}

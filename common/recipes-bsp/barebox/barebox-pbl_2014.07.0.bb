require barebox-pbl.inc

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=057bf9e50e1ca857d0eb97bfe4ba8e5d"
PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI = "http://barebox.org/download/${PN}-${PV}.tar.bz2 \
           file://defconfig"
SRC_URI[md5sum] = "a61b97a2fedebc808d4b182c2ca18a0c"
SRC_URI[sha256sum] = "d9e46dd3c68b04ab4cbc37135dc34907dbebef393da4b3e54abb203c295c1620"


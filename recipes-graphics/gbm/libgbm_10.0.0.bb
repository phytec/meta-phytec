DESCRIPTION = "Userspace interface for the kernel GBM services."
HOMEPAGE = "http://git.ti.com"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://gbm.c;beginline=1;endline=26;md5=b871c7f2f477df29ee4c0ec437b187f7"
DEPENDS = "libdrm"

inherit autotools pkgconfig

BRANCH = "next"

SRCREV = "96f37555c7e82a417b02051661377b10e6b3966e"

SRC_URI = "git://git.ti.com/glsdk/libgbm.git;protocol=git;branch=${BRANCH}"

S = "${WORKDIR}/git"
COMPATIBLE_MACHINE = "ti33x"

SUMMARY = "bayer2rgb color conversion with ARM neon support"
DESCRIPTION = "Faster bayer two RGB color format converstion implement with the ARM NEON architecture"
SECTION = "libs"
HOMEPAGE = "https://git.phytec.de/bayer2rgb-neon"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

BRANCH = "master"
SRC_URI = "git://git.phytec.de/${BPN};branch=${BRANCH}"

S = "${WORKDIR}/git"

# NOTE: Keep sha1sum in sync with recipe version and git tag
SRCREV = "7ff3d1ae404b73308c21844a15fbb4aecf2c4cc9"
PV = "0.3+git${SRCPV}"

PR = "r0"

inherit autotools ptest pkgconfig lib_package

DEPENDS += "gengetopt-native"

PACKAGES =+ "${PN}-tests"

FILES_${PN}-dbg += "${PTEST_PATH}/.debug"

PTEST_PATH = "${libdir}/bayer2rgb/testsuite"
FILES_${PN}-tests += "${libdir}/bayer2rgb/testsuite"
RDEPENDS_${PN}-tests += "bash"

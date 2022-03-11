SUMMARY = "bayer2rgb color conversion with ARM neon support"
DESCRIPTION = "Faster bayer two RGB color format conversion implement with the ARM NEON architecture"
SECTION = "libs"
HOMEPAGE = "https://git.phytec.de/bayer2rgb-neon"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

BRANCH = "master"
SRC_URI = "git://git.phytec.de/${BPN};branch=${BRANCH}"

S = "${WORKDIR}/git"

# NOTE: Keep sha1sum in sync with recipe version and git tag
SRCREV = "f7e81439634088f261926719d4061c77e27c14fc"
PV = "0.4+git${SRCPV}"

PR = "r0"

inherit autotools ptest pkgconfig lib_package tune_features_check

REQUIRED_TUNE_FEATURES = "neon"
REQUIRED_TUNE_FEATURES:aarch64 = ""

DEPENDS += "gengetopt-native"

PACKAGES =+ "${PN}-tests"

FILES:${PN}-dbg += "${PTEST_PATH}/.debug"

PTEST_PATH = "${libdir}/bayer2rgb/testsuite"
FILES:${PN}-tests += "${libdir}/bayer2rgb/testsuite"
RDEPENDS:${PN}-tests += "bash"

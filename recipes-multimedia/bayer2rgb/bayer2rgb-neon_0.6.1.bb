SUMMARY = "bayer2rgb color conversion with ARM neon support"
DESCRIPTION = "Faster bayer two RGB color format conversion implement with the ARM NEON architecture"
SECTION = "libs"
HOMEPAGE = "https://git.phytec.de/bayer2rgb-neon"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

BRANCH = "master"
SRC_URI = "git://git.phytec.de/${BPN};branch=${BRANCH}"

S = "${WORKDIR}/git"

# NOTE: Keep sha1sum in sync with recipe version and git tag
SRCREV = "15feb1115b4828488cc36d09f625e23e8b6a0ec5"

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

SUMMARY = "bayer2rgb color conversion with ARM neon support"
DESCRIPTION = "Faster bayer two RGB color format conversion implement with the ARM NEON architecture"
SECTION = "libs"
HOMEPAGE = "https://gitlab-ext.sigma-chemnitz.de/ensc/bayer2rgb.git"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

BRANCH = "master"
GIT_URL = "git://gitlab-ext.sigma-chemnitz.de/ensc/bayer2rgb.git;protocol=https"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

S = "${WORKDIR}/git"

UPSTREAM_CHECK_COMMITS = "1"

# NOTE: Keep sha1sum in sync with recipe version and git tag
SRCREV = "bc950b3398ba034fe5cc39f625796a6111cdb87f"
PV = "0.6.1+git${SRCPV}"

PR = "r0"

inherit autotools ptest pkgconfig lib_package tune_features_check

REQUIRED_TUNE_FEATURES = "neon"
REQUIRED_TUNE_FEATURES:aarch64 = ""

DEPENDS += "gengetopt-native"

PACKAGES =+ "${PN}-tests"

PTEST_PATH = "${libdir}/bayer2rgb/testsuite"
FILES:${PN}-tests += "${libdir}/bayer2rgb/testsuite"
FILES:${PN}-dbg += "${PTEST_PATH}/.debug"
RDEPENDS:${PN}-tests += "bash"

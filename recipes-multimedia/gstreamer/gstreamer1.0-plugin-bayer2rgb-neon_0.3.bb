SUMMARY = "bayer2rgb gstreamer plugin with neon support"
DESCRIPTION = "Gstreamer plugin using the bayer2rgb-neon library for fast color conversion"
SECTION = "multimedia"
HOMEPAGE = "https://git.phytec.de/gst-bayer2rgb-neon"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

BRANCH = "master"
SRC_URI = "git://git.phytec.de/gst-bayer2rgb-neon;branch=${BRANCH}"

S = "${WORKDIR}/git"

# NOTE: Keep sha1sum in sync with recipe version and git tag
SRCREV = "6f7b546fbab8ee9b9119684123aa86a235d7e6e9"
PV = "0.3+git${SRCPV}"

PR = "r0"

inherit pkgconfig autotools

DEPENDS += "bayer2rgb-neon gstreamer1.0-plugins-base gstreamer1.0"

FILES_${PN} += "${libdir}/gstreamer-*/*.so"
FILES_${PN}-dbg += "${libdir}/gstreamer-*/.debug"

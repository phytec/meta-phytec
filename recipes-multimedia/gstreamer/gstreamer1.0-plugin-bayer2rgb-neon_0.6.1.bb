SUMMARY = "bayer2rgb gstreamer plugin with neon support"
DESCRIPTION = "Gstreamer plugin using the bayer2rgb-neon library for fast color conversion"
SECTION = "multimedia"
HOMEPAGE = "https://git.phytec.de/gst-bayer2rgb-neon"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

BRANCH = "master"
SRC_URI = "git://git.phytec.de/gst-bayer2rgb-neon;branch=${BRANCH}"


# NOTE: Keep sha1sum in sync with recipe version and git tag
SRCREV = "69c3d779ceefe500ac5eae8e1e217a17f6d9bf38"

PR = "r0"

inherit pkgconfig autotools

DEPENDS += "bayer2rgb-neon gstreamer1.0-plugins-base gstreamer1.0"

FILES:${PN} += "${libdir}/gstreamer-*/*.so"
FILES:${PN}-dbg += "${libdir}/gstreamer-*/.debug"

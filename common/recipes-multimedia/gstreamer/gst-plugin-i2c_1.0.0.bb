# Copyright (C) 2015 Stefan Christ <s.christ@phytec.de>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "I2C GStreamer plug-in"
HOMEPAGE = "http://www.phytec.de"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SECTION = "multimedia"
DEPENDS += "gstreamer1.0 gstreamer1.0-plugins-base"

PR = "1"

SRC_URI = "git://git.phytec.de/gst-plugin-i2c;branch=master"
SRCREV = "fbf8f9b7aa002e96224a41194b1c8d0a617e775e"
PV = "1.0.0+git${SRCPV}"
S = "${WORKDIR}/git"

inherit autotools pkgconfig

FILES_${PN} += "${libdir}/gstreamer-1.0/*.so"
FILES_${PN}-dbg += "${libdir}/gstreamer-1.0/.debug"
FILES_${PN}-dev += "${libdir}/gstreamer-1.0/*.la"

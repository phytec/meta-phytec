# Copyright (C) 2015 Jan Remmet <j.remmet@phytec.de>
# Released under the MIT license (see COPYING.MIT for the terms)
SUMMARY = "barebox-state tool to interact with 'barebox-state'"
HOMEPAGE = "http://git.pengutronix.de/?p=tools/dt-utils.git"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=9ac2e7cff1ddaf48b6eab6028f23ef88"

DEPENDS = "udev"

SRCREV = "fb48280edcede9b97bf913654a19ea3b0ec45fac"
SRC_URI = "git://git.pengutronix.de/git/tools/dt-utils.git"
S = "${WORKDIR}/git"

PR = "r0"

inherit autotools pkgconfig

PACKAGES =+ "${PN}-dtblint ${PN}-barebox-state ${PN}-fdtdump"

FILES_${PN}-dtblint += "${bindir}/dtblint"
RDEPENDS_${PN}-dtblint += "${PN}"

FILES_${PN}-barebox-state += "${bindir}/barebox-state"
RDEPENDS_${PN}-barebox-state += "${PN}"

FILES_${PN}-fdtdump += "${bindir}/fdtdump"
RDEPENDS_${PN}-fdtdump += "${PN}"

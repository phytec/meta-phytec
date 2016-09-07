# Copyright (C) 2016 PHYTEC Messtechnik GmbH,
# Author: Wadim Egorov <w.egorov@phytec.de>

inherit phygittag
inherit buildinfo
include linux-common.inc

GIT_URL = "git://git.phytec.de/${PN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

PR = "${INC_PR}.0"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "056ac9e711ddf44e29b478c1b48564ee0f8391b5"

S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "firefly-rk3288-1"
SRC_URI_append_firefly-rk3288-1 = " file://rk-dbg-uart2.cfg"
COMPATIBLE_MACHINE .= "|phycore-rk3288-1"
SRC_URI_append_phycore-rk3288-1 = " file://rk-dbg-uart0.cfg"
COMPATIBLE_MACHINE .= "|phycore-rk3288-2"
SRC_URI_append_phycore-rk3288-2 = " file://rk-dbg-uart0.cfg"

# Copyright (C) 2016 PHYTEC Messtechnik GmbH,
# Author: Wadim Egorov <w.egorov@phytec.de>

inherit phygittag
inherit buildinfo
include linux-common.inc


GIT_URL = "git://git.phytec.de/${PN}"
SRC_URI = "${GIT_URL};branch=${BRANCH};protocol=git"

PR = "${INC_PR}.0"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "fa0c9fc9fe0b58b787ebbf2e765c103b7b0bf559"

S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "firefly-rk3288-1"
COMPATIBLE_MACHINE .= "|phycore-rk3288-1"

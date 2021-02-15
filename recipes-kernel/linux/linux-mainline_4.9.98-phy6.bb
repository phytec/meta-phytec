# Copyright (C) 2017 PHYTEC Messtechnik GmbH,
# Author: Wadim Egorov <w.egorov@phytec.de>

inherit phygittag
inherit buildinfo
include linux-common.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

#Skip this recipe if DISTRO_FEATURES contains the PREEMPT-RT value and
# a kernel with real-time is desired
python () {
    if 'preempt-rt' in d.getVar("DISTRO_FEATURES"):
        raise bb.parse.SkipPackage("Disable 'preempt-rt' in DISTRO_FEATURES!")
}

GIT_URL = "git://git.phytec.de/${BPN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

PR = "${INC_PR}.1"

RDEPENDS_kernel-modules_rk3288 += "cryptodev-module"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "49b8b0145d3413457eac31ac3b4c72001afb9693"

S = "${WORKDIR}/git"

INTREE_DEFCONFIG_rk3288 = "rk3288_phytec_defconfig"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .=  "phycore-rk3288-3"
COMPATIBLE_MACHINE .= "|phycore-rk3288-4"
COMPATIBLE_MACHINE .= ")$"

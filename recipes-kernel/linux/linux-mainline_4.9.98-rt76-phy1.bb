# Copyright (C) 2017 PHYTEC Messtechnik GmbH,
# Author: Daniel Schultz <d.schultz@phytec.de>

inherit phygittag
inherit buildinfo
include linux-common.inc

#Skip this recipe if DISTRO_FEATURES doesn't contain the PREEMPT-RT value and
# a kernel without real-time is desired
python () {
    if not 'preempt-rt' in d.getVar("DISTRO_FEATURES"):
        raise bb.parse.SkipPackage("Enable 'preempt-rt' in DISTRO_FEATURES!")
}

GIT_URL = "git://git.phytec.de/${PN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
SRC_URI_append = "\
    ${@oe.utils.conditional('DEBUG_BUILD','1','file://debugging.cfg','',d)} \
"

PR = "${INC_PR}.1"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "2138ed7efa5a8646378446e80194fdaee65e4701"

S = "${WORKDIR}/git"

INTREE_DEFCONFIG_rk3288 = "rk3288_rt_phytec_defconfig"

COMPATIBLE_MACHINE .= "|phycore-rk3288-3"

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
SRC_URI_append_ti33x = "\
    ${@bb.utils.contains('MACHINE_FEATURES', 'suspend', 'file://am335x-cm3.cfg', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', '3g', 'file://3g.cfg', '', d)} \
    file://blacklist-cpufreq_dt.cfg \
"

PR = "${INC_PR}.0"

RDEPENDS_kernel-modules_ti33x += "\
    cryptodev-module \
"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "1efa3a42d077b91150fdb8e89dfcad64be926f49"

S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "beagleboneblack-1"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-1"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-2"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-3"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-4"
COMPATIBLE_MACHINE .= "|phyboard-wega-r2-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-am335x-2"
COMPATIBLE_MACHINE .= "|phycore-am335x-3"
COMPATIBLE_MACHINE .= "|phycore-am335x-4"
COMPATIBLE_MACHINE .= "|phycore-am335x-5"
COMPATIBLE_MACHINE .= "|phycore-am335x-7"
COMPATIBLE_MACHINE .= "|phyboard-regor-am335x-1"
COMPATIBLE_MACHINE .= "|phyboard-regor-am335x-2"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-2"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-3"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-4"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-5"
COMPATIBLE_MACHINE .= "|phycore-emmc-am335x-1"

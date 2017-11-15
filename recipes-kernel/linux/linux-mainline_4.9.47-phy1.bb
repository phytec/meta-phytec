# Copyright (C) 2017 PHYTEC Messtechnik GmbH,
# Author: Wadim Egorov <w.egorov@phytec.de>

inherit phygittag
inherit buildinfo
include linux-common.inc

GIT_URL = "git://git.phytec.de/${PN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
SRC_URI_append = "\
    ${@base_conditional('DEBUG_BUILD','1','file://debugging.cfg','',d)} \
"
SRC_URI_append_ti33x = "\
    ${@bb.utils.contains('MACHINE_FEATURES', 'sgx', 'file://preempt_voluntary.cfg', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'suspend', 'file://am335x-cm3.cfg', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', '3g', 'file://3g.cfg', '', d)} \
"

PR = "${INC_PR}.0"

RDEPENDS_kernel-modules_rk3288 += "cryptodev-module"
RDEPENDS_kernel-modules_ti33x += "\
    ${@bb.utils.contains('MACHINE_FEATURES', 'suspend', 'amx3-cm3', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'sgx', 'ti-sgx-ddk-km', '', d)} \
    cryptodev-module \
"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "58482841720ca0616a6e46d54e23852af06582ac"

S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "phycore-rk3288-3"
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

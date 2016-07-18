# Copyright (C) 2014-2015 PHYTEC Messtechnik GmbH,
# Author: Stefan Mueller-Klieser <s.mueller-klieser@phytec.de>
DESCRIPTION =   "Linux Kernel provided and supported by PHYTEC based on TIs \
                Kernel for AM335x Family Boards. It includes support for \
                many IPs such as GPU, VPU and IPU."

inherit phygittag
inherit buildinfo
include linux-common.inc

GIT_URL = "git://git.phytec.de/${PN}"
SRC_URI = "${GIT_URL};branch=${BRANCH};protocol=git"
SRC_URI_append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', 'file://ipv6.cfg', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'file://systemd.cfg', '', d)} \
"
SRC_URI[vardeps] += "DISTRO_FEATURES"
S = "${WORKDIR}/git"

PR = "${INC_PR}.0"

# NOTE: Keep version in filename in sync with commit id!
SRCREV = "e3fe937f0464bf6b7bd980a909859f3839bd2847"

COMPATIBLE_MACHINE = "beagleboneblack-1"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-1"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-2"
COMPATIBLE_MACHINE .= "|phycore-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-am335x-2"
COMPATIBLE_MACHINE .= "|phycore-am335x-6"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-3"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-5"

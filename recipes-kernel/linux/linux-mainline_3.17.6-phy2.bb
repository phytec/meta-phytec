# Copyright (C) 2014 Stefan Mueller-Klieser <s.mueller-klieser@phytec.de>
# PHYTEC Messtechnik GmbH
inherit phygittag
inherit buildinfo
require common/recipes-kernel/linux/linux.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/linux-mainline:${THISDIR}/linux-mainline/features:"

GIT_URL = "git://git.phytec.de/${PN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
SRC_URI_append = " \
    file://defconfig \
    file://ipv6.cfg \
    file://systemd.cfg \
    file://resetdriver.cfg \
"
S = "${WORKDIR}/git"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "173838e5818b081c7601e9ba73a5785126013c13"

LINUX_VERSION = "${PV}"

KERNEL_LOCALVERSION = "-${BSP_VERSION}"

COMPATIBLE_MACHINE  =  "phyflex-imx6-1"
COMPATIBLE_MACHINE .= "|phyflex-imx6-2"
COMPATIBLE_MACHINE .= "|phycard-imx6-1"
COMPATIBLE_MACHINE .= "|phyboard-alcor-imx6-1"
COMPATIBLE_MACHINE .= "|phyboard-subra-imx6-1"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-1"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-2"

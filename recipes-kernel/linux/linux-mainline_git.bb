# Copyright (C) 2014 Stefan Mueller-Klieser <s.mueller-klieser@phytec.de>
# PHYTEC Messtechnik GmbH
inherit buildinfo
require common/recipes-kernel/linux/linux.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/linux-mainline:${THISDIR}/linux-mainline/features:"

# tag revision (NOTE: Keep TAG, PV and SRCREV in sync!)
BRANCH = "v3.17.6-phy"
TAG = "v3.17.6-phy1"
SRCREV = "b866874890884ddf526b8225e706f46c5856c698"

TAG_phyboard-mira-imx6-1 = "v3.17.6-phy2"
SRCREV_phyboard-mira-imx6-1 = "173838e5818b081c7601e9ba73a5785126013c13"

TAG_phyboard-mira-imx6-2 = "v3.17.6-phy2"
SRCREV_phyboard-mira-imx6-2 = "173838e5818b081c7601e9ba73a5785126013c13"

#SRCREV = "${TAG}"
# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
PV = "${@d.getVar('TAG').lstrip('v')}"
LINUX_VERSION ?= "${PV}"

GIT_URL = "git://git.phytec.de/${PN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
SRC_URI_append = " \
    file://defconfig \
    file://ipv6.cfg \
    file://systemd.cfg \
    file://resetdriver.cfg \
"

KERNEL_LOCALVERSION = "-${BSP_VERSION}"

COMPATIBLE_MACHINE_mx6 = "(mx6)"

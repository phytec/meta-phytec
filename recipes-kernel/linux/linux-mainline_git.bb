# Copyright (C) 2014 Stefan Mueller-Klieser <s.mueller-klieser@phytec.de>
# PHYTEC Messtechnik GmbH
require common/recipes-kernel/linux/linux.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/linux-mainline:${THISDIR}/linux-mainline/features:"

SRC_URI = "git://git.phytec.de/linux-mainline;branch=${BRANCH}"
SRC_URI_append = " \
    file://defconfig \
    file://ipv6.cfg \
    file://systemd.cfg \
    file://resetdriver.cfg \
"

LINUX_VERSION ?= "3.17.6-phy1"
SRCREV = "b866874890884ddf526b8225e706f46c5856c698"
BRANCH ?= "v3.17.6-phy"

BRANCH_phyboard-mira-imx6-1 = "v3.17.6-phy"
LINUX_VERSION_phyboard-mira-imx6-1 = "3.17.6-phy2"
SRCREV_phyboard-mira-imx6-1 = "173838e5818b081c7601e9ba73a5785126013c13"

BRANCH_phyboard-mira-imx6-2 = "v3.17.6-phy"
LINUX_VERSION_phyboard-mira-imx6-2 = "3.17.6-phy2"
SRCREV_phyboard-mira-imx6-2 = "173838e5818b081c7601e9ba73a5785126013c13"

PV = "${LINUX_VERSION}"
KERNEL_LOCALVERSION = "-${BSP_VERSION}"

COMPATIBLE_MACHINE_mx6 = "(mx6)"

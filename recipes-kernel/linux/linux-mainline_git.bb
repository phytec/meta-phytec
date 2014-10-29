# Copyright (C) 2014 Stefan Mueller-Klieser <s.mueller-klieser@phytec.de>
# PHYTEC Messtechnik GmbH
require common/recipes-kernel/linux/linux.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/linux-mainline:${THISDIR}/linux-mainline/features:"

SRC_URI = "git://git.phytec.de/linux-mainline;branch=${BRANCH}"
SRC_URI_append = " \
    file://defconfig \
"

SRCREV = "${AUTOREV}"
PV = "${LINUX_VERSION}-git${SRCPV}"

LINUX_VERSION ?= "3.17.1-phy"
BRANCH ?= "v3.17.1-phy"

COMPATIBLE_MACHINE_mx6 = "(mx6)"

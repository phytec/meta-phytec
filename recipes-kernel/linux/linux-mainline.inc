# Copyright (C) 2014 Stefan Mueller-Klieser <s.mueller-klieser@phytec.de>
# PHYTEC Messtechnik GmbH
DESCRIPTION = "Linux Kernel provided and supported by PHYTEC based on Linus Kernel"

require common/recipes-kernel/linux/linux.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/defconfigs:${THISDIR}/defconfigs/features:"

SRCBRANCH_ti33x ?= "master"
SRC_URI_ti33x = "${GITSERVER}/linux-mainline;branch=${SRCBRANCH}"
SRCREV = "${AUTOREV}"
PV = "${LINUX_VERSION}-git${SRCPV}"
S = "${WORKDIR}/git"
COMPATIBLE_MACHINE = "(ti33x)"

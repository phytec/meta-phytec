# Copyright (C) 2014 Stefan Mueller-Klieser <s.mueller-klieser@phytec.de>
# PHYTEC Messtechnik GmbH
DESCRIPTION =   "Linux Kernel provided and supported by PHYTEC based on TIs \
                Kernel for AM335x Family Boards. It includes support for \
                many IPs such as GPU, VPU and IPU."

require common/recipes-kernel/linux/linux.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/defconfigs-ti:${THISDIR}/defconfigs-ti/features:"

SRCBRANCH_ti33x ?= "master"
SRC_URI_ti33x = "git://${HOME}/linux-ti;branch=${SRCBRANCH}"
SRCREV = "${AUTOREV}"
PV = "${LINUX_VERSION}-git${SRCPV}"
S = "${WORKDIR}/git"
COMPATIBLE_MACHINE = "(ti33x)"

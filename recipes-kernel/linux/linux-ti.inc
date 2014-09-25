# Copyright (C) 2014 Stefan Mueller-Klieser <s.mueller-klieser@phytec.de>
# PHYTEC Messtechnik GmbH
DESCRIPTION =   "Linux Kernel provided and supported by PHYTEC based on TIs \
                Kernel for AM335x Family Boards. It includes support for \
                many IPs such as GPU, VPU and IPU."

require common/recipes-kernel/linux/linux.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/defconfigs-ti:${THISDIR}/defconfigs-ti/features:"

BRANCH ??= "master"
SRC_URI = "${GITSERVER}/linux-ti;branch=${BRANCH}"
SRCREV = "${AUTOREV}"
LINUX_VERION ??= "UNASSIGNED"
PV = "${LINUX_VERSION}-git${SRCPV}"
COMPATIBLE_MACHINE_ti33x = "(ti33x)"

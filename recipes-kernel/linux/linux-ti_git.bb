## Copyright (C) 2014 Stefan Mueller-Klieser <s.mueller-klieser@phytec.de>
# PHYTEC Messtechnik GmbH
DESCRIPTION =   "Linux Kernel provided and supported by PHYTEC based on TIs \
                Kernel for AM335x Family Boards. It includes support for \
                many IPs such as GPU, VPU and IPU."

require common/recipes-kernel/linux/linux.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/linux-ti/defconfigs:${THISDIR}/linux-ti/features:"

SRC_URI = "git://git.phytec.de/linux-ti;branch=${BRANCH}"
SRC_URI_append = " \
    file://defconfig \
    file://legacy-ptys.cfg \
"

COMPATIBLE_MACHINE_ti33x = "(ti33x)" 

LINUX_VERSION ?= "3.12.24-phy"
BRANCH ?= "v3.12.24-phy"
SRCREV = "${AUTOREV}"
PV = "${LINUX_VERSION}-git${SRCPV}"
KERNEL_LOCALVERSION = "-${BSP_VERSION}"

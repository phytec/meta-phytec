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
    file://ipv6.cfg \
    file://systemd.cfg \
"

COMPATIBLE_MACHINE_ti33x = "(ti33x)" 

LINUX_VERSION ?= "3.12.24-phy1"
BRANCH ?= "v3.12.24-phy"
#SRCREV = "${AUTOREV}"
#PV = "${LINUX_VERSION}-git${SRCPV}"
PV = "${LINUX_VERSION}"
SRCREV = "1a233b846375fe6d6b1aa521dec2973c5058d9f0"
KERNEL_LOCALVERSION = "-${BSP_VERSION}"

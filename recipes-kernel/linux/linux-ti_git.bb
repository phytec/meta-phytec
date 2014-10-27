## Copyright (C) 2014 Stefan Mueller-Klieser <s.mueller-klieser@phytec.de>
# PHYTEC Messtechnik GmbH
DESCRIPTION =   "Linux Kernel provided and supported by PHYTEC based on TIs \
                Kernel for AM335x Family Boards. It includes support for \
                many IPs such as GPU, VPU and IPU."

require common/recipes-kernel/linux/linux.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/linux-ti-git/defconfigs:${THISDIR}/linux-ti-git/features:"

SRC_URI = "git://git.phytec.de/linux-ti;branch=${BRANCH}"
SRC_URI_append = " \
    file://defconfig \
"
COMPATIBLE_MACHINE_ti33x = "(ti33x)" 

LINUX_VERSION ?= "3.12.24-phy"
BRANCH ?= "v3.12.24-phy"
SRCREV = "${AUTOREV}"
PV = "${LINUX_VERSION}-git${SRCPV}"

#SRCREV_phyflex-am335x-1 = ""
#PV = "${LINUX_VERSION}"
#SRCREV_phycore-am335x-1 = ""
#SRCREV_phyboard-wega-1

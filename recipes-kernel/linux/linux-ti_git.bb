## Copyright (C) 2014 Stefan Mueller-Klieser <s.mueller-klieser@phytec.de>
# PHYTEC Messtechnik GmbH
DESCRIPTION =   "Linux Kernel provided and supported by PHYTEC based on TIs \
                Kernel for AM335x Family Boards. It includes support for \
                many IPs such as GPU, VPU and IPU."

require common/recipes-kernel/linux/linux.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/linux-ti-git/defconfigs:${THISDIR}/linux-ti-git/features:"

SRC_URI = " \
  ${GITSERVER}/linux-ti;branch=${BRANCH} \
  file://defconfig \
"
COMPATIBLE_MACHINE_ti33x = "(ti33x)" 

LINUX_VERSION ?= "3.12.24-phy"
PV = "${LINUX_VERSION}"
BRANCH ?= "v3.12.24-phy"

#phyflex am335x
COMPATIBLE_MACHINE_phyflex-am335x-1 = "(phyflex-am335x-1)"
#SRCREV_phyflex-am335x-1 = ""


#phycore am335x
COMPATIBLE_MACHINE_phycore-am335x-1 = "(phycore-am335x-1)"

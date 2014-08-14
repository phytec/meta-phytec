# Copyright (C) 2014 Stefan Mueller-Klieser <s.mueller-klieser@phytec.de>
# PHYTEC Messtechnik GmbH
DESCRIPTION =   "Linux Kernel provided and supported by PHYTEC based on TIs \
                Kernel for AM335x Family Boards. It includes support for \
                many IPs such as GPU, VPU and IPU."
SECTION = "kernel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit kernel
require recipes-kernel/linux/linux-dtb.inc

DEPENDS += "lzop-native bc-native"

FILESEXTRAPATHS_prepend := "${THISDIR}/defconfigs:${THISDIR}/defconfigs/features:"

LINUX_VERSION ?= "3.12.0-tiphy"
LOCALVERSION ?= "${DISTRO}"
SRCBRANCH_am335x = "master"

SRC_URI_am335x = "git://${HOME}/linux-am335x;branch=${SRCBRANCH}"
SRCREV = "${AUTOREV}"
PV = "${LINUX_VERSION}-git${SRCPV}"
S = "${WORKDIR}/git"
COMPATIBLE_MACHINE = "(am335x)"

#phyflex am335x
SRC_URI_append_phyflex-am335x-2013-01 = " file://defconfig"
SRCBRANCH_phyflex-am335x-2013-01 = "WIP/smk/Unified-AM335x-PD14.1.0_3_1"
COMPATIBLE_MACHINE_phyflex-am335x-2013-01 = "(phyflex-am335x-2013-01)"

#phycore am335x
SRC_URI_append_phycore-am335x-2012-01 = " file://defconfig"
SRCBRANCH_phycore-am335x-2012-01 = "WIP/smk/Unified-AM335x-PD14.1.0_3_1"
COMPATIBLE_MACHINE_phycore-am335x-2012-01 = "(phycore-am335x-2012-01)"

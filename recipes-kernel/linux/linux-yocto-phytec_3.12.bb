# Copyright (C) 2014 Stefan Mueller-Klieser <s.mueller-klieser@phytec.de>
# PHYTEC Messtechnik GmbH
DESCRIPTION =   "Linux Kernel provided and supported by PHYTEC based on TIs \
                Kernel for AM335x Family Boards. It includes support for \
                many IPs such as GPU, VPU and IPU."

require recipes-kernel/linux/linux-yocto.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/defconfigs:${THISDIR}/defconfigs/features:"

LINUX_VERSION ?= "3.12.0-tiphy"
LOCALVERSION ?= "+poky"
KBRANCH_am335x = "master"

SRC_URI_am335x = "git://${HOME}/linux-am335x;branch=${KBRANCH}"
SRCREV = "${AUTOREV}"
PV = "${LINUX_VERSION}-git${SRCPV}"
S = "${WORKDIR}/git"
COMPATIBLE_MACHINE = "(am335x)"

# Functionality flags
#KERNEL_EXTRA_FEATURES ?= "features/netfilter/netfilter.scc"
#KERNEL_FEATURES_append = " ${KERNEL_EXTRA_FEATURES}"
#KERNEL_FEATURES_append = " ${@bb.utils.contains("TUNE_FEATURES", "mx32", " cfg/x32.scc", "" ,d)}"

# config for phyflex_am335x_2013_01 
SRC_URI_append_phyflex-am335x-2013-01 = " file://defconfig"
#    features/noswap.cfg \
#"
KBRANCH_phyflex-am335x-2013-01 = "WIP/smk/Unified-AM335x-PD14.1.0_3_1"
#SRCREV = "c2c147d5e665e50496b604d8fc969edddc484ebe"
#KERNEL_FEATURES_append_phyflex-am335x-2013-01 = " features/noswap.cfg"
COMPATIBLE_MACHINE_phyflex-am335x-2013-01 = "(phyflex-am335x-2013-01)"

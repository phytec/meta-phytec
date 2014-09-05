# Copyright (C) 2014 Stefan Mueller-Klieser <s.mueller-klieser@phytec.de>
# PHYTEC Messtechnik GmbH
require recipes-kernel/linux/linux-ti.inc

SRC_URI_append_ti33x = " file://defconfig"
LINUX_VERSION ?= "3.12.24-phy"
SRCBRANCH_ti33x ??= "3.12.y-phy"

#phyflex am335x
SRCBRANCH_phyflex-am335x-1 = "v3.12.24-phy"
COMPATIBLE_MACHINE_phyflex-am335x-1 = "(phyflex-am335x-1)"

#phycore am335x
SRCBRANCH_phycore-am335x-1 = "WIP/smk/Unified-AM335x-PD14.1.0_3_1"
COMPATIBLE_MACHINE_phycore-am335x-1 = "(phycore-am335x-1)"

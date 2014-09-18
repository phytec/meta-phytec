# Copyright (C) 2014 Stefan Mueller-Klieser <s.mueller-klieser@phytec.de>
# PHYTEC Messtechnik GmbH
require recipes-kernel/linux/linux-ti.inc

SRC_URI_append_ti33x = " \
    file://defconfig \
    file://systemd.cfg \
"
LINUX_VERSION_ti33x ?= "3.12.24-phy"
SRCBRANCH_ti33x ?= "3.12.24-phy"

#phyflex am335x
COMPATIBLE_MACHINE_phyflex-am335x-1 = "(phyflex-am335x-1)"

#phycore am335x
COMPATIBLE_MACHINE_phycore-am335x-1 = "(phycore-am335x-1)"

# Copyright (C) 2014 Stefan Mueller-Klieser <s.mueller-klieser@phytec.de>
# PHYTEC Messtechnik GmbH

require common/recipes-kernel/linux/linux-mainline.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/defconfigs-mainline:${THISDIR}/defconfigs-mainline/features:"

LINUX_VERSION ?= "3.17.1-phy"
BRANCH ?= "v3.17.1-phy"

#phyflex imx6
COMPATIBLE_MACHINE_phyflex-imx6-1 = "(phyflex-imx6-1)"

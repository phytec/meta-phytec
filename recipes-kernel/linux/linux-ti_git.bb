## Copyright (C) 2014 Stefan Mueller-Klieser <s.mueller-klieser@phytec.de>
# PHYTEC Messtechnik GmbH
DESCRIPTION =   "Linux Kernel provided and supported by PHYTEC based on TIs \
                Kernel for AM335x Family Boards. It includes support for \
                many IPs such as GPU, VPU and IPU."

inherit buildinfo
require common/recipes-kernel/linux/linux.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/linux-ti/defconfigs:${THISDIR}/linux-ti/features:"

GIT_URL = "git://git.phytec.de/${PN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
SRC_URI_append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', 'file://ipv6.cfg', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'file://systemd.cfg', '', d)} \
"
# As of Version 5 of the TI sgx graphic stack, the opengl modules need all of the
# TI kernel Graphics drivers even the legacy da8xx driver
SRC_URI_append += "${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'file://da8xx-fb.cfg', '', d)}"

# tag revision (NOTE: Keep TAG, PV and SRCREV in sync!)
BRANCH = "v3.12.24-phy"
TAG = "v3.12.24-phy1"
SRCREV = "1a233b846375fe6d6b1aa521dec2973c5058d9f0"
#SRCREV = "${TAG}", this will check the online repo for the commit id of the TAG
# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
PV = "${@d.getVar('TAG').lstrip('v')}"
LINUX_VERSION ?= "${PV}"

# use this to build the HEAD of the git branch
#SRCREV = "${AUTOREV}"
#PV = "${LINUX_VERSION}-git${SRCPV}"

KERNEL_LOCALVERSION = "-${BSP_VERSION}"

COMPATIBLE_MACHINE = "(ti33x)" 

## Copyright (C) 2014 Stefan Mueller-Klieser <s.mueller-klieser@phytec.de>
# PHYTEC Messtechnik GmbH
DESCRIPTION =   "Linux Kernel provided and supported by PHYTEC based on TIs \
                Kernel for AM335x Family Boards. It includes support for \
                many IPs such as GPU, VPU and IPU."

inherit phygittag
inherit buildinfo
require common/recipes-kernel/linux/linux.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/linux-ti/defconfigs:${THISDIR}/linux-ti/features:"

GIT_URL = "git://git.phytec.de/${PN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
SRC_URI_append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', 'file://ipv6.cfg', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'file://systemd.cfg', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'mtd-tests', 'file://mtd-tests.cfg', '', d)} \
"
# As of Version 5 of the TI sgx graphic stack, the opengl modules need all of the
# TI kernel Graphics drivers even the legacy da8xx driver
SRC_URI_append += "${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'file://da8xx-fb.cfg', '', d)}"
SRC_URI[vardeps] += "DISTRO_FEATURES"

KERNEL_LOCALVERSION = "-${BSP_VERSION}"
LINUX_VERSION = "${PV}"

# NOTE: Keep version in filename in sync with commit id!
SRCREV = "729ee9bd145656cceb65356fc07c527283b3116a"

COMPATIBLE_MACHINE = "beagleboneblack-1"
SRC_URI_append_beagleboneblack-1 = " file://beagleboneblack-1.cfg"
COMPATIBLE_MACHINE .= "|phyboard-maia-am335x-1"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-1"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-2"
COMPATIBLE_MACHINE .= "|phycore-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-am335x-2"
COMPATIBLE_MACHINE .= "|phyflex-am335x-1"

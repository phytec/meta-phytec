inherit kernel
inherit phygittag buildinfo kconfig
include linux-common.inc
include linux-barebox-dt-overlays.inc

# NOTE: This recipe only works PHYTEC-internally! Access to the -phynext
# branches is limited to internal developers.
GIT_URL = "git://git@git.phytec.de/linux-phytec-dev.git;protocol=ssh"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

PR = "${INC_PR}.0"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-phytec-6.6:"
SRC_URI:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'file://lxc.cfg', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'file://oci.cfg', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'file://systemd.cfg', '', d)} \
    ${@bb.utils.contains('DEBUG_BUILD', '1', 'file://debugging.cfg', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'tpm2', 'file://tpm2.cfg', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'caam', 'file://caam.cfg', '', d)} \
"

SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git"

INTREE_DEFCONFIG:ti33x = "am335x_phytec_defconfig"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .=  "phyboard-regor-am335x-1"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-1"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-2"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-3"
COMPATIBLE_MACHINE .= "|phyboard-wega-r2-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-am335x-2"
COMPATIBLE_MACHINE .= "|phycore-am335x-3"
COMPATIBLE_MACHINE .= "|phycore-am335x-4"
COMPATIBLE_MACHINE .= "|phycore-am335x-5"
COMPATIBLE_MACHINE .= "|phycore-emmc-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-2"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-3"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-4"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-5"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-6"
COMPATIBLE_MACHINE .= ")$"

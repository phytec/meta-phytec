inherit kernel
inherit phygittag buildinfo kconfig
include linux-common.inc
include linux-barebox-dt-overlays.inc

GIT_URL = "git://github.com/phytec/linux-phytec.git;protocol=https"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

PR = "${INC_PR}.0"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-phytec-6.6:"
SRC_URI:append = " \
  ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'file://lxc.cfg', '', d)} \
  ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'file://oci.cfg', '', d)} \
  ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'file://systemd.cfg', '', d)} \
  ${@bb.utils.contains('DEBUG_BUILD', '1', 'file://debugging.cfg', '', d)} \
  ${@bb.utils.contains('MACHINE_FEATURES', 'tpm2', 'file://tpm2.cfg', '', d)} \
  ${@bb.utils.contains('MACHINE_FEATURES', 'caam', 'file://caam.cfg', '',   d)} \
"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "53e4f08dabfe105be161c2e3ed49997bc02033fe"

S = "${WORKDIR}/git"

INTREE_DEFCONFIG = "imx_v6_v7_defconfig imx6_phytec_distro.config imx6_phytec_machine.config imx6_phytec_platform.config"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .=  "phyboard-segin-imx6ul-2"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-3"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-4"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-5"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-6"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-7"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-8"

COMPATIBLE_MACHINE .= "|phygate-tauri-s-imx6ul-1"
COMPATIBLE_MACHINE .= ")$"

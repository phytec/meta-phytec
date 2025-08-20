inherit kernel-deploy-oftree

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-yocto-dev:"
SRC_URI:append = " \
  file://audit-disable.scc \
"

KERNEL_FEATURES += "audit-disable.scc"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .=  "phyboard-polis-imx8mm-5"
COMPATIBLE_MACHINE .= "|phyboard-pollux-imx8mp-3"
COMPATIBLE_MACHINE .= "|phygate-tauri-l-imx8mm-2"
COMPATIBLE_MACHINE .= "|imx8mp-libra-fpsc-1"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx93-2"
COMPATIBLE_MACHINE .= "|phyboard-nash-imx93-1"
COMPATIBLE_MACHINE .= ")$"

ARCH = "arm64"

KBUILD_DEFCONFIG = "defconfig"
KCONFIG_MODE = "alldefconfig"

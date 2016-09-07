#Taken from meta-rockchip Layer, original name:rk3288.inc

require conf/machine/include/tune-cortexa17.inc
require conf/machine/include/soc-family.inc

SOC_FAMILY = "rk3288"
DEFAULTTUNE = "cortexa17thf-neon"

PREFERRED_PROVIDER_virtual/kernel = "linux-mainline"
KERNEL_IMAGETYPE ?= "zImage"
INTREE_DEFCONFIG_pn-linux-mainline = "rk3288_phytec_defconfig"

BAREBOX_BIN = "images/barebox-rk3288-phycore-som.img"
INTREE_DEFCONFIG_pn-barebox = "rk3288_defconfig"
PREFERRED_PROVIDER_virtual/bootloader ?= "barebox"
PREFERRED_PROVIDER_virtual/prebootloader ?= "u-boot-spl"

SERIAL_CONSOLES = "115200;ttyS0"

PREFERRED_PROVIDER_virtual/egl ?= "mali-t760-libs"
PREFERRED_PROVIDER_virtual/libgles1 ?= "mali-t760-libs"
PREFERRED_PROVIDER_virtual/libgles2 ?= "mali-t760-libs"

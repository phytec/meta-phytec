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

# Workaround: package.bbclass cannot extract dependency information from mali
# library automatically. SONAME is missing and libEGL and libGLES are only
# links to libmali.  With this the package 'qtbase' rdepends on
# 'mali-t760-libs' correctly.
ASSUME_SHLIBS += "libEGL.so:mali-t760-libs"
ASSUME_SHLIBS += "libGLESv1_CM.so:mali-t760-libs"
ASSUME_SHLIBS += "libGLESv2.so:mali-t760-libs"
ASSUME_SHLIBS += "libOpenCL.so:mali-t760-libs"

# rootfs images
# register image class for usage
IMAGE_CLASSES += "rk3288_sd_image-phy"

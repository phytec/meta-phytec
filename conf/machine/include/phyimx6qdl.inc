# Common settings for phytecs imx6qdl boards
DEFAULTTUNE ?= "cortexa9thf-neon"

include conf/machine/include/phyimx6.inc
include conf/machine/include/arm/armv7a/tune-cortexa9.inc

SERIAL_CONSOLES ?= "115200;ttymxc3"

# Sub-architecture support
MACHINE_SOCARCH = "${TUNE_PKGARCH}-mx6qdl"
PACKAGE_EXTRA_ARCHS:append = " ${MACHINE_SOCARCH}"

# cpu feature overwrite support
MACHINEOVERRIDES .= ":imxgpu3d"

# Use the expected value of the ubifs filesystem's volume name in the kernel
UBI_VOLNAME = "root"
# The biggest NANDs on current modules are 1GB. With current partition layout,
# this sums up to 7943 LEBs available for the ubifs
MKUBIFS_ARGS = "-F -m 2048 -e 126976 -c 8083"
UBINIZE_ARGS = "-m 2048 -p 128KiB -s 2048 -O 2048"

# mx6 GPU libraries
PREFERRED_PROVIDER_virtual/egl ?= "mesa"
PREFERRED_PROVIDER_virtual/libgles1 ?= "mesa"
PREFERRED_PROVIDER_virtual/libgles2 ?= "mesa"
PREFERRED_PROVIDER_virtual/libgl ?= "mesa"

# Firmware files
MACHINE_EXTRA_RRECOMMENDS:append:mx6q-generic-bsp = " firmware-imx-vpu-mx6q"
MACHINE_EXTRA_RRECOMMENDS:append:mx6dl-generic-bsp = " firmware-imx-vpu-mx6dl"
PREFERRED_VERSION_firmware-imx = "5.4"

# Install devicetree overlays
DT_OVERLAYS_INSTALL = " \
    imx6-cam-0-lvds.dtbo \
    imx6-vm009-0.dtbo \
    imx6-vm010-bw-0.dtbo \
    imx6-vm010-col-0.dtbo \
    imx6-vm011-bw-0.dtbo \
    imx6-vm011-col-0.dtbo \
    imx6-vm012-bw-0.dtbo \
    imx6-vm012-col-0.dtbo \
    imx6-vm016-0.dtbo \
    imx6-vm050-0.dtbo \
"

DT_OVERLAYS_INSTALL:append:phyboard-mira-imx6 = " \
    imx6-phy-adin1300.dtbo \
    imx6-phy-ksz9131.dtbo \
"

DT_OVERLAYS_INSTALL:append:phyboard-nunki-imx6 = " \
    imx6-phy-adin1300.dtbo \
    imx6-phy-ksz9131.dtbo \
"

KERNEL_DEVICETREE:append:phyboard-mira-imx6 = " \
    overlays/imx6-phy-adin1300.dtbo \
    overlays/imx6-phy-ksz9131.dtbo \
"

KERNEL_DEVICETREE:append:phyboard-nunki-imx6 = " \
    overlays/imx6-phy-adin1300.dtbo \
    overlays/imx6-phy-ksz9131.dtbo \
"

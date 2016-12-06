# Common settings for phytecs imx6qdl boards

include conf/machine/include/phyimx6.inc
include conf/machine/include/tune-cortexa9.inc

DEFAULTTUNE = "cortexa9hf-neon"

SERIAL_CONSOLES ?= "115200;ttymxc3"

# Sub-architecture support
MACHINE_SOCARCH = "${TUNE_PKGARCH}-mx6qdl"
PACKAGE_EXTRA_ARCHS_append = " ${MACHINE_SOCARCH}"

# cpu feature overwrite support
MACHINEOVERRIDES .= ":imxgpu3d"

# Use the expected value of the ubifs filesystem's volume name in the kernel
UBI_VOLNAME = "root"
# The biggest NANDs on current modules are 1GB. With current partition layout,
# this sums up to 7943 LEBs available for the ubifs
MKUBIFS_ARGS = "-F -m 2048 -e 126976 -c 8083"
UBINIZE_ARGS = "-m 2048 -p 128KiB -s 2048 -O 2048"

# mx6 GPU libraries
PREFERRED_PROVIDER_virtual/egl ?= "imx-gpu-viv"
PREFERRED_PROVIDER_virtual/libgles1 ?= "imx-gpu-viv"
PREFERRED_PROVIDER_virtual/libgles2 ?= "imx-gpu-viv"
PREFERRED_PROVIDER_virtual/libgl ?= "imx-gpu-viv"

# Firmware files
MACHINE_EXTRA_RRECOMMENDS_append_mx6q = " firmware-imx-vpu-mx6q"
MACHINE_EXTRA_RRECOMMENDS_append_mx6dl = " firmware-imx-vpu-mx6dl"

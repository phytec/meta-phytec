# Provides the i.MX common settings

include conf/machine/include/soc-family.inc

# Disable perf features as 2.6.35.3 fail to build otherwise
PERF_FEATURES_ENABLE = ""

# XServer
PREFERRED_PROVIDER_virtual/xserver = "xserver-xorg"
# We don't support a Xserver yet.
#XSERVER_DRIVER = "xf86-video-fbdev"
#XSERVER_DRIVER_mx6 = "xf86-video-imxfb-vivante"
#XSERVER = "xserver-xorg \
#           xf86-input-evdev \
#           ${XSERVER_DRIVER}"

# Ship kernel modules
MACHINE_EXTRA_RRECOMMENDS = "kernel-modules"

# Float-Point setting
DEFAULTTUNE ?= "cortexa9hf-neon"

# Sub-architecture support
MACHINE_SOCARCH = "${TUNE_PKGARCH}-mx6qdl"
PACKAGE_EXTRA_ARCHS_append = " ${MACHINE_SOCARCH}"

# Firmware
MACHINE_FIRMWARE ?= ""

# FIXME: Needs addition of firmware-imx of official BSPs
#MACHINE_FIRMWARE_append_mx35 = " firmware-imx-sdma-imx35"
#MACHINE_FIRMWARE_append_mx31 = " firmware-imx-sdma-imx31"
#MACHINE_FIRMWARE_append_mx27 = " firmware-imx-vpu-imx27"
#MACHINE_FIRMWARE_append_mx25 = " firmware-imx-sdma-imx25"

MACHINE_EXTRA_RRECOMMENDS += "${MACHINE_FIRMWARE}"

# mx6 GPU libraries
PREFERRED_PROVIDER_virtual/egl ?= "gpu-viv-bin-mx6q"
PREFERRED_PROVIDER_virtual/libgles1 ?= "gpu-viv-bin-mx6q"
PREFERRED_PROVIDER_virtual/libgles2 ?= "gpu-viv-bin-mx6q"
PREFERRED_PROVIDER_virtual/libgl ?= "gpu-viv-bin-mx6q"

# sd-card image
IMAGE_CLASSES += "image_types_fsl"

SERIAL_CONSOLE = "115200 ttymxc0"

KERNEL_IMAGETYPE = "zImage"

MACHINE_FEATURES = "apm usbgadget usbhost vfat"

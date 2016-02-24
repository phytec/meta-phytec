# Provides the i.MX common settings

include conf/machine/include/soc-family.inc

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

# mx6 GPU libraries
PREFERRED_PROVIDER_virtual/egl ?= "imx-gpu-viv"
PREFERRED_PROVIDER_virtual/libgles1 ?= "imx-gpu-viv"
PREFERRED_PROVIDER_virtual/libgles2 ?= "imx-gpu-viv"
PREFERRED_PROVIDER_virtual/libgl ?= "imx-gpu-viv"

# sd-card image
IMAGE_CLASSES += "image_types_fsl"

SERIAL_CONSOLE = "115200 ttymxc0"

KERNEL_IMAGETYPE = "zImage"

MACHINE_FEATURES = "apm usbgadget usbhost vfat"

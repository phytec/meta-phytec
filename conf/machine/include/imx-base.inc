# Provides the i.MX common settings

include conf/machine/include/fsl-default-settings.inc
include conf/machine/include/fsl-default-versions.inc
include conf/machine/include/fsl-default-providers.inc

include conf/machine/include/soc-family.inc

# Disable perf features as 2.6.35.3 fail to build otherwise
PERF_FEATURES_ENABLE = ""

# We don't support a Xserver yet.
#XSERVER_DRIVER = "xf86-video-fbdev"
#XSERVER_DRIVER_mx6 = "xf86-video-imxfb-vivante"
#XSERVER = "xserver-xorg \
#           xf86-input-evdev \
#           ${XSERVER_DRIVER}"

# Ship kernel modules
MACHINE_EXTRA_RRECOMMENDS = "kernel-modules"

# Float-Point setting
DEFAULTTUNE_mx6 ?= "cortexa9hf-neon"

# Sub-architecture support
MACHINE_SOCARCH ?= ""
MACHINE_SOCARCH_mx6q = "${TUNE_PKGARCH}-mx6qdl"
MACHINE_SOCARCH_mx6dl = "${TUNE_PKGARCH}-mx6qdl"
PACKAGE_EXTRA_ARCHS_append = " ${MACHINE_SOCARCH}"

# Firmware
MACHINE_FIRMWARE ?= ""

# FIXME: Needs addition of firmware-imx of official BSPs
#MACHINE_FIRMWARE_append_mx35 = " firmware-imx-sdma-imx35"
#MACHINE_FIRMWARE_append_mx31 = " firmware-imx-sdma-imx31"
#MACHINE_FIRMWARE_append_mx27 = " firmware-imx-vpu-imx27"
#MACHINE_FIRMWARE_append_mx25 = " firmware-imx-sdma-imx25"

MACHINE_EXTRA_RRECOMMENDS += "${MACHINE_FIRMWARE}"

# GStreamer 0.10 plugins
MACHINE_GSTREAMER_PLUGIN ?= "gst-fsl-plugin"

# GStreamer 1.0 plugins
MACHINE_GSTREAMER_1_0_PLUGIN ?= ""
MACHINE_GSTREAMER_1_0_PLUGIN_mx6 ?= "gstreamer1.0-plugins-imx-meta"

# mx6 GPU libraries
PREFERRED_PROVIDER_virtual/egl ?= "gpu-viv-bin-mx6q"
PREFERRED_PROVIDER_virtual/libgles1 ?= "gpu-viv-bin-mx6q"
PREFERRED_PROVIDER_virtual/libgles2 ?= "gpu-viv-bin-mx6q"
PREFERRED_PROVIDER_virtual/libgl ?= "gpu-viv-bin-mx6q"


SERIAL_CONSOLE = "115200 ttymxc0"

KERNEL_IMAGETYPE = "zImage"

MACHINE_FEATURES = "apm usbgadget usbhost vfat"

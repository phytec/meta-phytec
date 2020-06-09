# Common settings for PHYTEC's i.MX8 boards

PREFERRED_PROVIDER_virtual/bootloader= "u-boot-imx"
PREFERRED_PROVIDER_imx-boot="imx-boot-phytec"
PREFERRED_PROVIDER_u-boot-fw-utils = "libubootenv"
PREFERRED_PROVIDER_opencl-headers_imx = "imx-gpu-viv"

# Wic
IMAGE_CLASSES += "wic-helper"
IMAGE_CLASSES += "wic-imx8-helper"
IMAGE_BOOTFILES_DEPENDS ?= ""
IMAGE_BOOT_FILES += "oftree"
WKS_FILES_mx8 ?= "imx8-sdimage.wks"
WKS_FILES_mx8m ?= "imx8m-sdimage.wks"

# Default images to build
IMAGE_FSTYPES_mx8 = "tar.gz wic"

# vivante gpu
IMX_GPU_VERSION = "6.2.4.p4.0"
MACHINE_USES_VIVANTE_KERNEL_DRIVER_MODULE = "1"
# tensorflow lite workaround for old gpu viv
# library, TODO: remove when gpu viv is updated
ML_PKGS_mx8 = ""

# Common settings for phytecs imx8 boards

PREFERRED_PROVIDER_virtual/bootloader= "u-boot-imx"

#WIC
IMAGE_CLASSES += "wic-helper"
IMAGE_CLASSES += "wic-imx8-helper"
IMAGE_BOOTFILES_DEPENDS ?= ""
IMAGE_BOOT_FILES ?= "Image oftree ${KERNEL_DEVICETREE}"
WKS_FILES_mx8 ?= "imx8-sdimage.wks.in"
WKS_FILES_mx8m ?= "imx8m-sdimage.wks.in"

# default images to build
IMAGE_FSTYPES_mx8 = "tar.gz wic"

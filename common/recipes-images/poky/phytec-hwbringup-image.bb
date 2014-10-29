SUMMARY = "Phytecs hardwaretesting image"
DESCRIPTION = "A small image capable of allowing a device to boot and \
              check for hardware and software problems."
LICENSE = "MIT"
IMAGE_LINGUAS = " "

inherit core-image

IMAGE_ROOTFS_SIZE ?= "8192"

IMAGE_INSTALL = " \
    packagegroup-core-boot \
    packagegroup-hwtools \
    packagegroup-benchmark \
"

export IMAGE_BASENAME = "phytec-hwbringup"



SUMMARY = "Phytecs hardware testing image"
DESCRIPTION = "A small image capable of allowing a device to boot and \
               check for hardware problems."
LICENSE = "MIT"
inherit core-image

IMAGE_ROOTFS_SIZE ?= "8192"

IMAGE_INSTALL = " \
    packagegroup-core-boot \
    packagegroup-hwtools \
    packagegroup-benchmark \
    packagegroup-userland \
"

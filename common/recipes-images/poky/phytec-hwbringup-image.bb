SUMMARY = "Phytecs hardwaretesting image"
DESCRIPTION = "A small image capable of allowing a device to boot and \
              check for hardware and software problems."
LICENSE = "MIT"
IMAGE_LINGUAS = " "

inherit core-image

IMAGE_ROOTFS_SIZE ?= "8192"

#build barebox too
IMAGE_RDEPENDS += "barebox barebox-ipl"

IMAGE_INSTALL = " \
    packagegroup-core-boot \
    ${ROOTFS_PKGMANAGE_BOOTSTRAP} \
    ${CORE_IMAGE_EXTRA_INSTALL} \
    usbutils \
    ethtool \
    i2c-tools \
    alsa-utils \
    devmem2 \
    iw \
    bonnie++ \
    hdparm \
    iozone3 \
    iperf \
    lmbench \
    rt-tests \
    evtest \
    bc \
    tslib-conf \
    tslib-calibrate \
    tslib-tests \
    memedit \
    fb-test \
    phyedit \
"

export IMAGE_BASENAME = "phytec-hwbringup"



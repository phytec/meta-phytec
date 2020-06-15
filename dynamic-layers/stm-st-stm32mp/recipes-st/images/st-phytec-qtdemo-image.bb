SUMMARY =  "This image is designed to show development of a Qt application \
            running on the eglfs (GPU) or linuxfb (no GPU) single application backend."
LICENSE = "Proprietary"

include ./st-image.inc

inherit core-image distro_features_check

# let's make sure we have a good image..
CONFLICT_DISTRO_FEATURES = "x11 wayland"

IMAGE_LINGUAS = "en-us"

IMAGE_FEATURES += " \
    splash              \
    package-management  \
    ssh-server-dropbear \
    hwcodecs            \
    "

# Define ROOTFS_MAXSIZE to 3GB
IMAGE_ROOTFS_MAXSIZE = "3145728"

# Set ST_EXAMPLE_IMAGE property to '1' to allow specific use in image creation process
#ST_EXAMPLE_IMAGE = "1"

#
# INSTALL addons
#
CORE_IMAGE_EXTRA_INSTALL += " \
    nano \
    packagegroup-framework-core-base    \
    packagegroup-framework-tools-base   \
    packagegroup-framework-qt	\
    phytec-qtdemo	\
    "

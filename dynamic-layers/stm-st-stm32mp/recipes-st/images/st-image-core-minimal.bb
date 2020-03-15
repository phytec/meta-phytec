SUMMARY = "OpenSTLinux core minimal image."
LICENSE = "Proprietary"

require ./st-image.inc

inherit core-image

GLIBC_GENERATE_LOCALES = "en_US.UTF-8"
IMAGE_LINGUAS = "en-us"

IMAGE_FEATURES += "\
    ssh-server-dropbear \
    "

#
# Optee part addons
#
IMAGE_OPTEE_PART = " \
    ${@bb.utils.contains('COMBINED_FEATURES', 'optee', 'packagegroup-optee-core', '', d)} \
    ${@bb.utils.contains('COMBINED_FEATURES', 'optee', 'packagegroup-optee-test', '', d)} \
    "

#
# INSTALL addons
#
CORE_IMAGE_EXTRA_INSTALL += " \
    nano \
    ${IMAGE_OPTEE_PART}                         \
    ${@bb.utils.contains("MACHINE_FEATURES", "nand", "mtd-utils-ubifs", "", d)} \
"

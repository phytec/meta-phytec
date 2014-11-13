DESCRIPTION = "An image that will launch into the demo application for the embedded version of Qt 4"
LICENSE = "MIT"
inherit core-image
IMAGE_FEATURES = "splash"

IMAGE_INSTALL = "\
    ${CORE_IMAGE_BASE_INSTALL} \
    packagegroup-core-qt4e \
    icu \
    packagegroup-userland \
"

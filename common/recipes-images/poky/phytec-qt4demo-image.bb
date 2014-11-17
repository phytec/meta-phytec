require phytec-hwbringup-image.bb

DESCRIPTION = "An image that will launch into the demo application for the embedded version of Qt 4"
IMAGE_FEATURES = "splash"

IMAGE_INSTALL += "\
    packagegroup-core-qt4e \
    icu \
"

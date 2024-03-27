# Add specific package for phytec camera examples on userfs partition
PACKAGE_INSTALL:append:stm32mp15common = "\
    phytec-camera-examples-stm32mp1-userfs \
"

PACKAGE_INSTALL:append:stm32mp13common = "\
    phytec-camera-examples-stm32mp13x-userfs \
"

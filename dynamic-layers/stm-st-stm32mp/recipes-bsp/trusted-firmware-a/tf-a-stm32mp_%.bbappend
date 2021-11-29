FILESEXTRAPATHS_prepend := "${THISDIR}/tf-a-stm32mp:"

SRC_URI += " \
    file://0001-v2.4-stm32mp-phy1-DEVICETREE.patch \
    file://0002-v2.4-stm32mp-phy2-DEVICETREE.patch \
    "

# ---------------------------------
# Configure devupstream class usage
# ---------------------------------
BBCLASSEXTEND = "devupstream:target"

SRC_URI_class-devupstream = "git://git.phytec.de/tf-a-stm32mp;protocol=git;branch=v${TF_VERSION}-phy"
SRCREV_class-devupstream = "d2fee7e0336702a9b300ecf4cf39739d2221c723"

# ---------------------------------
# Configure default preference to manage dynamic selection between tarball and github
# ---------------------------------
STM32MP_SOURCE_SELECTION ?= "tarball"

DEFAULT_PREFERENCE = "${@bb.utils.contains('STM32MP_SOURCE_SELECTION', 'git.phytec', '-1', '1', d)}"

FILESEXTRAPATHS_prepend := "${THISDIR}/tf-a-stm32mp:"

SRC_URI += " \
    file://0001-v2.2-stm32mp-phy1-DEVICETREE.patch \
    "

# ---------------------------------
# Configure devupstream class usage
# ---------------------------------
BBCLASSEXTEND = "devupstream:target"

SRC_URI_class-devupstream = "git://git.phytec.de/tf-a-stm32mp;protocol=git;branch=v${TF_VERSION}-phy"
SRCREV_class-devupstream = "47cb355361ff063c187949a8334e85f3a921bb1b"

# ---------------------------------
# Configure default preference to manage dynamic selection between tarball and github
# ---------------------------------
STM32MP_SOURCE_SELECTION ?= "tarball"

DEFAULT_PREFERENCE = "${@bb.utils.contains('STM32MP_SOURCE_SELECTION', 'git.phytec', '-1', '1', d)}"

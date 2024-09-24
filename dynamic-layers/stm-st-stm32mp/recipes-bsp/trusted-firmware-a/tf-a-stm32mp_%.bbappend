FILESEXTRAPATHS:prepend := "${THISDIR}/tf-a-stm32mp:"

SRC_URI += " \
    file://0001-v2.6-stm32mp-phy1-MMC.patch \
    file://0002-v2.6-stm32mp-phy1-DEVICETREE.patch \
    file://0001-v2.6-stm32mp-phy2-DEVICETREE.patch \
    file://0001-v2.6-stm32mp-phy3-DEVICETREE.patch \
    file://0002-v2.6-stm32mp-phy2-FWU.patch \
    file://0001-stm32mp1-platform-update-st-version-to-r2.2.patch \
    "

# ---------------------------------
# Configure devupstream class usage
# ---------------------------------
BBCLASSEXTEND = "devupstream:target"

SRC_URI:class-devupstream = "git://git.phytec.de/tf-a-stm32mp;protocol=git;branch=${TF_A_VERSION}-phy"
SRCREV:class-devupstream = "c593a30a6033423d00fea901b2b6f4aa38b3d548"

# ---------------------------------
# Configure default preference to manage dynamic selection between tarball and github
# ---------------------------------
STM32MP_SOURCE_SELECTION ?= "tarball"

DEFAULT_PREFERENCE = "${@bb.utils.contains('STM32MP_SOURCE_SELECTION', 'git.phytec', '-1', '1', d)}"

FILESEXTRAPATHS:prepend := "${THISDIR}/tf-a-stm32mp:"

SRC_URI += " \
    file://0001-v2.4-stm32mp-phy3-DEVICETREE.patch \
    file://0002-v2.4-stm32mp-phy3-MMC.patch \
    file://0003-v2.4-stm32mp-phy3-REGULATOR.patch \
    "

# ---------------------------------
# Configure devupstream class usage
# ---------------------------------
BBCLASSEXTEND = "devupstream:target"

SRC_URI:class-devupstream = "git://git.phytec.de/tf-a-stm32mp;protocol=git;branch=${TF_A_VERSION}-phy"
SRCREV:class-devupstream = "12d0b32b15033814a2d6a46db59e19883f401853"

# ---------------------------------
# Configure default preference to manage dynamic selection between tarball and github
# ---------------------------------
STM32MP_SOURCE_SELECTION ?= "tarball"

DEFAULT_PREFERENCE = "${@bb.utils.contains('STM32MP_SOURCE_SELECTION', 'git.phytec', '-1', '1', d)}"

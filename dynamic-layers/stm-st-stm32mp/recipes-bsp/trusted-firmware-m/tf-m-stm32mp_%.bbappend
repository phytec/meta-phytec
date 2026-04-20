FILESEXTRAPATHS:prepend := "${THISDIR}/tf-m-stm32mp:"

# ---------------------------------
# Configure devupstream class usage
# ---------------------------------
BBCLASSEXTEND = "devupstream:target"

SRC_URI:class-devupstream = "git://github.com/phytec/tf-m-stm32mp;protocol=https;branch=v${TF_M_VERSION}-phy"
SRCREV:class-devupstream = "c4917c1acf576ee34a3be4ffef33fca940ad7b9e"

# -----------------------------------------------------------------------------------
# Configure default preference to manage dynamic selection between tarball and github
# -----------------------------------------------------------------------------------
STM32MP_SOURCE_SELECTION ?= "tarball"

# Use PHYTEC github by default
STM32MP_SOURCE_SELECTION = "github_phytec"
DEFAULT_PREFERENCE = "${@bb.utils.contains('STM32MP_SOURCE_SELECTION', 'github_phytec', '-1', '1', d)}"

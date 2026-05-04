SRC_URI = "git://github.com/phytec/tf-m-stm32mp;protocol=https;branch=v${TF_M_VERSION}-phy"
SRCREV = "c4917c1acf576ee34a3be4ffef33fca940ad7b9e"

TF_M_RELEASE = "r1-phy1"

# ----------------------------------------------------------------------
# Configure devupstream class usage to get the HEAD of PHYTEC git branch
# ----------------------------------------------------------------------
DEFAULT_PREFERENCE = "${@bb.utils.contains('STM32MP_SOURCE_SELECTION', 'phytec-dev', '-1', '1', d)}"

SRC_URI:class-devupstream = "git://github.com/phytec/tf-m-stm32mp;protocol=https;branch=v${TF_M_VERSION}-phy"
SRCREV:class-devupstream = "${AUTOREV}"

SRC_URI = "git://github.com/phytec/optee_os-phytec-stm32mp.git;protocol=https;branch=${OPTEE_VERSION}-phy"
SRCREV = "6d697c62c9ce2da47dc84ff0fa19a9d40f30088d"

OPTEE_RELEASE = "r2-phy3"

# ----------------------------------------------------------------------
# Configure devupstream class usage to get the HEAD of PHYTEC git branch
# ----------------------------------------------------------------------
DEFAULT_PREFERENCE = "${@bb.utils.contains('STM32MP_SOURCE_SELECTION', 'phytec-dev', '-1', '1', d)}"

SRC_URI:class-devupstream = "git://github.com/phytec/optee_os-phytec-stm32mp.git;protocol=https;branch=${OPTEE_VERSION}-phy"
SRCREV:class-devupstream = "${AUTOREV}"

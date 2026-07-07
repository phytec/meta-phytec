SRC_URI = "git://github.com/phytec/optee_os-phytec-stm32mp.git;protocol=https;branch=${OPTEE_VERSION}-phy"
SRCREV = "ced0b1fd38e05fad00e8d65af9cd435346a6ba51"

OPTEE_RELEASE = "r3.1-phy1"

# ----------------------------------------------------------------------
# Configure devupstream class usage to get the HEAD of PHYTEC git branch
# ----------------------------------------------------------------------
DEFAULT_PREFERENCE = "${@bb.utils.contains('STM32MP_SOURCE_SELECTION', 'phytec-dev', '-1', '1', d)}"

SRC_URI:class-devupstream = "git://github.com/phytec/optee_os-phytec-stm32mp.git;protocol=https;branch=${OPTEE_VERSION}-phy"
SRCREV:class-devupstream = "${AUTOREV}"

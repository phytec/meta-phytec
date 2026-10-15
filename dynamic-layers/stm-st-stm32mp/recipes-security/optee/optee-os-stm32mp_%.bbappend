SRC_URI = "git://github.com/phytec/optee_os-phytec-stm32mp.git;protocol=https;branch=${OPTEE_VERSION}-phy"
SRCREV = "4296aea6ea690b57dd47f8252a9bd4a236f78b8b"

OPTEE_RELEASE = "r3.1-phy2"

# ----------------------------------------------------------------------
# Configure devupstream class usage to get the HEAD of PHYTEC git branch
# ----------------------------------------------------------------------
DEFAULT_PREFERENCE = "${@bb.utils.contains('STM32MP_SOURCE_SELECTION', 'phytec-dev', '-1', '1', d)}"

SRC_URI:class-devupstream = "git://github.com/phytec/optee_os-phytec-stm32mp.git;protocol=https;branch=${OPTEE_VERSION}-phy"
SRCREV:class-devupstream = "${AUTOREV}"

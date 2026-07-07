SRC_URI = "git://git.phytec.de/tf-a-stm32mp;protocol=git;branch=${TF_A_VERSION}-phy"
SRCREV = "af01615e86c7c96f4d4d2f73fb86b7f8c2100ea8"

TF_A_RELEASE = "r3.1-phy1"

# ----------------------------------------------------------------------
# Configure devupstream class usage to get the HEAD of PHYTEC git branch
# ----------------------------------------------------------------------
DEFAULT_PREFERENCE = "${@bb.utils.contains('STM32MP_SOURCE_SELECTION', 'phytec-dev', '-1', '1', d)}"

SRC_URI:class-devupstream = "git://git.phytec.de/tf-a-stm32mp;protocol=git;branch=${TF_A_VERSION}-phy"
SRCREV:class-devupstream = "${AUTOREV}"

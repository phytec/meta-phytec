SRC_URI = "git://github.com/phytec/tf-m-stm32mp;protocol=https;branch=v${TF_M_VERSION}-phy"
SRCREV = "08f189542d4420cf70ccf857fca8e06feefddea1"

TF_M_RELEASE = "r2.1-phy2"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI += " \
    file://0001-cmsis-phytec.patch;patchdir=../${TF_M_EXTERNAL_SOURCES_ROOTDIR}/${TF_M_PATH_STM32MP2_CMSIS_DEVICE} \
    file://0001-lp-fw-phytec.patch;patchdir=../${TF_M_EXTERNAL_SOURCES_ROOTDIR}/${TF_M_PATH_STM32MP2_LOW_POWER} \
"

# ----------------------------------------------------------------------
# Configure devupstream class usage to get the HEAD of PHYTEC git branch
# ----------------------------------------------------------------------
DEFAULT_PREFERENCE = "${@bb.utils.contains('STM32MP_SOURCE_SELECTION', 'phytec-dev', '-1', '1', d)}"

SRC_URI:class-devupstream = "git://github.com/phytec/tf-m-stm32mp;protocol=https;branch=v${TF_M_VERSION}-phy"
SRCREV:class-devupstream = "${AUTOREV}"

SRC_URI:class-devupstream += " \
    file://0001-cmsis-phytec.patch;patchdir=../${TF_M_EXTERNAL_SOURCES_ROOTDIR}/${TF_M_PATH_STM32MP2_CMSIS_DEVICE} \
    file://0001-lp-fw-phytec.patch;patchdir=../${TF_M_EXTERNAL_SOURCES_ROOTDIR}/${TF_M_PATH_STM32MP2_LOW_POWER} \
"

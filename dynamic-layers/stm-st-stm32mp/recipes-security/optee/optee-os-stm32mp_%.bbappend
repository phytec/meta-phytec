FILESEXTRAPATHS:prepend := "${THISDIR}/optee-os:"

SRC_URI += " \
    file://0001-3.16.0-phy1.patch \
    file://0001-3.16.0-phy2.patch \
    "

# ---------------------------------
# Configure devupstream class usage
# ---------------------------------
BBCLASSEXTEND = "devupstream:target"

SRC_URI:class-devupstream = "git://github.com/phytec/optee_os-phytec-stm32mp.git;protocol=https;branch=${OPTEE_VERSION}-phy"
SRCREV:class-devupstream = "645ac1666a1811c7d7e08b6a89e498c5007831d7"

# -----------------------------------------------------------------------------------
# Configure default preference to manage dynamic selection between tarball and github
# -----------------------------------------------------------------------------------
STM32MP_SOURCE_SELECTION ?= "tarball"

DEFAULT_PREFERENCE = "${@bb.utils.contains('STM32MP_SOURCE_SELECTION', 'github_phytec', '-1', '1', d)}"

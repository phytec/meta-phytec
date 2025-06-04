FILESEXTRAPATHS:prepend := "${THISDIR}/optee-os:"

SRC_URI += " \
    file://0001-4.0.0-phy1.patch \
    "

# ---------------------------------
# Configure devupstream class usage
# ---------------------------------
BBCLASSEXTEND = "devupstream:target"

SRC_URI:class-devupstream = "git://github.com/phytec/optee_os-phytec-stm32mp.git;protocol=https;branch=${OPTEE_VERSION}-phy"
SRCREV:class-devupstream = "f4a179d9c9c9afdbcbc91353910314ac91f9059a"

# -----------------------------------------------------------------------------------
# Configure default preference to manage dynamic selection between tarball and github
# -----------------------------------------------------------------------------------
STM32MP_SOURCE_SELECTION ?= "tarball"

DEFAULT_PREFERENCE = "${@bb.utils.contains('STM32MP_SOURCE_SELECTION', 'github_phytec', '-1', '1', d)}"

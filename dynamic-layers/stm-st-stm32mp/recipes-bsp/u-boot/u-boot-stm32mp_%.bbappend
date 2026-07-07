FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI = "git://git.phytec.de/u-boot-stm32mp;protocol=git;branch=${U_BOOT_VERSION}-phy"
SRCREV = "2bf24ff2cb102d58ed7101d23b9454760051dae8"

U_BOOT_RELEASE = "r3.1-phy1"

SRC_URI += " \
    ${@bb.utils.contains('MACHINE_FEATURES', 'fw-update', 'file://0001-configs-phytec-stm32mp-update-env-offset-for-firmwar.patch', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'fw-update', 'file://0002-enable-doraucboot.patch', '', d)} \
"

# ----------------------------------------------------------------------
# Configure devupstream class usage to get the HEAD of PHYTEC git branch
# ----------------------------------------------------------------------
DEFAULT_PREFERENCE = "${@bb.utils.contains('STM32MP_SOURCE_SELECTION', 'phytec-dev', '-1', '1', d)}"

SRC_URI:class-devupstream = "git://git.phytec.de/u-boot-stm32mp;protocol=git;branch=${U_BOOT_VERSION}-phy"
SRCREV:class-devupstream = "${AUTOREV}"

SRC_URI:class-devupstream += " \
    ${@bb.utils.contains('MACHINE_FEATURES', 'fw-update', 'file://0001-configs-phytec-stm32mp-update-env-offset-for-firmwar.patch', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'fw-update', 'file://0002-enable-doraucboot.patch', '', d)} \
"

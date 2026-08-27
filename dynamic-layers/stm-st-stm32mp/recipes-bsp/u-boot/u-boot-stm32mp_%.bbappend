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

python do_create_extlinux_config:append() {
    splash = d.getVar('UBOOT_EXTLINUX_SPLASH')
    cfile = d.getVar('UBOOT_EXTLINUX_CONFIG')

    if not splash or not cfile or not os.path.exists(cfile):
        return

    with open(cfile, 'r+') as f:
        lines = f.read().splitlines()
        lines.insert(1, f"MENU BACKGROUND /{splash}.bmp")

        f.seek(0)
        f.write('\n'.join(lines) + '\n')
        f.truncate()
}

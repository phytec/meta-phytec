SUMMARY = "TI Firmware Package"
DESCRIPTION = "Provides TI firmware files for various components"

LICENSE = "TI-TFL"
LIC_FILES_CHKSUM = "file://LICENSE.ti;md5=b5aebf0668bdf95621259288c4a46d76"

PV = "11.01.05"
PR = "r0"

SRC_URI = "git://git.ti.com/git/processor-firmware/ti-linux-firmware.git;protocol=https;branch=ti-linux-firmware"
SRCREV = "92a31c8e9ecb5671371cc3a48eb5a4b8340221a4"

S = "${WORKDIR}/git"
PACKAGE_ARCH = "${MACHINE_ARCH}"

CLEANBROKEN = "1"

PACKAGES = "ti-sci-fw prueth-fw pruhsr-fw prusw-fw ti-dm-fw"
PROVIDES = "ti-sci-fw prueth-fw pruhsr-fw prusw-fw ti-dm-fw"

FILES:ti-sci-fw += "${nonarch_base_libdir}/firmware/ti-sysfw/*"
FILES:prueth-fw += "${nonarch_base_libdir}/firmware/ti-pruss/*-prueth-fw.elf"
FILES:pruhsr-fw += "${nonarch_base_libdir}/firmware/ti-pruss/*-pruhsr-fw.elf"
FILES:prusw-fw += "${nonarch_base_libdir}/firmware/ti-pruss/*-prusw-fw.elf"
FILES:ti-dm-fw += "${nonarch_base_libdir}/firmware/ti-dm/${PLAT_SFX}/${DM_FIRMWARE}"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP += "arch"

do_compile[noexec] = "1"
do_configure[noexec] = "1"

PRU_FW = " \
    am65x-sr2-pru0-prusw-fw.elf \
    am65x-sr2-pru1-prusw-fw.elf \
    am65x-sr2-rtu0-prusw-fw.elf \
    am65x-sr2-rtu1-prusw-fw.elf \
    am65x-sr2-txpru0-prusw-fw.elf \
    am65x-sr2-txpru1-prusw-fw.elf \
\
    am65x-sr2-pru0-prueth-fw.elf \
    am65x-sr2-pru1-prueth-fw.elf \
    am65x-sr2-rtu0-prueth-fw.elf \
    am65x-sr2-rtu1-prueth-fw.elf \
    am65x-sr2-txpru0-prueth-fw.elf \
    am65x-sr2-txpru1-prueth-fw.elf \
\
    am65x-sr2-pru0-pruhsr-fw.elf \
    am65x-sr2-pru1-pruhsr-fw.elf \
    am65x-sr2-rtu0-pruhsr-fw.elf \
    am65x-sr2-rtu1-pruhsr-fw.elf \
    am65x-sr2-txpru0-pruhsr-fw.elf \
    am65x-sr2-txpru1-pruhsr-fw.elf \
"

DM_FIRMWARE = "ipc_echo_testb_mcu1_0_release_strip.xer5f"
DM_FIRMWARE:am62axx = "dm_edgeai_mcu1_0_release_strip.out"

DM_FW_LIST = ""
DM_FW_LIST:am62xx = "${DM_FIRMWARE}"
DM_FW_LIST:am62axx = "${DM_FIRMWARE}"

PLAT_SFX = ""
PLAT_SFX:am62xx = "am62xx"
PLAT_SFX:am62axx = "am62axx"

DM_FW_DIR = "ti-dm/${PLAT_SFX}"
INSTALL_DM_FW_DIR  = "${nonarch_base_libdir}/firmware/${DM_FW_DIR}"

do_install() {
    install -d ${D}${nonarch_base_libdir}/firmware/ti-sysfw
    install -m 644 ${S}/ti-sysfw/ti-sci-firmware-* ${D}${nonarch_base_libdir}/firmware/ti-sysfw
    install -m 644 ${S}/ti-sysfw/ti-fs-firmware-* ${D}${nonarch_base_libdir}/firmware/ti-sysfw
    install -m 644 ${S}/ti-sysfw/ti-fs-stub-firmware-* ${D}${nonarch_base_libdir}/firmware/ti-sysfw

    install -d ${D}${nonarch_base_libdir}/firmware/ti-pruss/
    for f in ${PRU_FW}; do
        install -m 0644 ${S}/ti-pruss/$f ${D}${nonarch_base_libdir}/firmware/ti-pruss/$f
    done

    # DM Firmware
    if [ -n "${DM_FW_LIST}" ]; then
        install -d ${D}${INSTALL_DM_FW_DIR}
        for FW_NAME in ${DM_FW_LIST}
        do
            install -m 0644 ${S}/${DM_FW_DIR}/${FW_NAME} ${D}${INSTALL_DM_FW_DIR}/
        done
    fi
}

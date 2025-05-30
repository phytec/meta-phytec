SUMMARY = "TI Firmware Package"
DESCRIPTION = "Provides TI firmware files for various components"

LICENSE = "TI-TFL"
LIC_FILES_CHKSUM = "file://LICENSE.ti;md5=b5aebf0668bdf95621259288c4a46d76"

inherit deploy

PV = "11.00.09"
PR = "r0"

SRC_URI = "git://git.ti.com/git/processor-firmware/ti-linux-firmware.git;protocol=https;branch=ti-linux-firmware"
SRCREV = "27003b6bff61a55dd1fff2cc777e5e07b092b1f7"

S = "${WORKDIR}/git"
PACKAGE_ARCH = "${MACHINE_ARCH}"

CLEANBROKEN = "1"

PACKAGES = "ti-sci-fw prueth-fw pruhsr-fw prusw-fw ti-dm-fw"
PROVIDES = "ti-sci-fw prueth-fw pruhsr-fw prusw-fw ti-dm-fw"

FILES:ti-sci-fw += "${nonarch_base_libdir}/firmware/ti-sysfw/*"
FILES:prueth-fw += "${nonarch_base_libdir}/firmware/ti-pruss/*-prueth-fw.elf"
FILES:pruhsr-fw += "${nonarch_base_libdir}/firmware/ti-pruss/*-pruhsr-fw.elf"
FILES:prusw-fw += "${nonarch_base_libdir}/firmware/ti-pruss/*-prusw-fw.elf"
FILES:ti-dm-fw += "${nonarch_base_libdir}/firmware/ti-dm/am62xx/ipc_echo_testb_mcu1_0_release_strip.xer5f"

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

do_install() {
    install -d ${D}${nonarch_base_libdir}/firmware/ti-sysfw
    install -m 644 ${S}/ti-sysfw/ti-sci-firmware-* ${D}${nonarch_base_libdir}/firmware/ti-sysfw
    install -m 644 ${S}/ti-sysfw/ti-fs-firmware-* ${D}${nonarch_base_libdir}/firmware/ti-sysfw
    install -m 644 ${S}/ti-sysfw/ti-fs-stub-firmware-* ${D}${nonarch_base_libdir}/firmware/ti-sysfw

    install -d ${D}${nonarch_base_libdir}/firmware/ti-pruss/
    for f in ${PRU_FW}; do
        install -m 0644 ${S}/ti-pruss/$f ${D}${nonarch_base_libdir}/firmware/ti-pruss/$f
    done
}

do_install:append:am62xx() {
    install -d ${D}${nonarch_base_libdir}/firmware/ti-dm/am62xx
    install -m 644 ${S}/ti-dm/am62xx/ipc_echo_testb_mcu1_0_release_strip.xer5f ${D}${nonarch_base_libdir}/firmware/ti-dm/am62xx
}

do_deploy(){
}

do_deploy:append:am62xx() {
    # DM Firmware is needed for building U-Boot
    install -d ${DEPLOYDIR}/ti-dm/am62xx
    install -m 0644 ${S}/ti-dm/am62xx/ipc_echo_testb_mcu1_0_release_strip.xer5f ${DEPLOYDIR}/ti-dm/am62xx
}

do_deploy:k3r5() {
    install -d ${DEPLOYDIR}/ti-sysfw
    install -m 644 ${S}/ti-sysfw/ti-sci-firmware-* ${DEPLOYDIR}/ti-sysfw
    install -m 644 ${S}/ti-sysfw/ti-fs-firmware-* ${DEPLOYDIR}/ti-sysfw
    install -m 644 ${S}/ti-sysfw/ti-fs-stub-firmware-* ${DEPLOYDIR}/ti-sysfw
}

addtask deploy before do_build after do_compile

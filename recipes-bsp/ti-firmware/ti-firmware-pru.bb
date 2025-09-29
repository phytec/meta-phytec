SUMMARY = "TI PRU Firmware"

require ti-firmware.inc

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
    install -d ${D}${nonarch_base_libdir}/firmware/ti-pruss/
    for f in ${PRU_FW}; do
        install -m 0644 ${S}/ti-pruss/$f ${D}${nonarch_base_libdir}/firmware/ti-pruss/$f
    done
}

FILES:prueth-fw += "${nonarch_base_libdir}/firmware/ti-pruss/*-prueth-fw.elf"
FILES:pruhsr-fw += "${nonarch_base_libdir}/firmware/ti-pruss/*-pruhsr-fw.elf"
FILES:prusw-fw += "${nonarch_base_libdir}/firmware/ti-pruss/*-prusw-fw.elf"

COMPATIBLE_MACHINE = "am64xx"

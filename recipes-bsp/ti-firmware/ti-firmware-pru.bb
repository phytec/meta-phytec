SUMMARY = "TI PRU Firmware"

require ti-firmware.inc

PRU_FW = " \
    am64x-sr2-pru0-prusw-fw.elf \
    am64x-sr2-pru1-prusw-fw.elf \
    am64x-sr2-rtu0-prusw-fw.elf \
    am64x-sr2-rtu1-prusw-fw.elf \
    am64x-sr2-txpru0-prusw-fw.elf \
    am64x-sr2-txpru1-prusw-fw.elf \
\
    am64x-sr2-pru0-prueth-fw.elf \
    am64x-sr2-pru1-prueth-fw.elf \
    am64x-sr2-rtu0-prueth-fw.elf \
    am64x-sr2-rtu1-prueth-fw.elf \
    am64x-sr2-txpru0-prueth-fw.elf \
    am64x-sr2-txpru1-prueth-fw.elf \
\
    am64x-sr2-pru0-pruhsr-fw.elf \
    am64x-sr2-pru1-pruhsr-fw.elf \
    am64x-sr2-rtu0-pruhsr-fw.elf \
    am64x-sr2-rtu1-pruhsr-fw.elf \
    am64x-sr2-txpru0-pruhsr-fw.elf \
    am64x-sr2-txpru1-pruhsr-fw.elf \
"

do_install() {
    install -d ${D}${nonarch_base_libdir}/firmware/ti-pruss/

    for f in ${PRU_FW}; do
        install -m 0644 ${S}/ti-pruss/$f ${D}${nonarch_base_libdir}/firmware/ti-pruss/$f

        # Create a backward-compatible AM65x symlink -> AM64x target
        # Only change the leading SoC prefix; leave the rest untouched.
        legacy="$(printf '%s\n' "$f" | sed -e 's/^am64x/am65x/')"
        if [ "$legacy" != "$f" ]; then
            ln -sr ${D}${nonarch_base_libdir}/firmware/ti-pruss/$f \
                   ${D}${nonarch_base_libdir}/firmware/ti-pruss/$legacy
        fi
    done
}

FILES:${PN} += "${nonarch_base_libdir}/firmware/ti-pruss/*"

COMPATIBLE_MACHINE = "am64xx"

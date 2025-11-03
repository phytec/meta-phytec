SUMMARY = "TI SYSFW Firmware"

require ti-firmware.inc

PACKAGES = "ti-sci-fw"
PROVIDES = "ti-sci-fw"

FILES:ti-sci-fw += "${nonarch_base_libdir}/firmware/ti-sysfw/*"

do_install() {
    install -d ${D}${nonarch_base_libdir}/firmware/ti-sysfw
    install -m 644 ${S}/ti-sysfw/ti-sci-firmware-* ${D}${nonarch_base_libdir}/firmware/ti-sysfw
    install -m 644 ${S}/ti-sysfw/ti-fs-firmware-* ${D}${nonarch_base_libdir}/firmware/ti-sysfw
    install -m 644 ${S}/ti-sysfw/ti-fs-stub-firmware-* ${D}${nonarch_base_libdir}/firmware/ti-sysfw
}

COMPATIBLE_MACHINE = "^("
COMPATIBLE_MACHINE .= "am62xx"
COMPATIBLE_MACHINE .= "|am62axx"
COMPATIBLE_MACHINE .= "|am64xx"
COMPATIBLE_MACHINE .= "|k3r5"
COMPATIBLE_MACHINE .= ")$"

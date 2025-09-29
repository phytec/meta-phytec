SUMMARY = "TI DM Firmware Image"

require ti-firmware.inc

PACKAGES = "ti-dm-fw"
PROVIDES = "ti-dm-fw"

DM_FIRMWARE = ""
DM_FIRMWARE:am62xx = "ipc_echo_testb_mcu1_0_release_strip.xer5f"
DM_FIRMWARE:am62axx = "dm_edgeai_mcu1_0_release_strip.out"

PLAT_SFX = ""
PLAT_SFX:am62xx = "am62xx"
PLAT_SFX:am62axx = "am62axx"

DM_FW_DIR = "ti-dm/${PLAT_SFX}"
INSTALL_DM_FW_DIR  = "${nonarch_base_libdir}/firmware/${DM_FW_DIR}"

do_install() {
    install -d ${D}${INSTALL_DM_FW_DIR}
    install -m 0644 ${S}/${DM_FW_DIR}/${DM_FIRMWARE} ${D}${INSTALL_DM_FW_DIR}/
}

FILES:ti-dm-fw += "${nonarch_base_libdir}/firmware/ti-dm/${PLAT_SFX}/${DM_FIRMWARE}"

COMPATIBLE_MACHINE = "^("
COMPATIBLE_MACHINE .= "am62xx"
COMPATIBLE_MACHINE .= "|am62axx"
COMPATIBLE_MACHINE .= ")$"

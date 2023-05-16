# Freeze the ti-rtos-firmware version to match with the current
# Linux/U-boot version.
require dynamic-layers/meta-ti-bsp/recipes-bsp/ti-linux-fw/ti-linux-fw-freeze.inc

PLAT_SFX:phyboard-izar-am68x-1 = "j721s2"

do_install:phyboard-izar-am68x-1() {
    install -d ${LEGACY_IPC_FW_DIR}
    install -m 0644 ${RTOS_IPC_FW_DIR}/ipc_echo_test_mcu1_1_release_strip.xer5f ${LEGACY_IPC_FW_DIR}
    install -m 0644 ${RTOS_IPC_FW_DIR}/ipc_echo_test_mcu2_0_release_strip.xer5f ${LEGACY_IPC_FW_DIR}
    install -m 0644 ${RTOS_IPC_FW_DIR}/ipc_echo_test_mcu2_1_release_strip.xer5f ${LEGACY_IPC_FW_DIR}
    install -m 0644 ${RTOS_IPC_FW_DIR}/ipc_echo_test_mcu3_0_release_strip.xer5f ${LEGACY_IPC_FW_DIR}
    install -m 0644 ${RTOS_IPC_FW_DIR}/ipc_echo_test_mcu3_1_release_strip.xer5f ${LEGACY_IPC_FW_DIR}
    install -m 0644 ${RTOS_IPC_FW_DIR}/ipc_echo_test_c7x_1_release_strip.xe71 ${LEGACY_IPC_FW_DIR}
    install -m 0644 ${RTOS_IPC_FW_DIR}/ipc_echo_test_c7x_2_release_strip.xe71 ${LEGACY_IPC_FW_DIR}
    # DM Firmware
    install -m 0644 ${RTOS_DM_FW_DIR}/ipc_echo_testb_mcu1_0_release_strip.xer5f ${LEGACY_DM_FW_DIR}
}

ALTERNATIVE:${PN}:phyboard-izar-am68x-1 = "\
                    j721s2-mcu-r5f0_0-fw \
                    j721s2-mcu-r5f0_1-fw \
                    j721s2-main-r5f0_0-fw \
                    j721s2-main-r5f0_1-fw \
                    j721s2-main-r5f1_0-fw \
                    j721s2-main-r5f1_1-fw \
                    j721s2-c71_0-fw \
                    j721s2-c71_1-fw \
                    "
TARGET_MCU_R5FSS0_0:phyboard-izar-am68x-1 = "j721s2-mcu-r5f0_0-fw"
TARGET_MCU_R5FSS0_1:phyboard-izar-am68x-1 = "j721s2-mcu-r5f0_1-fw"
TARGET_MAIN_R5FSS0_0:phyboard-izar-am68x-1 = "j721s2-main-r5f0_0-fw"
TARGET_MAIN_R5FSS0_1:phyboard-izar-am68x-1 = "j721s2-main-r5f0_1-fw"
TARGET_MAIN_R5FSS1_0:phyboard-izar-am68x-1 = "j721s2-main-r5f1_0-fw"
TARGET_MAIN_R5FSS1_1:phyboard-izar-am68x-1 = "j721s2-main-r5f1_1-fw"
TARGET_C7X_0:phyboard-izar-am68x-1 = "j721s2-c71_0-fw"
TARGET_C7X_1:phyboard-izar-am68x-1 = "j721s2-c71_1-fw"

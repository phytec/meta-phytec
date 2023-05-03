# Freeze the ti-rtos-firmware version to match with the current
# Linux/U-boot version.
require dynamic-layers/meta-ti/recipes-bsp/ti-linux-fw/ti-linux-fw-freeze.inc

#deploy firmware all of our am62axx platforms
do_install_prepend_am62axx() {
        ( cd ${RTOS_DM_FW_DIR}; \
                mv ${DM_FIRMWARE} ${DM_FIRMWARE}.unsigned; \
                ${TI_SECURE_DEV_PKG}/scripts/secure-binary-image.sh ${DM_FIRMWARE}.unsigned ${DM_FIRMWARE}; \
        )
        ( cd ${RTOS_IPC_FW_DIR}; \
                ${TI_SECURE_DEV_PKG}/scripts/secure-binary-image.sh am62a-mcu-r5f0_0-fw \
                    am62a-mcu-r5f0_0-fw.signed; \
                ${TI_SECURE_DEV_PKG}/scripts/secure-binary-image.sh ipc_echo_test_c7x_1_release_strip.xe71 \
                    ipc_echo_test_c7x_1_release_strip.xe71.signed; \
        )
}

PLAT_SFX_phyboard-izar-am68x-1 = "j721s2"

do_install_phyboard-izar-am68x-1() {
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

ALTERNATIVE_${PN}_phyboard-izar-am68x-1 = "\
                    j721s2-mcu-r5f0_0-fw \
                    j721s2-mcu-r5f0_1-fw \
                    j721s2-main-r5f0_0-fw \
                    j721s2-main-r5f0_1-fw \
                    j721s2-main-r5f1_0-fw \
                    j721s2-main-r5f1_1-fw \
                    j721s2-c71_0-fw \
                    j721s2-c71_1-fw \
                    "
TARGET_MCU_R5FSS0_0_phyboard-izar-am68x-1 = "j721s2-mcu-r5f0_0-fw"
TARGET_MCU_R5FSS0_1_phyboard-izar-am68x-1 = "j721s2-mcu-r5f0_1-fw"
TARGET_MAIN_R5FSS0_0_phyboard-izar-am68x-1 = "j721s2-main-r5f0_0-fw"
TARGET_MAIN_R5FSS0_1_phyboard-izar-am68x-1 = "j721s2-main-r5f0_1-fw"
TARGET_MAIN_R5FSS1_0_phyboard-izar-am68x-1 = "j721s2-main-r5f1_0-fw"
TARGET_MAIN_R5FSS1_1_phyboard-izar-am68x-1 = "j721s2-main-r5f1_1-fw"
TARGET_C7X_0_phyboard-izar-am68x-1 = "j721s2-c71_0-fw"
TARGET_C7X_1_phyboard-izar-am68x-1 = "j721s2-c71_1-fw"

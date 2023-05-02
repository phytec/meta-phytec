# Freeze the ti-rtos-firmware version to match with the current
# Linux/U-boot version.
require dynamic-layers/meta-ti-bsp/recipes-bsp/ti-linux-fw/ti-linux-fw-freeze.inc

#deploy firmware all of our am62axx platforms
do_install:prepend:am62axx() {
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

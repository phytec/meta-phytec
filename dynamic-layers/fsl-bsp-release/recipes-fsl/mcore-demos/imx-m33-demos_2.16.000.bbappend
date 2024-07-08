do_install:append:mx93-nxp-bsp () {
    cd ${D}${base_libdir}/firmware

    IMX_M33_DEMO_EXT="elf"

    # Remove all demos (*.elf) not in the IMX_M33_DEMO_INSTALL variable
    for demo in $(find . -type f -name "*.elf" -printf "%P\n"); do
        if ! echo "${IMX_M33_DEMO_INSTALL}" | grep -Fq "${demo}"; then
            rm -f ${demo}
        fi
    done
}

do_deploy:append:mx93-nxp-bsp () {
    cd ${DEPLOYDIR}

    IMX_M33_DEMO_EXT="bin"

    # Remove all demos (*.bin) not in the IMX_M33_DEMO_INSTALL variable
    for demo in $(find . -type f -name "*.bin" -printf "%P\n"); do
        if ! echo "${IMX_M33_DEMO_INSTALL}" | grep -Fq "${demo}"; then
            rm -f ${demo}
        fi
    done
}

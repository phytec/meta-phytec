remove_m33_demos() {
    local ext="$1"
    local search_dir="$2"

    cd "$search_dir"

    # Remove all demos (with .${ext} suffix) not in the IMX_M33_DEMO_INSTALL variable
    for demo in $(find . -type f -name "*.${ext}" -printf "%P\n"); do
        demo_base=$(basename "${demo}" ".${ext}")
        if ! echo "${IMX_M33_DEMO_INSTALL}" | grep -Fq "${demo_base}"; then
            rm -f "${demo}"
        fi
    done
}

do_install:append:mx93-nxp-bsp () {
    remove_m33_demos "elf" "${D}${base_libdir}/firmware"
}

do_deploy:append:mx93-nxp-bsp () {
    remove_m33_demos "bin" "${DEPLOYDIR}"
}

do_deploy:append:mx93-nxp-bsp () {
    install -m 0644 ${S}/*.${MCORE_DEMO_FILE_EXTENSION} ${DEPLOYDIR}
}

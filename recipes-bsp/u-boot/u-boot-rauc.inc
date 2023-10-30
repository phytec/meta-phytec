set_doraucboot_1 () {
    bbnote "Setting \"doraucboot\" to \"1\""
    sed -i "s/\(setenv doraucboot \)[01]/\11/" ${S}/include/configs/phycore_*.h
    sed -i "s/\(setenv doraucboot \)[01]/\11/" ${S}/configs/phycore-imx8m*
    if test -f "${S}/configs/imx8mm-phygate-tauri_defconfig"; then
        sed -i "s/\(setenv doraucboot \)[01]/\11/" ${S}/configs/imx8mm-phygate-tauri_defconfig
    fi
}

enable_bootretry () {
    bbnote "Setting \"bootretry\" to \"30\""
    sed -i "s/\(bootretry=\)-1/\130/" ${S}/include/configs/phycore_rauc_env.h
}

python do_patch:append:update () {
    bb.build.exec_func('set_doraucboot_1', d)
    bb.build.exec_func('enable_bootretry', d)
}

set_safely () {
    if test -f "${S}/${2}"; then
        sed -i ${1} "${S}/${2}"
    fi
}

set_doraucboot_1 () {
    bbnote "Setting \"doraucboot\" to \"1\""
    set_safely "s/\(setenv doraucboot \)[01]/\11/" include/configs/phycore_*.h
    set_safely "s/\(setenv doraucboot \)[01]/\11/" configs/phycore-imx8m*
    set_safely "s/\(setenv doraucboot \)[01]/\11/" configs/imx8mm-phygate-tauri_defconfig
}

enable_bootretry () {
    bbnote "Setting \"bootretry\" to \"30\""
    set_safely "s/\(bootretry=\)-1/\130/" include/configs/phycore_rauc_env.h
}

python do_patch:append:update () {
    bb.build.exec_func('set_doraucboot_1', d)
    bb.build.exec_func('enable_bootretry', d)
}

inherit sed-safely

set_doraucboot_1 () {
    bbnote "Setting \"doraucboot\" to \"1\""
    sed_safely "s/\(doraucboot=\)[01]/\11/" include/environment/phytec/rauc.env
    sed_safely "s/\(doraucboot=\)[01]/\11/" include/env/phytec/rauc.env
    sed_safely "s/\(setenv doraucboot \)[01]/\11/" include/configs/phycore_*.h
    sed_safely "s/\(setenv doraucboot \)[01]/\11/" configs/phycore-imx8m*
    sed_safely "s/\(setenv doraucboot \)[01]/\11/" configs/imx8mm-phygate-tauri_defconfig
}

enable_bootretry () {
    bbnote "Setting \"bootretry\" to \"30\""
    sed_safely "s/\(bootretry=\)-1/\130/" include/configs/phycore_rauc_env.h
}

python do_patch:append:update () {
    bb.build.exec_func('set_doraucboot_1', d)
    bb.build.exec_func('enable_bootretry', d)
}

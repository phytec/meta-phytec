FILESEXTRAPATHS:prepend := "${THISDIR}/features:"

inherit sed-safely

SRC_URI:append:mx8m-nxp-bsp:update = " \
    file://default-env.cfg \
"

set_doraucboot_1 () {
    bbnote "Setting \"doraucboot\" to \"1\""
    sed_safely "s/\(doraucboot=\)[01]/\11/" include/env/phytec/rauc.env
}

python do_patch:append:update () {
    bb.build.exec_func('set_doraucboot_1', d)
}

python do_patch:append:mx8m-nxp-bsp:update () {
    bb.build.exec_func('set_dofitboot_1', d)
    bb.build.exec_func('set_dolegacyboot_1', d)
    bb.build.exec_func('set_image_to_fitimage', d)
}

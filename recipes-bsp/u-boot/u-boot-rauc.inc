DORAUCBOOT ??= "0"

set_doraucboot () {
    bbnote "Setting \"doraucboot\" to \"${DORAUCBOOT}\""
    sed -i "s/\(setenv doraucboot \)[01]/\1${DORAUCBOOT}/" ${S}/include/configs/phycore_*.h
}

python do_patch_append () {
    # We assume by the filename if booting the A/B system with RAUC is enabled
    # by default or not. This means, if the currently selected WKS filename
    # contains the string "rauc", we set "doraucboot" to "1".
    d.setVar('DORAUCBOOT', '1' if 'rauc' in d.getVar('WKS_FILES') else '0')
    bb.build.exec_func('set_doraucboot', d)
}

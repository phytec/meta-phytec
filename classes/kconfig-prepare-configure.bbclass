# Copyright (C) 2015 PHYTEC Messtechnik GmbH,
# Author: Stefan Christ <s.christ@phytec.de>
# Released under the MIT license (see COPYING.MIT for the terms)

# FIXME Recipes, which use the bbclass, must define the tasks
# 'do_default_defconfig' and 'do_savedefconfig' by themselfs.

# returns and .cfg filenames from SRC_URI
def find_sccs(d):
    sources=src_patches(d, True)
    sources_list=[]
    for s in sources:
        base, ext = os.path.splitext(os.path.basename(s))
        if ext and ext in [".cfg"]:
            sources_list.append(s)
    return sources_list


# uses kern-tools-native to merge the config fragments
python do_prepare_configure() {
    import subprocess
    workdir = d.getVar('WORKDIR', True)
    S = d.getVar('S', True)
    defconfig = os.path.join(workdir , 'defconfig')
    config = os.path.join(S, '.config')
    if not os.path.isfile(defconfig):
        bb.note("No defconfig file provided for the recipe")
        if not os.path.isfile(config):
            bb.note("recipe is not configured")
            bb.build.exec_func("do_default_defconfig",d)
        bb.build.exec_func("do_savedefconfig",d)
        defconfig = os.path.join(workdir , 'defconfig.temp')
    fragments = find_sccs(d)
    if not fragments:
        bb.note("no config fragments will be merged")
        bb.utils.copyfile(defconfig, config)
    else:
        bb.note("combining kconfig fragments into .config")
        cmd = 'scripts/kconfig/merge_config.sh -m -O %s %s %s' % (S, defconfig, ' '.join(map(str,fragments)))
        subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)

}
addtask prepare_configure after do_patch before do_configure
do_prepare_configure[depends] += "kconfig-frontends-native:do_populate_sysroot"

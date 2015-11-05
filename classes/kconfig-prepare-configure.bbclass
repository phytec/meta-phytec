# Copyright (C) 2015 PHYTEC Messtechnik GmbH,
# Author: Stefan Christ <s.christ@phytec.de>
# Released under the MIT license (see COPYING.MIT for the terms)

# FIXME Recipes, which use the bbclass, must define the task
# 'do_default_defconfig' by themselfs.

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
do_prepare_configure() {
    defconfig="${WORKDIR}/defconfig"
    config="${S}/.config"

    set -e

    if test -f "$defconfig"; then
	bbnote "Use defconfig from SRC_URI"
	cp -f "$defconfig" "$config"
    else
	bbnote "No defconfig file provided for the recipe"
	if test ! -f "$config"; then
	    bbnote "recipe is not configured"
	    do_default_defconfig
	fi
    fi

    fragments="${@' '.join(find_sccs(d))}"
    if test ! -z "$fragments"; then
	bbnote "combining kconfig fragments into .config"
	# Change directory to WORKDIR, because the fragments are located there
	# and filenames in variable $fragments are not absolute.
	# Use subshell to avoid changing the work directory of current shell.
	(cd "${WORKDIR}" && ${S}/scripts/kconfig/merge_config.sh -m -O "${S}" "$config" $fragments)
    fi

}
addtask prepare_configure after do_patch before do_configure
do_prepare_configure[depends] += "kconfig-frontends-native:do_populate_sysroot"

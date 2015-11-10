inherit cml1

# these variables can be configured in the recipes inheriting kconfig
INTREE_DEFCONFIG ??= ""

# class variables
KBUILD_OUTPUT = "${B}"
KBUILD_OUTPUT[export] = "1"

kconfig_set() {
    bbnote "Setting $1 in .config to $2"
    if [ "$2" == "n" ]; then
        line="# CONFIG_$1 is not set"
    else
        line="CONFIG_$1=$2"
    fi

    if [ "$(grep -E CONFIG_$1[=\ ] ${B}/.config)" ]; then
        sed -i "/CONFIG_$1[= ]/c\\$line" ${B}/.config
    else
        echo "$line" >> ${B}/.config
    fi
}

# returns and .cfg filenames from SRC_URI
def find_cfgs(d):
    sources=src_patches(d, True)
    sources_list=[]
    for s in sources:
        base, ext = os.path.splitext(os.path.basename(s))
        if ext and ext in [".cfg"]:
            sources_list.append(s)
    return sources_list

do_configure_prepend() {
    defconfig="${WORKDIR}/defconfig"
    config="${B}/.config"

    set -e

    if test -f "$defconfig"; then
	bbnote "Using defconfig from SRC_URI"
	cp -f "$defconfig" "$config"
    else
	bbnote "No defconfig file is provided for the recipe."
	if test ! -f "$config"; then
            bbnote "Using intree defconfig: ${INTREE_DEFCONFIG}"
            oe_runmake -C ${S} ${INTREE_DEFCONFIG}
	fi
    fi

    fragments="${@' '.join(find_cfgs(d))}"
    if test ! -z "$fragments"; then
	bbnote "combining kconfig fragments into .config"
	# Change directory to WORKDIR, because the fragments are located there
	# and filenames in variable $fragments are not absolute.
	# Use subshell to avoid changing the work directory of current shell.
	(cd "${WORKDIR}" && ${S}/scripts/kconfig/merge_config.sh -m -O "${B}" "$config" $fragments)
    fi

}

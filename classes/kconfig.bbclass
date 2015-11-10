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

do_intree_defconfig () {
    bbnote "generating .config for target ${INTREE_DEFCONFIG}"
    unset CFLAGS LDFLAGS
    oe_runmake -C ${S} ${INTREE_DEFCONFIG}
}

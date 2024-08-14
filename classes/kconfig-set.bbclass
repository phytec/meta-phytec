# Sets a CONFIG_ variable in .config
kconfig_set() {
    bbnote "Setting $1 in .config to $2"
    if [ "$2" = "n" ]; then
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

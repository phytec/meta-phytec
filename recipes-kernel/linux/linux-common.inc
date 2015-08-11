DEPENDS += "lzop-native lz4-native"

# this is a workaround for missing upstream patch [kernel.bbclass: Fix
# so_shared_workdir task ordering
do_compile_kernelmodules_append(){
	do_shared_workdir
}

# this is a workaround for missing upstream patch [cml1.bbclass: make
# menuconfig task configurable
# KCONFIG_CONFIG_COMMAND ?= "nconfig"
# KCONFIG_CONFIG_COMMAND ?= "MENUCONFIG_COLOR=mono menuconfig"
python do_nconfig() {
    import shutil

    try:
        mtime = os.path.getmtime(".config")
        shutil.copy(".config", ".config.orig")
    except OSError:
        mtime = 0

    oe_terminal("${SHELL} -c \"make nconfig; if [ \$? -ne 0 ]; then echo 'Command failed.'; printf 'Press any key to continue... '; read r; fi\"", '${PN} Configuration', d)
}
do_nconfig[depends] += "ncurses-native:do_populate_sysroot"
do_nconfig[nostamp] = "1"
addtask nconfig

KERNEL_IMAGE_BASE_NAME = "${KERNEL_IMAGETYPE}-${PN}-${PKGV}-${PKGR}-${MACHINE}-${DATETIME}"
do_deploy_append() {
    install -m 644 ${B}/.config ${DEPLOYDIR}/${KERNEL_IMAGE_BASE_NAME}.config
    ln -sf ${KERNEL_IMAGE_BASE_NAME}.config ${DEPLOYDIR}/${KERNEL_IMAGETYPE}.config
}

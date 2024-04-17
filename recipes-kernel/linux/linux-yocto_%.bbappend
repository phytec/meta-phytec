KCONFIG_MODE:phytec-machine = "alldefconfig"

COMPATIBLE_MACHINE:phyboard-segin-imx6ul-6 = "phyboard-segin-imx6ul-6"
KBUILD_DEFCONFIG:phyboard-segin-imx6ul-6 = "imx_v6_v7_defconfig"

# extend yocto-kernel and kernel-devicetree.bbclass
# 1. generate the oftree symlink
# 2. deploy the kernel config file
#
def get_oftree(d):
    kdt = d.getVar('KERNEL_DEVICETREE', '')
    return os.path.basename(kdt.split()[0])

FIRST_DTS = "${@get_oftree(d)}"
do_deploy:append:phytec-machine() {
    install -m 644 ${B}/.config $deployDir/$baseName.config
    ln -sf $baseName.config $deployDir/${KERNEL_IMAGETYPE}.config
    dtdtb=`normalize_dtb "$FIRST_DTS"`
    dtb_ext=${dtb##*.}
    dtb_base_name=`basename $dtb .$dtb_ext`
    ln -sf ${dtb_base_name}.${dtb_ext}  $deployDir/oftree
}

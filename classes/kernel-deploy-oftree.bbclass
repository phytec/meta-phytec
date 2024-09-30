# Generate a 'oftree' symlink to the first entry of KERNEL_DEVICETREE
# E.g. oftree -> k3-am625-phyboard-lyra-rdk.dtb

inherit kernel-devicetree

def get_oftree(d):
    kdt = d.getVar('KERNEL_DEVICETREE', '')
    return os.path.basename(kdt.split()[0])

FIRST_DTS = "${@get_oftree(d)}"
do_deploy:append() {
    if [ -z "${EXTERNAL_KERNEL_DEVICETREE}" ]; then
        ln -sf ${FIRST_DTS}  ${DEPLOYDIR}/oftree
    fi
}

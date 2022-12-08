require conf/machine/include/k3.inc
SOC_FAMILY:append = ":am64xx"

SERIAL_CONSOLES = "115200;ttyS2 115200;ttyS1"
SERIAL_CONSOLES_CHECK = "${SERIAL_CONSOLES}"

TFA_BOARD = "lite"
OPTEEMACHINE = "k3-am64x"
OPTEEOUTPUTMACHINE = "k3"

# Set the list of device targets for ti-pdk class recipes
TI_PDK_LIMIT_SOCS = "am64x"
TI_PDK_LIMIT_BOARDS = "am64x_evm"
TI_PDK_LIMIT_CORES = "mcu1_0 mpu1_0 mcu1_1 mcu2_0 mcu2_1"

# Install modules in initramfs and roofs
PACKAGE_INSTALL:append = " kernel-modules"

BBMULTICONFIG = "k3r5"

do_image_wic[mcdepends] = "mc::k3r5:ti-sci-fw:do_deploy"
do_image_tar[mcdepends] = "mc::k3r5:ti-sci-fw:do_deploy"

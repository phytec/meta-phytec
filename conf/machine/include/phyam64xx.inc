require conf/machine/include/phyk3.inc
SOC_FAMILY:append = ":am64xx"

TFA_BOARD = "lite"
OPTEEMACHINE = "k3-am64x"

# Set the list of device targets for ti-pdk class recipes
TI_PDK_LIMIT_SOCS = "am64x"
TI_PDK_LIMIT_BOARDS = "am64x_evm"
TI_PDK_LIMIT_CORES = "mcu1_0 mpu1_0 mcu1_1 mcu2_0 mcu2_1"

BBMULTICONFIG = "k3r5"

MACHINE_FEATURES:remove = "efi"
WKS_FILE ?= "sdimage-2part.wks"

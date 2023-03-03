require conf/machine/include/phyk3.inc
SOC_FAMILY:append = ":am64xx"

TFA_BOARD = "lite"
OPTEEMACHINE = "k3-am64x"

BBMULTICONFIG = "k3r5"

MACHINE_FEATURES:remove = "efi"
WKS_FILE ?= "sdimage-2part.wks"

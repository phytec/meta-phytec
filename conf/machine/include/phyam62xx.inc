require conf/machine/include/phyk3.inc
SOC_FAMILY:append = ":am62xx"

TFA_BOARD = "lite"
OPTEEMACHINE = "k3-am62x"

TFA_K3_SYSTEM_SUSPEND = "1"

# am62xx has a Rogue GPU core
PREFERRED_PROVIDER_virtual/gpudriver:am62xx = "ti-img-rogue-driver"

MACHINE_FEATURES:remove = "efi"
WKS_FILE ?= "sdimage-2part.wks"

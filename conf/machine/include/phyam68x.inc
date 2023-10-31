require conf/machine/include/phyk3.inc

SOC_FAMILY:append = ":j721s2"

# j721s2 has a Rogue GPU core
require conf/machine/include/mesa-pvr.inc
PREFERRED_PROVIDER_virtual/gpudriver:j721s2 = "ti-img-rogue-driver"

MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += "cadence-mhdp-fw cnm-wave-fw"

TFA_BOARD = "generic"
TFA_K3_USART = "0x8"
OPTEEMACHINE = "k3-j784s4"
OPTEE_K3_USART = "0x8"

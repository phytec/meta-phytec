require conf/machine/include/phyk3.inc

SOC_FAMILY:append = ":j7"

# j7 has a Rogue GPU core
require conf/machine/include/mesa-pvr.inc
PREFERRED_PROVIDER_virtual/gpudriver:j7 = "ti-img-rogue-driver"

TFA_K3_USART = "0x8"
OPTEEMACHINE = "k3-j784s4"
OPTEE_K3_USART = "0x8"
OPTEEOUTPUTMACHINE = "k3"

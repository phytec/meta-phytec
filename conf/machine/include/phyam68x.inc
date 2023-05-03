require conf/machine/include/phyk3.inc

SOC_FAMILY:append = ":j7"

# j7 has a Rogue GPU core
PREFERRED_PROVIDER_virtual/egl:j7 = "ti-img-rogue-umlibs"
PREFERRED_PROVIDER_virtual/libgles1:j7 = "ti-img-rogue-umlibs"
PREFERRED_PROVIDER_virtual/libgles2:j7 = "ti-img-rogue-umlibs"
PREFERRED_PROVIDER_virtual/libgbm:j7 = "ti-img-rogue-umlibs"
PREFERRED_PROVIDER_virtual/gpudriver:j7 = "ti-img-rogue-driver"

TFA_K3_USART = "0x8"
OPTEEMACHINE = "k3-j784s4"
OPTEE_K3_USART = "0x8"
OPTEEOUTPUTMACHINE = "k3"

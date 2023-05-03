require conf/machine/include/phyk3.inc

SOC_FAMILY_append = ":j7"

# j7 has a Rogue GPU core
PREFERRED_PROVIDER_virtual/egl_j7 = "ti-img-rogue-umlibs"
PREFERRED_PROVIDER_virtual/libgles1_j7 = "ti-img-rogue-umlibs"
PREFERRED_PROVIDER_virtual/libgles2_j7 = "ti-img-rogue-umlibs"
PREFERRED_PROVIDER_virtual/libgbm_j7 = "ti-img-rogue-umlibs"
PREFERRED_PROVIDER_virtual/gpudriver_j7 = "ti-img-rogue-driver"

TFA_K3_USART = "0x8"
OPTEEMACHINE = "k3-j784s4"
OPTEE_K3_USART = "0x8"
OPTEEOUTPUTMACHINE = "k3"

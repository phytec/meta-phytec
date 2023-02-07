require conf/machine/include/phyk3.inc
SOC_FAMILY_append = ":am62xx"

TFA_BOARD = "lite"
OPTEEMACHINE = "k3-am62x"
OPTEEOUTPUTMACHINE = "k3"

TFA_K3_SYSTEM_SUSPEND = "1"

# am62xx has a Rogue GPU core
PREFERRED_PROVIDER_virtual/egl_am62xx = "ti-img-rogue-umlibs"
PREFERRED_PROVIDER_virtual/libgles1_am62xx = "ti-img-rogue-umlibs"
PREFERRED_PROVIDER_virtual/libgles2_am62xx = "ti-img-rogue-umlibs"
PREFERRED_PROVIDER_virtual/libgbm_am62xx = "ti-img-rogue-umlibs"
PREFERRED_PROVIDER_virtual/gpudriver_am62xx = "ti-img-rogue-driver"

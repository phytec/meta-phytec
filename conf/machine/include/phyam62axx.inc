require conf/machine/include/phyk3.inc
SOC_FAMILY_append = ":am62axx"

TFA_BOARD = "lite"
OPTEEMACHINE = "k3-am62x"
OPTEEOUTPUTMACHINE = "k3"

TFA_K3_SYSTEM_SUSPEND = "1"

# This is required to use am62axx with graphical support such as QT5
PREFERRED_PROVIDER_virtual/egl_am62axx = "mesa"
PREFERRED_PROVIDER_virtual/libgles1_am62axx = "mesa"
PREFERRED_PROVIDER_virtual/libgles2_am62axx = "mesa"
PREFERRED_PROVIDER_virtual/libgbm_am62axx = "mesa"
PREFERRED_PROVIDER_virtual/gpudriver_am62axx = "mesa"

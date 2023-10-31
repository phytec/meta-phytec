require conf/machine/include/phyk3.inc
SOC_FAMILY:append = ":am62axx"

TFA_BOARD = "lite"
OPTEEMACHINE = "k3-am62x"

MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += "cnm-wave-fw"

TFA_K3_SYSTEM_SUSPEND = "1"

MACHINE_FEATURES += "screen"

# This is required to use am62axx with graphical support such as QT5
PREFERRED_PROVIDER_virtual/mesa:am62axx = "mesa"
PREFERRED_PROVIDER_virtual/egl:am62axx = "mesa"
PREFERRED_PROVIDER_virtual/libgles1:am62axx = "mesa"
PREFERRED_PROVIDER_virtual/libgles2:am62axx = "mesa"
PREFERRED_PROVIDER_virtual/libgles3:am62axx = "mesa"
PREFERRED_PROVIDER_virtual/libgbm:am62axx = "mesa"
PREFERRED_PROVIDER_virtual/libglx:am62axx = "mesa"
PREFERRED_PROVIDER_virtual/libgl:am62axx = "mesa"
PREFERRED_PROVIDER_virtual/gpudriver:am62axx = "mesa"

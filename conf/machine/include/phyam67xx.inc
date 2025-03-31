require conf/machine/include/phyk3.inc
require conf/machine/include/mc_k3r5.inc
SOC_FAMILY:append = ":j722s"

TFA_BOARD = "lite"
OPTEEMACHINE = "k3-am62x"

TFA_K3_SYSTEM_SUSPEND = "1"

# j722s has a Rogue GPU core
PREFERRED_PROVIDER_virtual/mesa ?= "mesa-pvr"
PREFERRED_PROVIDER_virtual/egl ?= "mesa-pvr"
PREFERRED_PROVIDER_virtual/libgles1 ?= "mesa-pvr"
PREFERRED_PROVIDER_virtual/libgles2 ?= "mesa-pvr"
PREFERRED_PROVIDER_virtual/libgles3 ?= "mesa-pvr"
PREFERRED_PROVIDER_virtual/libgbm ?= "mesa-pvr"
PREFERRED_PROVIDER_virtual/libglx ?= "mesa-pvr"
PREFERRED_PROVIDER_virtual/libgl ?= "mesa-pvr"
PREFERRED_PROVIDER_virtual/gpudriver ?= "ti-img-rogue-driver"

MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += "cnm-wave-fw"

MACHINE_FEATURES += "screen touchscreen pcie"

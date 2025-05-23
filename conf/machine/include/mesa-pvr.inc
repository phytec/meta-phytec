PREFERRED_PROVIDER_virtual/mesa ?= "${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/kernel", "linux-phytec-ti", "mesa-pvr", "mesa", d)}"
PREFERRED_PROVIDER_virtual/egl ?= "${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/kernel", "linux-phytec-ti", "mesa-pvr", "mesa", d)}"
PREFERRED_PROVIDER_virtual/libgles1 ?= "${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/kernel", "linux-phytec-ti", "mesa-pvr", "mesa", d)}"
PREFERRED_PROVIDER_virtual/libgles2 ?= "${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/kernel", "linux-phytec-ti", "mesa-pvr", "mesa", d)}"
PREFERRED_PROVIDER_virtual/libgles3 ?= "${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/kernel", "linux-phytec-ti", "mesa-pvr", "mesa", d)}"
PREFERRED_PROVIDER_virtual/libgbm ?= "${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/kernel", "linux-phytec-ti", "mesa-pvr", "mesa", d)}"
PREFERRED_PROVIDER_virtual/libglx ?= "${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/kernel", "linux-phytec-ti", "mesa-pvr", "mesa", d)}"
PREFERRED_PROVIDER_virtual/libgl ?= "${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/kernel", "linux-phytec-ti", "mesa-pvr", "mesa", d)}"
PREFERRED_PROVIDER_virtual/gpudriver ?= "${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/kernel", "linux-phytec-ti", "ti-img-rogue-driver", "", d)}"

MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += "${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/kernel", "linux-mainline", "linux-firmware-powervr", "", d)}"
MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += "${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/kernel", "linux-phytec", "linux-firmware-powervr", "", d)}"

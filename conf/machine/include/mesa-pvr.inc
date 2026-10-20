# TI proprietary graphics stack for K3 SoCs with a PowerVR Rogue GPU:
# ti-img-rogue-driver kernel module, ti-img-rogue-umlibs and mesa-pvr.
# Set PREFERRED_PROVIDER_virtual/gpudriver = "" to use the in-kernel powervr
# driver with linux-firmware-powervr and the Imagination / Mesa / Zink stack.
PREFERRED_PROVIDER_virtual/gpudriver ?= "ti-img-rogue-driver"

PREFERRED_PROVIDER_virtual/mesa ?= "${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/gpudriver", "ti-img-rogue-driver", "mesa-pvr", "mesa", d)}"
PREFERRED_PROVIDER_virtual/egl ?= "${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/gpudriver", "ti-img-rogue-driver", "mesa-pvr", "mesa", d)}"
PREFERRED_PROVIDER_virtual/libgles1 ?= "${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/gpudriver", "ti-img-rogue-driver", "mesa-pvr", "mesa", d)}"
PREFERRED_PROVIDER_virtual/libgles2 ?= "${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/gpudriver", "ti-img-rogue-driver", "mesa-pvr", "mesa", d)}"
PREFERRED_PROVIDER_virtual/libgles3 ?= "${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/gpudriver", "ti-img-rogue-driver", "mesa-pvr", "mesa", d)}"
PREFERRED_PROVIDER_virtual/libgbm ?= "${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/gpudriver", "ti-img-rogue-driver", "mesa-pvr", "mesa", d)}"
PREFERRED_PROVIDER_virtual/libglx ?= "${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/gpudriver", "ti-img-rogue-driver", "mesa-pvr", "mesa", d)}"
PREFERRED_PROVIDER_virtual/libgl ?= "${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/gpudriver", "ti-img-rogue-driver", "mesa-pvr", "mesa", d)}"

MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += "${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/gpudriver", "ti-img-rogue-driver", "", "linux-firmware-powervr", d)}"
PACKAGECONFIG:append:pn-mesa = " ${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/gpudriver", "ti-img-rogue-driver", "", "gallium-llvm zink", d)}"
DEPENDS:append:pn-mesa = " ${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/gpudriver", "ti-img-rogue-driver", "", "virtual/libopencl1", d)}"

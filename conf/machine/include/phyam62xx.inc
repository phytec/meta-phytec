require conf/machine/include/phyk3.inc
SOC_FAMILY:append = ":am62xx"

TFA_BOARD = "lite"
OPTEEMACHINE = "k3-am62x"

TFA_K3_SYSTEM_SUSPEND = "1"

# am62xx has a Rogue GPU core
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

MACHINE_FEATURES += "screen touchscreen"

# phyCORE-AM62x overlays
KERNEL_OVERLAYS ?= " \
    ti/k3-am6xx-phycore-disable-spi-nor.dtbo \
    ti/k3-am6xx-phycore-disable-rtc.dtbo \
    ti/k3-am6xx-phycore-disable-eth-phy.dtbo \
    ti/k3-am6xx-phycore-qspi-nor.dtbo \
"

# phyBOARD-Lyra downstream kernel overlays
KERNEL_OVERLAYS_PHYTEC = " \
    ti/k3-am62-phyboard-lyra-lpm.dtbo \
    ti/k3-am62-phyboard-lyra-m2-lwb5p.dtbo \
    ti/k3-am62-phyboard-lyra-oldi-lcd185.dtbo \
    ti/k3-am62-phyboard-lyra-oldi-ac200.dtbo \
    ti/k3-am62-phyboard-lyra-oldi-ac209a.dtbo \
    ti/k3-am62-phyboard-lyra-vm016-mipi.dtbo \
    ti/k3-am62-phyboard-lyra-x17-spi0.dtbo \
    ti/k3-am62-phyboard-lyra-x17-evse.dtbo \
"

# phyBOARD-Lyra overlays
KERNEL_OVERLAYS:append:phyboard-lyra = " \
    ti/k3-am62x-phyboard-lyra-gpio-fan.dtbo \
    ${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/kernel", "linux-phytec-ti", "${KERNEL_OVERLAYS_PHYTEC}", "", d)} \
"

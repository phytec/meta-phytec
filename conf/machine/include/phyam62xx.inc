require conf/machine/include/phyk3.inc
require conf/machine/include/mc_k3r5.inc
require conf/machine/include/mesa-pvr.inc
SOC_FAMILY:append = ":am62xx"

TFA_BOARD = "lite"
OPTEEMACHINE = "k3-am62x"

TFA_K3_SYSTEM_SUSPEND = "1"

MACHINE_FEATURES += "screen touchscreen"

# phyBOARD-Lyra downstream kernel overlays
KERNEL_OVERLAYS_PHYTEC = " \
    ti/k3-am62-phyboard-lyra-lpm.dtbo \
    ti/k3-am62-phyboard-lyra-m2-cc3351.dtbo \
    ti/k3-am62-phyboard-lyra-m2-lwb5p.dtbo \
    ti/k3-am62-phyboard-lyra-oldi-lcd185.dtbo \
    ti/k3-am62-phyboard-lyra-oldi-ac200.dtbo \
    ti/k3-am62-phyboard-lyra-oldi-ac209a.dtbo \
    ti/k3-am62-phyboard-lyra-vm016-mipi.dtbo \
    ti/k3-am62-phyboard-lyra-x17-spi0.dtbo \
    ti/k3-am62-phyboard-lyra-x17-evse.dtbo \
"

# phyBOARD-Lyra overlays
KERNEL_OVERLAYS:phyboard-lyra = " \
    ti/k3-am62x-phyboard-lyra-gpio-fan.dtbo \
    ${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/kernel", "linux-phytec-ti", "${KERNEL_OVERLAYS_PHYTEC}", "", d)} \
"

PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-phytec"
PREFERRED_PROVIDER_u-boot ?= "u-boot-phytec"
PREFERRED_PROVIDER_ti-sci-fw ?= "ti-firmware"
PREFERRED_PROVIDER_ti-dm-fw ?= "ti-firmware"

UBOOT_CAPSULE = "uboot-capsule.bin"
SPL_CAPSULE = "tispl-capsule.bin"

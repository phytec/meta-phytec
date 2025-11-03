require conf/machine/include/phyk3.inc
require conf/machine/include/mc_k3r5.inc
SOC_FAMILY:append = ":am62axx"

TFA_BOARD = "lite"
OPTEEMACHINE = "k3-am62x"

MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += "cnm-wave-fw"

TFA_K3_SYSTEM_SUSPEND = "1"

MACHINE_FEATURES += "screen touchscreen"

# phyBOARD-Lyra downstream kernel overlays
KERNEL_OVERLAYS_PHYTEC = " \
    ti/k3-am62-phyboard-lyra-m2-cc3351.dtbo \
    ti/k3-am62-phyboard-lyra-vm016-mipi.dtbo \
    ti/k3-am62-phyboard-lyra-x17-spi0.dtbo \
"

# phyBOARD-Lyra overlays
KERNEL_OVERLAYS:phyboard-lyra = " \
    ti/k3-am62x-phyboard-lyra-gpio-fan.dtbo \
    ${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/kernel", "linux-phytec-ti", "${KERNEL_OVERLAYS_PHYTEC}", "", d)} \
"

PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-phytec"
PREFERRED_PROVIDER_u-boot ?= "u-boot-phytec"
PREFERRED_PROVIDER_ti-sci-fw ?= "ti-firmware-sysfw"
PREFERRED_PROVIDER_ti-dm-fw ?= "ti-firmware-dm"

UBOOT_CAPSULE = "uboot-capsule.bin"
SPL_CAPSULE = "tispl-capsule.bin"

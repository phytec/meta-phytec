require conf/machine/include/phyk3.inc
require conf/machine/include/mc_k3r5.inc
SOC_FAMILY:append = ":am64xx"

TFA_BOARD = "lite"
OPTEEMACHINE = "k3-am64x"

MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += "prueth-fw pruhsr-fw prusw-fw"

MACHINE_FEATURES += "pcie"

BBMULTICONFIG = "k3r5"

# phyBOARD-Electra downstream kernel overlays
KERNEL_OVERLAYS_PHYTEC = " \
    ti/k3-am642-phyboard-electra-peb-c-010.dtbo \
    ti/k3-am642-phyboard-electra-x27-gpio1-spi1-uart3.dtbo \
"

# phyBOARD-Electra overlays
KERNEL_OVERLAYS:phyboard-electra = " \
    ti/k3-am642-phyboard-electra-pcie-usb2.dtbo \
    ti/k3-am642-phyboard-electra-gpio-fan.dtbo \
    ${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/kernel", "linux-phytec", "${KERNEL_OVERLAYS_PHYTEC}", "", d)} \
"

PREFERRED_PROVIDER_virtual/kernel ?= "linux-phytec"
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-phytec"
PREFERRED_PROVIDER_u-boot ?= "u-boot-phytec"
PREFERRED_PROVIDER_ti-sci-fw ?= "ti-firmware-sysfw"
PREFERRED_PROVIDER_prueth-fw ?= "ti-firmware-pru"
PREFERRED_PROVIDER_pruhsr-fw ?= "ti-firmware-pru"
PREFERRED_PROVIDER_prusw-fw ?= "ti-firmware-pru"

UBOOT_CAPSULE = "uboot-capsule.bin"
SPL_CAPSULE = "tispl-capsule.bin"

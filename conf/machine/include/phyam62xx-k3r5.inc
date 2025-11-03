require conf/machine/include/phyk3r5.inc

PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-phytec"
PREFERRED_PROVIDER_u-boot ?= "u-boot-phytec"
PREFERRED_PROVIDER_ti-sci-fw ?= "ti-firmware-sysfw"

UBOOT_CAPSULE:k3r5 = "tiboot3-capsule.bin"

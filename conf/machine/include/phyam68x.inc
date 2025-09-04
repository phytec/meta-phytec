require conf/machine/include/phyk3.inc
require conf/machine/include/mc_k3r5.inc
require conf/machine/include/mesa-pvr.inc

SOC_FAMILY:append = ":j721s2"

MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += "cadence-mhdp-fw cnm-wave-fw"
MACHINE_FEATURES += "screen touchscreen pcie"

IMAGE_CLASSES:append = " wic-bootenv-helper"
IMAGE_BOOT_FILES += "overlays.txt"

TFA_BOARD = "generic"
TFA_K3_USART = "0x8"
OPTEEMACHINE = "k3-j784s4"
OPTEE_K3_USART = "0x8"

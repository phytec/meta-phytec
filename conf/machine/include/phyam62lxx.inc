require conf/machine/include/phyk3.inc

SOC_FAMILY:append = ":am62lxx"

TFA_BOARD = "am62l"
OPTEEMACHINE = "k3-am62lx"

MACHINE_FEATURES += "screen touchscreen"

IMAGE_CLASSES:append = " wic-bootenv-helper"
IMAGE_BOOT_FILES += "overlays.txt"

SERIAL_CONSOLES = "115200;ttyS3"
# Drop MAXSIZE
KERNEL_IMAGE_MAXSIZE ?= ""

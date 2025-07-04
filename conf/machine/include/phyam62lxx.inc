require conf/machine/include/phyk3.inc

SOC_FAMILY:append = ":am62lxx"

TFA_BOARD = "am62l"
OPTEEMACHINE = "k3-am62lx"
SPL_SYMLINK = "tiboot3.bin"

MACHINE_FEATURES += "screen touchscreen"

SERIAL_CONSOLES = "115200;ttyS3"
# Drop MAXSIZE
KERNEL_IMAGE_MAXSIZE ?= ""

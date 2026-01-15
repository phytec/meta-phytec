require conf/machine/include/phyk3.inc

SOC_FAMILY:append = ":am62lxx"

TFA_PLATFORM="am6x"
TFA_BOARD="phyflex-am62l"
OPTEEMACHINE = "k3-am62lx"

MACHINE_FEATURES += "screen touchscreen"

SERIAL_CONSOLES = "115200;ttyS3"
# Drop MAXSIZE
KERNEL_IMAGE_MAXSIZE ?= ""

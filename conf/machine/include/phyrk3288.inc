#Taken from meta-rockchip Layer, original name:rk3288.inc

require conf/machine/include/tune-cortexa17.inc
require conf/machine/include/soc-family.inc

SOC_FAMILY = "rk3288"
DEFAULTTUNE = "cortexa17thf-neon"

PREFERRED_PROVIDER_virtual/kernel = "linux-mainline"
KERNEL_IMAGETYPE = "zImage"

SERIAL_CONSOLES = "115200;ttyS2"

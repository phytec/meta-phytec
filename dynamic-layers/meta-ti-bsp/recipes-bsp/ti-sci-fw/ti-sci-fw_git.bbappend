# Freeze the ti-rtos-firmware version to match with the current
# Linux/U-boot version.
require dynamic-layers/meta-ti-bsp/recipes-bsp/ti-linux-fw/ti-linux-fw-freeze.inc

SYSFW_PREFIX:phyboard-lyra-am62xx-1-k3r5 = "fs"
SYSFW_PREFIX:phyboard-lyra-am62xx-2-k3r5 = "fs"
SYSFW_PREFIX:phyboard-lyra-am62axx-1-k3r5 = "fs"

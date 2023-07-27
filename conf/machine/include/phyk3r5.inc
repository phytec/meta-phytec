require conf/machine/include/k3r5.inc

PREFERRED_PROVIDER_virtual/kernel = "linux-dummy"
PREFERRED_PROVIDER_virtual/bootloader = "u-boot-ti"
PREFERRED_PROVIDER_u-boot = "u-boot-ti"

# TODO: Remove after switching to u-boot 2023.04
SPL_BINARY = "tiboot3.bin"

# Common settings for PHYTEC's i.MX8M boards
include conf/machine/include/phyimx8.inc
require conf/machine/include/arm/armv8a/tune-cortexa53.inc

MACHINE_FEATURES += "emmc pci wifi bluetooth"

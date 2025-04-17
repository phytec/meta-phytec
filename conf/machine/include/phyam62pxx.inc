require conf/machine/include/phyk3.inc
require conf/machine/include/mc_k3r5.inc
require conf/machine/include/mesa-pvr.inc

SOC_FAMILY:append = ":am62pxx"

TFA_BOARD = "lite"
OPTEEMACHINE = "k3-am62x"

TFA_K3_SYSTEM_SUSPEND = "1"

MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += "cnm-wave-fw"

MACHINE_FEATURES += "screen touchscreen pcie"

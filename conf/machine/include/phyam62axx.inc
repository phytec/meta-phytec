require conf/machine/include/phyk3.inc
SOC_FAMILY:append = ":am62axx"

TFA_BOARD = "lite"
OPTEEMACHINE = "k3-am62x"

MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += "cnm-wave-fw"

TFA_K3_SYSTEM_SUSPEND = "1"

MACHINE_FEATURES += "screen"

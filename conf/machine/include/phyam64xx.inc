require conf/machine/include/phyk3.inc
SOC_FAMILY:append = ":am64xx"

TFA_BOARD = "lite"
OPTEEMACHINE = "k3-am64x"

MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += "prueth-fw pruhsr-fw prusw-fw"

BBMULTICONFIG = "k3r5"

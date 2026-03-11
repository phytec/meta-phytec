require conf/machine/include/phyk3.inc
require conf/machine/include/mc_k3r5.inc
require conf/machine/include/mesa-pvr.inc

SOC_FAMILY:append = ":j721s2"

MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += "cadence-mhdp-fw cnm-wave-fw"
MACHINE_FEATURES += "fan screen touchscreen pcie"

TFA_BOARD = "generic"
TFA_K3_USART = "0x8"
OPTEEMACHINE = "k3-j784s4"
OPTEE_K3_USART = "0x8"

KERNEL_DEVICETREE = " \
    ti/k3-am68-phyboard-izar.dtb \
    ti/k3-am68-phyboard-izar-dp.dtbo \
    ti/k3-am68-phyboard-izar-pwm-fan.dtbo \
    ti/k3-am68-phyboard-izar-peb-av-15.dtbo \
    ti/k3-am68-phyboard-izar-lvds-ph128800t006.dtbo \
    ti/k3-am68-phyboard-izar-vm016-mipi-csi0.dtbo \
    ti/k3-am68-phyboard-izar-vm016-mipi-csi1.dtbo \
    ti/k3-am68-phyboard-izar-vm017-mipi-csi0.dtbo \
    ti/k3-am68-phyboard-izar-vm017-mipi-csi1.dtbo \
    ti/k3-am68-phyboard-izar-vm020-mipi-csi0.dtbo \
    ti/k3-am68-phyboard-izar-vm020-mipi-csi1.dtbo \
"

require conf/machine/include/phyk3.inc
require conf/machine/include/mc_k3r5.inc
require conf/machine/include/mesa-pvr.inc

SOC_FAMILY:append = ":j722s:am67xx"

TFA_BOARD = "lite"
OPTEEMACHINE = "k3-am62x"

TFA_K3_SYSTEM_SUSPEND = "1"

MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += "cnm-wave-fw"

MACHINE_FEATURES += "screen touchscreen pcie"

#phyBOARD-Rigel downstream kernel overlays
KERNEL_OVERLAYS_PHYTEC = " \
    ti/k3-am6754-phyboard-rigel-dsi-lcd282.dtbo \
    ti/k3-am6754-phyboard-rigel-gpio-fan.dtbo \
    ti/k3-am6754-phyboard-rigel-m2-cc3351.dtbo \
    ti/k3-am6754-phyboard-rigel-mcu-mcans.dtbo \
    ti/k3-am6754-phyboard-rigel-oldi-lcd185.dtbo \
    ti/k3-am6754-phyboard-rigel-vm016-mipi-csi0.dtbo \
    ti/k3-am6754-phyboard-rigel-vm016-mipi-csi1.dtbo \
    ti/k3-am6754-phyboard-rigel-vm016-mipi-csi2.dtbo \
    ti/k3-am6754-phyboard-rigel-vm016-mipi-csi3.dtbo \
"

#phyBOARD_Rigel overlays
KERNEL_OVERLAYS:phyboard-rigel = " \
    ${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/kernel", "linux-phytec-ti", "${KERNEL_OVERLAYS_PHYTEC}", "", d)} \
"

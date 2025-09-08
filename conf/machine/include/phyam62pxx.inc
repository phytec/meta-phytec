require conf/machine/include/phyk3.inc
require conf/machine/include/mc_k3r5.inc
require conf/machine/include/mesa-pvr.inc

SOC_FAMILY:append = ":am62pxx"

TFA_BOARD = "lite"
OPTEEMACHINE = "k3-am62x"

TFA_K3_SYSTEM_SUSPEND = "1"

MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += "cnm-wave-fw"

MACHINE_FEATURES += "screen touchscreen pcie"

#phyBOARD-Lynx downstream kernel overlays
KERNEL_OVERLAYS_PHYTEC = " \
    ti/k3-am62p5-phyboard-lynx-oldi-lcd185.dtbo \
    ti/k3-am62p5-phyboard-lynx-dsi-lcd282.dtbo \
    ti/k3-am62p5-phyboard-lynx-m2-cc3351.dtbo \
    ti/k3-am62p5-phyboard-lynx-vm016-mipi.dtbo \
"

#phyBOARD_Lynx overlays
KERNEL_OVERLAYS:phyboard-lynx = " \
    ${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/kernel", "linux-phytec-ti", "${KERNEL_OVERLAYS_PHYTEC}", "", d)} \
"

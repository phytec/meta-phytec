require conf/machine/include/phyk3.inc
SOC_FAMILY:append = ":am64xx"

TFA_BOARD = "lite"
OPTEEMACHINE = "k3-am64x"

MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += "prueth-fw pruhsr-fw prusw-fw"

BBMULTICONFIG = "k3r5"

# phyCORE-AM64x overlays
KERNEL_OVERLAYS ?= " \
    ti/k3-am6xx-phycore-disable-spi-nor.dtbo \
    ti/k3-am6xx-phycore-disable-rtc.dtbo \
    ti/k3-am6xx-phycore-disable-eth-phy.dtbo \
    ti/k3-am6xx-phycore-qspi-nor.dtbo \
"

# phyBOARD-Electra downstream kernel overlays
KERNEL_OVERLAYS_PHYTEC = " \
    ti/k3-am642-phyboard-electra-x27-gpio1-spi1-uart3.dtbo \
"

# phyBOARD-Electra overlays
KERNEL_OVERLAYS:append:phyboard-electra = " \
    ti/k3-am642-phyboard-electra-pcie-usb2.dtbo \
    ti/k3-am642-phyboard-electra-gpio-fan.dtbo \
    ${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/kernel", "linux-phytec", "${KERNEL_OVERLAYS_PHYTEC}", "", d)} \
"

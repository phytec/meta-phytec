INTREE_DEFCONFIG:append:hardening = " imx6_phytec_hardening.config"

SRC_URI:append:hardening = " \
    ${@bb.utils.contains('MACHINE_FEATURES', 'wifi', 'file://wifi.cfg', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'bluetooth', 'file://bluetooth.cfg', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'can', 'file://can.cfg', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'pci', 'file://pci.cfg', '', d)} \
"

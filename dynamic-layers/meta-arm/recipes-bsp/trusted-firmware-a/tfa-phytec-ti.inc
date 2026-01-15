COMPATIBLE_MACHINE = "k3"
TFA_BUILD_TARGET = "all"
TFA_INSTALL_TARGET = "bl31"
TFA_SPD = "opteed"

TFA_TI = ""
TFA_TI:am62lxx = "recipes-bsp/trusted-firmware-a/trusted-firmware-a-ti.inc"

require ${TFA_TI}

SRCREV_tfa:am62lxx = "f0161477134214112d2ea3eebb198830bab74088"
SRCREV_tfa:am62lxx-phyflex-libra-rdk-1 = "8834bbdf98a91dcc9ac4651d1588d95343b385f5"
SRC_URI_TRUSTED_FIRMWARE_A:am62lxx = "git://git@github.com/phytec/tf-a-phytec.git;protocol=https"
SRCBRANCH:am62lxx = "v2.12.0-ti-phy"

TFA_INSTALL_TARGET:am62lxx = "bl31 bl1"
TFA_SPD:am62lxx = ""
EXTRA_OEMAKE:append = "${@ ' K3_USART=' + d.getVar('TFA_K3_USART') if d.getVar('TFA_K3_USART') else ''}"
EXTRA_OEMAKE:append = "${@ ' K3_PM_SYSTEM_SUSPEND=' + d.getVar('TFA_K3_SYSTEM_SUSPEND') if d.getVar('TFA_K3_SYSTEM_SUSPEND') else ''}"

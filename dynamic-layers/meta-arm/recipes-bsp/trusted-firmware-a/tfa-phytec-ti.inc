COMPATIBLE_MACHINE = "k3"
TFA_BUILD_TARGET = "all"
TFA_INSTALL_TARGET = "bl31"
TFA_SPD = "opteed"

TFA_TI = ""
TFA_TI:am62lxx = "recipes-bsp/trusted-firmware-a/trusted-firmware-a-ti.inc"

require ${TFA_TI}

SRCREV_tfa:am62lxx = "7e3c5620887cc32c74166ac2a114b380303e3d1c"
SRC_URI_TRUSTED_FIRMWARE_A:am62lxx = "git://git@github.com/phytec/tf-a-phytec.git;protocol=https"
SRCBRANCH:am62lxx = "v2.14.0-ti-phy"

TFA_INSTALL_TARGET:am62lxx = "bl31 bl1"
EXTRA_OEMAKE:append = "${@ ' K3_USART=' + d.getVar('TFA_K3_USART') if d.getVar('TFA_K3_USART') else ''}"

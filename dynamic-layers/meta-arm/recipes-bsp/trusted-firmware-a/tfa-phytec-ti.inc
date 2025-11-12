COMPATIBLE_MACHINE = "k3"
TFA_BUILD_TARGET = "all"
TFA_INSTALL_TARGET = "bl31"
TFA_SPD = "opteed"

TFA_TI = ""
TFA_TI:am62lxx = "recipes-bsp/trusted-firmware-a/trusted-firmware-a-ti.inc"

require ${TFA_TI}

SRC_URI:append:am62lxx-libra-fpsc-1 = " \
    file://0001-fdts-Override-DDRSS-register-for-PFL-G-03.patch \
"

SRC_URI:append:am62lxx-libra-fpsc-2 = " \
    file://0001-fdts-Override-DDRSS-register-for-PFL-G-03.patch \
    file://0002-fdts-k3-am62l-ddr-Set-RAM-to-1GB.patch \
"

TFA_INSTALL_TARGET:am62lxx = "bl31 bl1"
TFA_SPD:am62lxx = ""
EXTRA_OEMAKE:append = "${@ ' K3_USART=' + d.getVar('TFA_K3_USART') if d.getVar('TFA_K3_USART') else ''}"
EXTRA_OEMAKE:append = "${@ ' K3_PM_SYSTEM_SUSPEND=' + d.getVar('TFA_K3_SYSTEM_SUSPEND') if d.getVar('TFA_K3_SYSTEM_SUSPEND') else ''}"

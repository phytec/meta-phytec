require imx-boot-secureboot.inc

OEI_ENABLE = "${@bb.utils.contains('DEPENDS', 'imx-oei-phytec', 'YES', 'NO', d)}"

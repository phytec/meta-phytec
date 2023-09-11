RCONFLICTS:${PN} = "${@bb.utils.contains("MACHINE_FEATURES", "lwb5p", "linux-firmware-bcm4373", "", d)}"
RREPLACES:${PN} = "${@bb.utils.contains("MACHINE_FEATURES", "lwb5p", "linux-firmware-bcm4373", "", d)}"
RPROVIDES:${PN} = "${@bb.utils.contains("MACHINE_FEATURES", "lwb5p", "linux-firmware-bcm4373", "", d)}"

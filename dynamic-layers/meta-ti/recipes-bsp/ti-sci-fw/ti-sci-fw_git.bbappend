DEPENDS:append_phyboard-electra-am64xx-1-k3r5 = " virtual/bootloader"
DEPENDS:append:phyboard-lyra-am62xx-1-k3r5 = " virtual/bootloader"

EXTRA_OEMAKE:append_phyboard-electra-am64xx-1-k3r5 = " SBL="${STAGING_DIR_HOST}/boot/u-boot-spl.bin""
EXTRA_OEMAKE:append:phyboard-lyra-am62xx-1-k3r5 = " SBL="${STAGING_DIR_HOST}/boot/u-boot-spl.bin""
SYSFW_PREFIX:phyboard-lyra-am62xx-1-k3r5 = "ti-fs-firmware"

do_install_phyboard-electra-am64xx-1-k3r5 () {
        install -d ${D}/boot
        install -m 644 ${WORKDIR}/imggen/${UBOOT_BINARY} ${D}/boot/${UBOOT_IMAGE}
        ln -sf ${UBOOT_IMAGE} ${D}/boot/${UBOOT_SYMLINK}
        ln -sf ${UBOOT_IMAGE} ${D}/boot/${UBOOT_BINARY}
}

do_deploy_phyboard-electra-am64xx-1-k3r5 () {
        install -d ${DEPLOYDIR}
        install -m 644 ${WORKDIR}/imggen/${UBOOT_BINARY} ${DEPLOYDIR}/${UBOOT_IMAGE}
        ln -sf ${UBOOT_IMAGE} ${DEPLOYDIR}/${UBOOT_SYMLINK}
        ln -sf ${UBOOT_IMAGE} ${DEPLOYDIR}/${UBOOT_BINARY}
        install -m 644 ${SYSFW_TISCI} ${DEPLOYDIR}/
}

do_install:phyboard-lyra-am62xx-1-k3r5 () {
        install -d ${D}/boot
        install -m 644 ${WORKDIR}/imggen/${UBOOT_BINARY} ${D}/boot/${UBOOT_IMAGE}
        ln -sf ${UBOOT_IMAGE} ${D}/boot/${UBOOT_SYMLINK}
        ln -sf ${UBOOT_IMAGE} ${D}/boot/${UBOOT_BINARY}
}

do_deploy:phyboard-lyra-am62xx-1-k3r5 () {
        install -d ${DEPLOYDIR}
        install -m 644 ${WORKDIR}/imggen/${UBOOT_BINARY} ${DEPLOYDIR}/${UBOOT_IMAGE}
        ln -sf ${UBOOT_IMAGE} ${DEPLOYDIR}/${UBOOT_SYMLINK}
        ln -sf ${UBOOT_IMAGE} ${DEPLOYDIR}/${UBOOT_BINARY}
        install -m 644 ${SYSFW_TISCI} ${DEPLOYDIR}/
}

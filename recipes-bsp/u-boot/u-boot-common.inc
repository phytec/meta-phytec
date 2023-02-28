# Deploy u-boot configs
do_deploy:append() {
    install -m 644 ${B}/.config ${DEPLOYDIR}/${UBOOT_BINARY}-${MACHINE}-${PV}-${PR}.config
    ln -sf ${UBOOT_BINARY}-${MACHINE}-${PV}-${PR}.config ${DEPLOYDIR}/${UBOOT_BINARY}.config
}

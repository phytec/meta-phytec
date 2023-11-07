FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
require recipes-security/optee/optee-os.inc
SRCREV = "001ace6655dd6bb9cbe31aa31b4ba69746e1a1d9"

# SoC specific patches
SRC_URI:append:mx8m-nxp-bsp = " \
    file://0001-BACKPORT-for-3.22-TEE-639-drivers-caam-skip-JR-init-.patch \
"

do_compile:append() {
    readelf -h ${B}/core/tee.elf | grep Entry | awk '{print $4}' > ${B}/core/tee-init_load_addr.txt
}

do_install:append() {
    if [ "${PN}" = "optee-os" ] ; then
        install -d ${D}${nonarch_base_libdir}/firmware/
        install -m 644 ${B}/core/tee-init_load_addr.txt ${D}${nonarch_base_libdir}/firmware/
        install -m 644 ${B}/conf.mk ${D}${nonarch_base_libdir}/firmware/${PN}-config-${MACHINE}-${PV}-${PR}
    fi
}

do_deploy:append() {
    if [ "${PN}" = "optee-os" ] ; then
        # Link tee.bin so it can be consumed by recipes such as imx-boot
        ln -sf ${MLPREFIX}optee/tee-pager_v2.bin ${DEPLOYDIR}/tee.bin
    fi
}

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
require recipes-security/optee/optee-os.inc
require optee-phytec.inc

SRCREV = "2a5b1d1232f582056184367fb58a425ac7478ec6"

# SoC specific patches
SRC_URI:append:mx8m-nxp-bsp = " \
    file://0001-BACKPORT-for-4.0.0-TEE-639-drivers-caam-skip-JR-init.patch \
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

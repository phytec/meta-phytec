require u-boot-capsule.inc

DEPENDS += " \
    bc-native \
    dtc-native \
    gnutls-native \
"

PROVIDES += "u-boot"

do_deploy:append() {
    if [ -n "${UBOOT_CONFIG}" ]; then
        for config in ${UBOOT_MACHINE}; do
            i=$(expr $i + 1);
            for type in ${UBOOT_CONFIG}; do
                j=$(expr $j + 1);
                if [ $j -eq $i ]; then
                    install -m 644 ${B}/${config}-${type}/.config ${DEPLOYDIR}/${UBOOT_BINARY}-${MACHINE}-${config}-${type}.config
                    ln -sf ${UBOOT_BINARY}-${MACHINE}-${config}-${type}.config ${DEPLOYDIR}/${UBOOT_BINARY}-${type}.config
                    ln -sf ${UBOOT_BINARY}-${MACHINE}-${config}-${type}.config ${DEPLOYDIR}/${UBOOT_BINARY}.config
                fi
            done
            unset  j
        done
        unset  i
    else
        install -m 644 ${B}/.config ${DEPLOYDIR}/${UBOOT_BINARY}-${MACHINE}-${PV}-${PR}.config
        ln -sf ${UBOOT_BINARY}-${MACHINE}-${PV}-${PR}.config ${DEPLOYDIR}/${UBOOT_BINARY}.config
    fi
}

do_compile:prepend:k3r5() {
    if [ -n "${UBOOT_CONFIG}" ]; then
        for config in ${UBOOT_MACHINE}; do
            i=$(expr $i + 1);
            for type in ${UBOOT_CONFIG}; do
                j=$(expr $j + 1);
                if [ $j -eq $i ]; then
                    if ! [ -L ${B}/${config}-${type}/${UBOOT_BINARY} ] && ! [ -f ${B}/${config}-${type}/${UBOOT_BINARY} ]; then
                        ln -s ${B}/${config}-${type}/spl/u-boot-spl.${UBOOT_SUFFIX} ${B}/${config}-${type}/${UBOOT_BINARY}
                    fi
                fi
            done
            unset  j
        done
        unset  i
    else
        if ! [ -f ${B}/${UBOOT_BINARY} ]; then
            ln -s spl/u-boot-spl.${UBOOT_SUFFIX} ${B}/${UBOOT_BINARY}
        fi
    fi
}

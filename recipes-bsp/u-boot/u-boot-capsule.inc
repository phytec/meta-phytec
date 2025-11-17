# Some versions of U-Boot also generate UEFI capsule update archives used for
# updating the bootloader. These archives must be deployed alongside the
# U-Boot binaries. For versions that support capsule updates, the following
# variables can be set to enable packaging of the capsules.
UBOOT_CAPSULE ?= ""
UBOOT_CAPSULE_SUFFIX ?= "bin"
UBOOT_CAPSULE_BINARYNAME ?= "${@os.path.splitext(d.getVar("UBOOT_CAPSULE"))[0]}"
UBOOT_CAPSULE_IMAGE ?= "${UBOOT_CAPSULE_BINARYNAME}-${MACHINE}-${PV}-${PR}.${UBOOT_CAPSULE_SUFFIX}"
UBOOT_CAPSULE_SYMLINK ?= "${UBOOT_CAPSULE_BINARYNAME}-${MACHINE}.${UBOOT_CAPSULE_SUFFIX}"

SPL_CAPSULE ?= ""
SPL_CAPSULE_SUFFIX ?= "bin"
SPL_CAPSULE_BINARYNAME ?= "${@os.path.splitext(d.getVar("SPL_CAPSULE"))[0]}"
SPL_CAPSULE_IMAGE ?= "${SPL_CAPSULE_BINARYNAME}-${MACHINE}-${PV}-${PR}.${SPL_CAPSULE_SUFFIX}"
SPL_CAPSULE_SYMLINK ?= "${SPL_CAPSULE_BINARYNAME}-${MACHINE}.${SPL_CAPSULE_SUFFIX}"

do_install:append () {
    if [ -n "${UBOOT_CAPSULE}" ]
    then
        if [ -n "${UBOOT_CONFIG}" ]
        then
            for config in ${UBOOT_MACHINE}; do
                i=$(expr $i + 1);
                for type in ${UBOOT_CONFIG}; do
                    j=$(expr $j + 1);
                    if [ $j -eq $i ]
                    then
                        uboot_install_capsule_config $config $type
                    fi
                done
                unset j
            done
            unset i
        else
            uboot_install_capsule
        fi
    fi

    if [ -n "${SPL_CAPSULE}" ]
    then
        if [ -n "${UBOOT_CONFIG}" ]
        then
            for config in ${UBOOT_MACHINE}; do
                i=$(expr $i + 1);
                for type in ${UBOOT_CONFIG}; do
                    j=$(expr $j + 1);
                    if [ $j -eq $i ]
                    then
                        uboot_install_spl_capsule_config $config $type
                    fi
                done
                unset j
            done
            unset i
        else
            uboot_install_spl_capsule
        fi
    fi
}

uboot_install_capsule_config () {
    config=$1
    type=$2
    builddir="${config}-${type}"

    install -m 644 ${B}/${builddir}/${UBOOT_CAPSULE} ${D}/boot/${UBOOT_CAPSULE_BINARYNAME}-${type}-${PV}-${PR}.${UBOOT_CAPSULE_SUFFIX}
    ln -sf ${UBOOT_CAPSULE_BINARYNAME}-${type}-${PV}-${PR}.${UBOOT_CAPSULE_SUFFIX} ${D}/boot/${UBOOT_CAPSULE}-${type}
    ln -sf ${UBOOT_CAPSULE_BINARYNAME}-${type}-${PV}-${PR}.${UBOOT_CAPSULE_SUFFIX} ${D}/boot/${UBOOT_CAPSULE}
}

uboot_install_capsule () {
    install -m 644 ${B}/${UBOOT_CAPSULE} ${D}/boot/${UBOOT_CAPSULE_IMAGE}
    ln -sf ${UBOOT_CAPSULE_IMAGE} ${D}/boot/${UBOOT_CAPSULE}
}

uboot_install_spl_capsule_config () {
    config=$1
    type=$2
    builddir="${config}-${type}"

    install -m 644 ${B}/${builddir}/${SPL_CAPSULE} ${D}/boot/${SPL_CAPSULE_BINARYNAME}-${type}-${PV}-${PR}.${SPL_CAPSULE_SUFFIX}
    ln -sf ${SPL_CAPSULE_BINARYNAME}-${type}-${PV}-${PR}.${SPL_CAPSULE_SUFFIX} ${D}/boot/${SPL_CAPSULE}-${type}
    ln -sf ${SPL_CAPSULE_BINARYNAME}-${type}-${PV}-${PR}.${SPL_CAPSULE_SUFFIX} ${D}/boot/${SPL_CAPSULE}
}

uboot_install_spl_capsule () {
    install -m 644 ${B}/${SPL_CAPSULE} ${D}/boot/${SPL_CAPSULE_IMAGE}
    ln -sf ${SPL_CAPSULE_IMAGE} ${D}/boot/${SPL_CAPSULE}
}

do_deploy:append () {
    if [ -n "${UBOOT_CAPSULE}" ]
    then
        if [ -n "${UBOOT_CONFIG}" ]
        then
            for config in ${UBOOT_MACHINE}; do
                i=$(expr $i + 1);
                for type in ${UBOOT_CONFIG}; do
                    j=$(expr $j + 1);
                    if [ $j -eq $i ]
                    then
                        uboot_deploy_capsule_config $config $type
                    fi
                done
                unset j
            done
            unset i
        else
            uboot_deploy_capsule
        fi
    fi

    if [ -n "${SPL_CAPSULE}" ]
     then
        if [ -n "${UBOOT_CONFIG}" ]
        then
            for config in ${UBOOT_MACHINE}; do
                i=$(expr $i + 1);
                for type in ${UBOOT_CONFIG}; do
                    j=$(expr $j + 1);
                    if [ $j -eq $i ]
                    then
                        uboot_deploy_spl_capsule_config $config $type
                    fi
                done
                unset j
            done
            unset i
        else
            uboot_deploy_spl_capsule
        fi
    fi
}

uboot_deploy_capsule_config () {
    config=$1
    type=$2
    builddir="${config}-${type}"

    install -m 644 ${B}/${builddir}/${UBOOT_CAPSULE} ${DEPLOYDIR}/${UBOOT_CAPSULE_BINARYNAME}-${type}-${PV}-${PR}.${UBOOT_CAPSULE_SUFFIX}
    ln -sf ${UBOOT_CAPSULE_BINARYNAME}-${type}-${PV}-${PR}.${UBOOT_CAPSULE_SUFFIX} ${DEPLOYDIR}/${UBOOT_CAPSULE}-${type}
    ln -sf ${UBOOT_CAPSULE_BINARYNAME}-${type}-${PV}-${PR}.${UBOOT_CAPSULE_SUFFIX} ${DEPLOYDIR}/${UBOOT_CAPSULE}
    ln -sf ${UBOOT_CAPSULE_BINARYNAME}-${type}-${PV}-${PR}.${UBOOT_CAPSULE_SUFFIX} ${DEPLOYDIR}/${UBOOT_CAPSULE_SYMLINK}-${type}
    ln -sf ${UBOOT_CAPSULE_BINARYNAME}-${type}-${PV}-${PR}.${UBOOT_CAPSULE_SUFFIX} ${DEPLOYDIR}/${UBOOT_CAPSULE_SYMLINK}
}

uboot_deploy_capsule () {
    install -m 644 ${B}/${UBOOT_CAPSULE} ${DEPLOYDIR}/${UBOOT_CAPSULE_IMAGE}
    ln -sf ${UBOOT_CAPSULE_IMAGE} ${DEPLOYDIR}/${UBOOT_CAPSULE}
    ln -sf ${UBOOT_CAPSULE_IMAGE} ${DEPLOYDIR}/${UBOOT_CAPSULE_SYMLINK}
}

uboot_deploy_spl_capsule_config () {
    config=$1
    type=$2
    builddir="${config}-${type}"

    install -m 644 ${B}/${builddir}/${SPL_CAPSULE} ${DEPLOYDIR}/${SPL_CAPSULE_BINARYNAME}-${type}-${PV}-${PR}.${SPL_CAPSULE_SUFFIX}
    rm -f ${DEPLOYDIR}/${SPL_CAPSULE_BINARYFILE} ${DEPLOYDIR}/${SPL_CAPSULE_SYMLINK}
    ln -sf ${SPL_CAPSULE_BINARYNAME}-${type}-${PV}-${PR}.${SPL_CAPSULE_SUFFIX} ${DEPLOYDIR}/${SPL_CAPSULE_BINARYFILE}-${type}
    ln -sf ${SPL_CAPSULE_BINARYNAME}-${type}-${PV}-${PR}.${SPL_CAPSULE_SUFFIX} ${DEPLOYDIR}/${SPL_CAPSULE_BINARYFILE}
    ln -sf ${SPL_CAPSULE_BINARYNAME}-${type}-${PV}-${PR}.${SPL_CAPSULE_SUFFIX} ${DEPLOYDIR}/${SPL_CAPSULE_SYMLINK}-${type}
    ln -sf ${SPL_CAPSULE_BINARYNAME}-${type}-${PV}-${PR}.${SPL_CAPSULE_SUFFIX} ${DEPLOYDIR}/${SPL_CAPSULE_SYMLINK}
}

uboot_deploy_spl_capsule () {
    install -m 644 ${B}/${SPL_CAPSULE} ${DEPLOYDIR}/${SPL_CAPSULE_IMAGE}
    ln -sf ${SPL_CAPSULE_IMAGE} ${DEPLOYDIR}/${SPL_CAPSULE}
    ln -sf ${SPL_CAPSULE_IMAGE} ${DEPLOYDIR}/${SPL_CAPSULE_SYMLINK}
}

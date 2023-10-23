# remove symlinks to u-boot binaries created by
# recipes-bsp/u-boot/u-boot.inc which are not required for imx-boot-phytec
do_deploy:append () {
    if [ -n "${UBOOT_CONFIG}" ]
    then
        for config in ${UBOOT_MACHINE}; do
            i=$(expr $i + 1);
            for type in ${UBOOT_CONFIG}; do
                j=$(expr $j + 1);
                if [ $j -eq $i ]
                then
                    cd ${DEPLOYDIR}
                    rm -f ${UBOOT_SYMLINK}
                    rm -f ${UBOOT_BINARY}-${type}

                    # Deploy the uboot-initial-env
                    if [ -n "${UBOOT_INITIAL_ENV}" ]; then
                        cd ${DEPLOYDIR}
                        rm -f ${UBOOT_INITIAL_ENV}-${type}
                    fi
                fi
            done
            unset j
        done
        unset i
    else
        cd ${DEPLOYDIR}
        rm -f ${UBOOT_BINARY}

        # Deploy the uboot-initial-env
        if [ -n "${UBOOT_INITIAL_ENV}" ]; then
            cd ${DEPLOYDIR}
            rm -f ${UBOOT_INITIAL_ENV}
        fi
    fi

    if [ -n "${SPL_BINARY}" ]
     then
        if [ -n "${UBOOT_CONFIG}" ]
        then
            for config in ${UBOOT_MACHINE}; do
                i=$(expr $i + 1);
                for type in ${UBOOT_CONFIG}; do
                    j=$(expr $j + 1);
                    if [ $j -eq $i ]
                    then
                        rm -f ${DEPLOYDIR}/${SPL_BINARYFILE}-${type}
                        rm -f ${DEPLOYDIR}/${SPL_BINARYFILE}
                        rm -f ${DEPLOYDIR}/${SPL_SYMLINK}
                    fi
                done
                unset j
            done
            unset i
        else
            rm -f ${DEPLOYDIR}/${SPL_BINARYNAME}
        fi
    fi
}

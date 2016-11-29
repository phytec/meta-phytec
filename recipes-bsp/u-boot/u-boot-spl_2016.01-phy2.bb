inherit phygittag
require recipes-bsp/u-boot/u-boot.inc

PROVIDES = "virtual/prebootloader"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=0507cd7da8e7ad6d6701926ec9b84c95"

DEPENDS += "dtc-native"

GIT_URL = "git://git.phytec.de/u-boot"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

S = "${WORKDIR}/git"

SRCREV = "6e5f0d9ab23a524d04959b54810850807175cd76"
SRC_URI += " file://0001-compiler-.h-sync-include-linux-compiler-.h-with-Linu.patch"

PR = "r0"

COMPATIBLE_MACHINE = "firefly-rk3288-1"
SRC_URI_append_firefly-rk3288-1 = " file://rk-dbg-uart2.cfg"
COMPATIBLE_MACHINE .= "|phycore-rk3288-1"
SRC_URI_append_phycore-rk3288-1 = " file://rk-dbg-uart0.cfg"
COMPATIBLE_MACHINE .= "|phycore-rk3288-2"
SRC_URI_append_phycore-rk3288-2 = " file://rk-dbg-uart0.cfg"

UBOOT_SUFFIX = "img"
UBOOT_BINARY = "u-boot-dtb.${UBOOT_SUFFIX}"
SPL_BINARY = "u-boot-spl-dtb.bin"

do_install () {
    if [ "x${UBOOT_CONFIG}" != "x" ]
    then
        for config in ${UBOOT_MACHINE}; do
            i=`expr $i + 1`;
            for type in ${UBOOT_CONFIG}; do
                j=`expr $j + 1`;
                if [ $j -eq $i ]
                then
                    install -d ${D}/boot
                    install ${B}/${config}/u-boot-${type}.${UBOOT_SUFFIX} ${D}/boot/u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX}
                    ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${D}/boot/${UBOOT_BINARY}-${type}
                    ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${D}/boot/${UBOOT_BINARY}
                fi
            done
            unset  j
        done
        unset  i
    else
        install -d ${D}/boot
        install ${B}/${UBOOT_BINARY} ${D}/boot/${UBOOT_IMAGE}
        ln -sf ${UBOOT_IMAGE} ${D}/boot/${UBOOT_BINARY}
    fi

    if [ "x${UBOOT_ELF}" != "x" ]
    then
        if [ "x${UBOOT_CONFIG}" != "x" ]
        then
            for config in ${UBOOT_MACHINE}; do
                i=`expr $i + 1`;
                for type in ${UBOOT_CONFIG}; do
                    j=`expr $j + 1`;
                    if [ $j -eq $i ]
                    then
                        install ${B}/${config}/${UBOOT_ELF} ${D}/boot/u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX}
                        ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX} ${D}/boot/${UBOOT_BINARY}-${type}
                        ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX} ${D}/boot/${UBOOT_BINARY}
                    fi
                done
                unset j
            done
            unset i
        else
            install ${B}/${UBOOT_ELF} ${D}/boot/${UBOOT_ELF_IMAGE}
            ln -sf ${UBOOT_ELF_IMAGE} ${D}/boot/${UBOOT_ELF_BINARY}
        fi
    fi

    if [ -e ${WORKDIR}/fw_env.config ] ; then
        install -d ${D}${sysconfdir}
        install -m 644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
    fi

    if [ "x${SPL_BINARY}" != "x" ]
    then
        if [ "x${UBOOT_CONFIG}" != "x" ]
        then
            for config in ${UBOOT_MACHINE}; do
                i=`expr $i + 1`;
                for type in ${UBOOT_CONFIG}; do
                    j=`expr $j + 1`;
                    if [ $j -eq $i ]
                    then
                         install ${B}/${config}/${SPL_BINARY} ${D}/boot/${SPL_IMAGE}-${type}-${PV}-${PR}
                         ln -sf ${SPL_IMAGE}-${type}-${PV}-${PR} ${D}/boot/${SPL_BINARY}-${type}
                         ln -sf ${SPL_IMAGE}-${type}-${PV}-${PR} ${D}/boot/${SPL_BINARY}
                    fi
                done
                unset  j
            done
            unset  i
        else
            if [ -f ${B}/${SPL_BINARY} ]
            then
                install ${B}/${SPL_BINARY} ${D}/boot/${SPL_IMAGE}
            else
                install ${B}/spl/${SPL_BINARY} ${D}/boot/${SPL_IMAGE}
            fi
            ln -sf ${SPL_IMAGE} ${D}/boot/${SPL_BINARY}
        fi
    fi

    if [ "x${UBOOT_ENV}" != "x" ]
    then
        install ${WORKDIR}/${UBOOT_ENV_BINARY} ${D}/boot/${UBOOT_ENV_IMAGE}
        ln -sf ${UBOOT_ENV_IMAGE} ${D}/boot/${UBOOT_ENV_BINARY}
    fi
}

do_deploy () {
    if [ "x${UBOOT_CONFIG}" != "x" ]
    then
        for config in ${UBOOT_MACHINE}; do
            i=`expr $i + 1`;
            for type in ${UBOOT_CONFIG}; do
                j=`expr $j + 1`;
                if [ $j -eq $i ]
                then
                    install -d ${DEPLOYDIR}
                    install ${B}/${config}/u-boot-${type}.${UBOOT_SUFFIX} ${DEPLOYDIR}/u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX}
                    cd ${DEPLOYDIR}
                    ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${UBOOT_SYMLINK}-${type}
                    ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${UBOOT_SYMLINK}
                    ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${UBOOT_BINARY}-${type}
                    ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${UBOOT_BINARY}
                fi
            done
            unset  j
        done
        unset  i
    else
        install -d ${DEPLOYDIR}
        install ${B}/${UBOOT_BINARY} ${DEPLOYDIR}/${UBOOT_IMAGE}
        cd ${DEPLOYDIR}
        rm -f ${UBOOT_BINARY} ${UBOOT_SYMLINK}
        ln -sf ${UBOOT_IMAGE} ${UBOOT_SYMLINK}
        ln -sf ${UBOOT_IMAGE} ${UBOOT_BINARY}
   fi

    if [ "x${UBOOT_ELF}" != "x" ]
    then
        if [ "x${UBOOT_CONFIG}" != "x" ]
        then
            for config in ${UBOOT_MACHINE}; do
                i=`expr $i + 1`;
                for type in ${UBOOT_CONFIG}; do
                    j=`expr $j + 1`;
                    if [ $j -eq $i ]
                    then
                        install ${B}/${config}/${UBOOT_ELF} ${DEPLOYDIR}/u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX}
                        ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX} ${DEPLOYDIR}/${UBOOT_ELF_BINARY}-${type}
                        ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX} ${DEPLOYDIR}/${UBOOT_ELF_BINARY}
                        ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX} ${DEPLOYDIR}/${UBOOT_ELF_SYMLINK}-${type}
                        ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX} ${DEPLOYDIR}/${UBOOT_ELF_SYMLINK}
                    fi
                done
                unset j
            done
            unset i
        else
            install ${B}/${UBOOT_ELF} ${DEPLOYDIR}/${UBOOT_ELF_IMAGE}
            ln -sf ${UBOOT_ELF_IMAGE} ${DEPLOYDIR}/${UBOOT_ELF_BINARY}
            ln -sf ${UBOOT_ELF_IMAGE} ${DEPLOYDIR}/${UBOOT_ELF_SYMLINK}
        fi
    fi


     if [ "x${SPL_BINARY}" != "x" ]
     then
         if [ "x${UBOOT_CONFIG}" != "x" ]
         then
             for config in ${UBOOT_MACHINE}; do
                 i=`expr $i + 1`;
                 for type in ${UBOOT_CONFIG}; do
                     j=`expr $j + 1`;
                     if [ $j -eq $i ]
                     then
                         install ${B}/${config}/${SPL_BINARY} ${DEPLOYDIR}/${SPL_IMAGE}-${type}-${PV}-${PR}
                         rm -f ${DEPLOYDIR}/${SPL_BINARY} ${DEPLOYDIR}/${SPL_SYMLINK}-${type}
                         ln -sf ${SPL_IMAGE}-${type}-${PV}-${PR} ${DEPLOYDIR}/${SPL_BINARY}-${type}
                         ln -sf ${SPL_IMAGE}-${type}-${PV}-${PR} ${DEPLOYDIR}/${SPL_BINARY}
                         ln -sf ${SPL_IMAGE}-${type}-${PV}-${PR} ${DEPLOYDIR}/${SPL_SYMLINK}-${type}
                         ln -sf ${SPL_IMAGE}-${type}-${PV}-${PR} ${DEPLOYDIR}/${SPL_SYMLINK}
                     fi
                 done
                 unset  j
             done
             unset  i
         else
             if [ -f ${B}/${SPL_BINARY} ]
             then
                 install ${B}/${SPL_BINARY} ${DEPLOYDIR}/${SPL_IMAGE}
             else
                 install ${B}/spl/${SPL_BINARY} ${DEPLOYDIR}/${SPL_IMAGE}
             fi

             rm -f ${DEPLOYDIR}/${SPL_BINARY} ${DEPLOYDIR}/${SPL_SYMLINK}
             ln -sf ${SPL_IMAGE} ${DEPLOYDIR}/${SPL_BINARY}
             ln -sf ${SPL_IMAGE} ${DEPLOYDIR}/${SPL_SYMLINK}
         fi
     fi


    if [ "x${UBOOT_ENV}" != "x" ]
    then
        install ${WORKDIR}/${UBOOT_ENV_BINARY} ${DEPLOYDIR}/${UBOOT_ENV_IMAGE}
        rm -f ${DEPLOYDIR}/${UBOOT_ENV_BINARY} ${DEPLOYDIR}/${UBOOT_ENV_SYMLINK}
        ln -sf ${UBOOT_ENV_IMAGE} ${DEPLOYDIR}/${UBOOT_ENV_BINARY}
        ln -sf ${UBOOT_ENV_IMAGE} ${DEPLOYDIR}/${UBOOT_ENV_SYMLINK}
    fi
}

do_deploy_append () {
	# deploy SPL images for USB, SD and SPI boot sources.
	${B}/tools/mkimage -n rk3288 -T rkimage -d ${B}/spl/${SPL_BINARY} ${DEPLOYDIR}/${SPL_BINARY}.rkimage
	${B}/tools/mkimage -n rk3288 -T rksd -d ${B}/spl/${SPL_BINARY} ${DEPLOYDIR}/${SPL_BINARY}.rksd
	${B}/tools/mkimage -n rk3288 -T rkspi -d ${B}/spl/${SPL_BINARY} ${DEPLOYDIR}/${SPL_BINARY}.rkspi
}

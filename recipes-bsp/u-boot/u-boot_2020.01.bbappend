FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}/:"

DEPENDS += "u-boot-mkimage-native"

SRC_URI_append = " \
  file://0001-FORUPSTREAM-phycore-rk3288-Remove-PMIC-related-stuff.patch \
  file://boot.cmd \
"

do_deploy_append () {
        # deploy SPL images for USB, SD and SPI boot sources.
        ${B}/tools/mkimage -n rk3288 -T rkimage -d ${B}/${SPL_BINARY} ${DEPLOYDIR}/${SPL_BINARYNAME}.rkimage
        ${B}/tools/mkimage -n rk3288 -T rksd -d ${B}/${SPL_BINARY} ${DEPLOYDIR}/${SPL_BINARYNAME}.rksd
        ${B}/tools/mkimage -n rk3288 -T rkspi -d ${B}/${SPL_BINARY} ${DEPLOYDIR}/${SPL_BINARYNAME}.rkspi
        cat ${B}/u-boot-dtb.bin >> ${DEPLOYDIR}/${SPL_BINARYNAME}.rkimage
        cat ${B}/u-boot-dtb.bin >> ${DEPLOYDIR}/${SPL_BINARYNAME}.rksd
        cat ${B}/u-boot-dtb.bin >> ${DEPLOYDIR}/${SPL_BINARYNAME}.rkspi

	mkimage -C none -A arm -T script -d ${WORKDIR}/boot.cmd ${DEPLOYDIR}/boot.scr
}

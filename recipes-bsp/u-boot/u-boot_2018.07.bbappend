do_deploy_append () {
        # deploy SPL images for USB, SD and SPI boot sources.
        ${B}/tools/mkimage -n rk3288 -T rkimage -d ${B}/${SPL_BINARY} ${DEPLOYDIR}/${SPL_BINARYNAME}.rkimage
        ${B}/tools/mkimage -n rk3288 -T rksd -d ${B}/${SPL_BINARY} ${DEPLOYDIR}/${SPL_BINARYNAME}.rksd
        ${B}/tools/mkimage -n rk3288 -T rkspi -d ${B}/${SPL_BINARY} ${DEPLOYDIR}/${SPL_BINARYNAME}.rkspi
        ${B}/tools/mkimage -n rk3288 -T rksd -d ${B}/${SPL_BINARY} ${DEPLOYDIR}/${SPL_BINARYNAME}.rksd
}

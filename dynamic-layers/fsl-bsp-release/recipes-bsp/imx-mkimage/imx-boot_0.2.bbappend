CFLAGS = "-O2 -Wall -std=c99 -static -I ${STAGING_INCDIR_NATIVE} -L ${STAGING_LIBDIR_NATIVE}"

#HACK that imx-mkimage runs
#The soc.mak file has hardcoded u-boot devicetree names. For the Alpha release we
#use a HACK variant and renamed the Phytec devictree equal to NXP devicetree.

do_compile_prepend () {
	if [ "${SOC_TARGET}" = "iMX8M" ]; then
		cp ${DEPLOY_DIR_IMAGE}/${BOOT_TOOLS}/${UBOOT_DTB_NAME} ${S}/${SOC_TARGET}/fsl-imx8mq-evk.dtb
	fi
}

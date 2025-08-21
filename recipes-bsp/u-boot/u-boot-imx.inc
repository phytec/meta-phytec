# This include file is designed for U-Boot binman use. It replaces the imx-boot-container
# functionality.
# Binman device tree node defines the boot container and how binaries are packed within it.
# Thus, the main purpose of this include file is to provide all the needed binaries for an U-Boot
# binman build.

IMX_BOOT_CONTAINER_FIRMWARE_SOC ?= ""
IMX_BOOT_CONTAINER_FIRMWARE_SOC:mx95-generic-bsp ?= "oei-m33-ddr.bin oei-m33-tcm.bin m33_image.bin \
                                                     mx95a0-ahab-container.img"
IMX_BOOT_CONTAINER_FIRMWARE ?= " \
    ${IMX_BOOT_CONTAINER_FIRMWARE_SOC} \
    ${DDR_FIRMWARE_NAME} \
"

# This package aggregates output deployed by other packages, so set the
# appropriate dependencies for populate binaries task
do_configure[depends] += " \
    ${@' '.join('%s:do_deploy' % r for r in '${IMX_EXTRA_FIRMWARE}'.split() )} \
    ${IMX_DEFAULT_ATF_PROVIDER}:do_deploy \
    ${@bb.utils.contains('MACHINE_FEATURES', 'optee', 'optee-os:do_deploy', '', d)} \
"

ATF_MACHINE_NAME = "bl31-${ATF_PLATFORM}.bin"

# Define an additional task that collects binary output from dependent packages
# and deploys them into the U-Boot build directory
do_configure:append() {
    if [ -n "${UBOOT_CONFIG}" ]; then
        for config in ${UBOOT_MACHINE}; do
            i=$(expr $i + 1);
            for type in ${UBOOT_CONFIG}; do
                j=$(expr $j + 1);
                if [ $j -eq $i ]; then
                    for firmware in ${IMX_BOOT_CONTAINER_FIRMWARE}; do
                        bbnote "Copy firmware: ${firmware} from ${DEPLOY_DIR_IMAGE} -> ${B}/${config}/"
                        cp ${DEPLOY_DIR_IMAGE}/${firmware} ${B}/${config}/
                    done
                    if [ -n "${ATF_MACHINE_NAME}" ]; then
                        cp ${DEPLOY_DIR_IMAGE}/${ATF_MACHINE_NAME} ${B}/${config}/bl31.bin
                    else
                        bberror "ATF binary is undefined, result binary would be unusable!"
                    fi
                    if [ "${@bb.utils.contains('MACHINE_FEATURES', 'optee', '1' , '0' , d)}" = "1" ] ; then
                        cp ${DEPLOY_DIR_IMAGE}/optee/tee-raw.bin ${B}/${config}/tee.bin
                    fi
                fi
            done
            unset  j
        done
        unset  i
    fi
}

# Needed for interoperability between NXP BSP and Mainline BSP. wks files hardcode the name
do_deploy:append() {
    ln -sf ${UBOOT_BINARY} imx-boot
}

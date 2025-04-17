FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append = " \
    file://boot.cmd \
    file://boot.its \
    "
DEPENDS += "u-boot-mkimage-native"

# define FIT variables for packaging boot script into FIT image
# TODO send upstream
UBOOT_ENV_FIT_SRC = "boot.its"
UBOOT_ENV_FIT_BINARY = "boot.scr.uimg"

# Use FIT image boot script
do_compile:append() {
    if [ -n "${UBOOT_ENV_FIT_SRC}" ]
    then
        ${UBOOT_MKIMAGE} -C none -A ${UBOOT_ARCH} -f ${UNPACKDIR}/${UBOOT_ENV_FIT_SRC} ${UNPACKDIR}/${UBOOT_ENV_FIT_BINARY}
    fi
}

do_deploy:append() {
    install -m 644 ${UNPACKDIR}/${UBOOT_ENV_FIT_BINARY} ${DEPLOYDIR}/${UBOOT_ENV_FIT_BINARY}
}

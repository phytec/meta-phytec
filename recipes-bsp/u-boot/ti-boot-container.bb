SUMMARY = "Combined image of tiboot3.bin, tispl.bin and u-boot.img"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit deploy

do_compile[depends] += "virtual/bootloader:do_deploy"
EXTRA_DO_COMPILE_MCDEPENDS:k3 = "mc::k3r5:virtual/bootloader:do_deploy"
EXTRA_DO_COMPILE_MCDEPENDS:am62lxx = ""
do_compile[mcdepends] += "${EXTRA_DO_COMPILE_MCDEPENDS}"

do_compile() {
    dd if=${DEPLOY_DIR_IMAGE}/tiboot3.bin of=${B}/ti-boot-container.img conv=fsync
    dd if=${DEPLOY_DIR_IMAGE}/tispl.bin of=${B}/ti-boot-container.img seek=1024 conv=fsync
    dd if=${DEPLOY_DIR_IMAGE}/u-boot.img of=${B}/ti-boot-container.img seek=5120 conv=fsync
}

do_deploy() {
    install -m 644 ${B}/ti-boot-container.img ${DEPLOYDIR}
}
addtask deploy after do_compile

COMPATIBLE_MACHINE = "(k3)"

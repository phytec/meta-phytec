DESCRIPTION =  "Kernel drivers for the PowerVR SGX chipset found in the TI SoCs"
HOMEPAGE = "https://git.ti.com/graphics/omap5-sgx-ddk-linux"
LICENSE = "MIT | GPL-2.0-only"
LIC_FILES_CHKSUM = "file://eurasia_km/README;beginline=13;endline=22;md5=74506d9b8e5edbce66c2747c50fcef12"

inherit module features_check

REQUIRED_MACHINE_FEATURES = "gpu"

COMPATIBLE_MACHINE = "ti33x"

PR = "r0"

PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS = "virtual/kernel"

PROVIDES = "virtual/gpudriver"

BRANCH = "ti-img-sgx/${PV}/k5.10"

SRC_URI = "git://git.ti.com/git/graphics/omap5-sgx-ddk-linux.git;protocol=https;branch=${BRANCH}"
SRC_URI += " \
        file://0001-Makefiles-Fix-arch-assignement.patch \
        file://0002-pvr_linux_fence.c-Rename-functions.patch \
        file://0003-module-Remove-MODULE_SUPPORTED_DEVICE.patch \
"
S = "${WORKDIR}/git"

SRCREV = "eda7780bfd5277e16913c9bc0b0e6892b4e79063"

TARGET_PRODUCT:ti33x = "ti335x"

EXTRA_OEMAKE += 'KERNELDIR="${STAGING_KERNEL_DIR}" TARGET_PRODUCT=${TARGET_PRODUCT} WINDOW_SYSTEM=nulldrmws'

do_compile:prepend() {
    cd ${S}/eurasia_km/eurasiacon/build/linux2/omap_linux
}

do_install() {
    make -C ${STAGING_KERNEL_DIR} M=${B}/eurasia_km/eurasiacon/binary_omap_linux_nulldrmws_release/target_armhf/kbuild INSTALL_MOD_PATH=${D}${root_prefix} PREFIX=${STAGING_DIR_HOST} modules_install
}

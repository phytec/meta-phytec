DESCRIPTION =  "Kernel drivers for the PowerVR SGX chipset found in the TI SoCs"
HOMEPAGE = "https://git.ti.com/graphics/omap5-sgx-ddk-linux"
LICENSE = "MIT | GPL-2.0-only"
LIC_FILES_CHKSUM = "file://GPL-COPYING;md5=60422928ba677faaa13d6ab5f5baaa1e"

inherit module features_check

REQUIRED_MACHINE_FEATURES = "gpu"

COMPATIBLE_MACHINE = "ti33x"


PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS = "virtual/kernel"

PROVIDES = "virtual/gpudriver"

BRANCH = "${PV}/mesa/k6.1"

SRC_URI = "git://git.ti.com/git/graphics/omap5-sgx-ddk-linux.git;protocol=https;branch=${BRANCH}"
SRC_URI += " \
    file://0001-Makefiles-Fix-arch-assignement.patch \
"

S = "${WORKDIR}/git"

SRCREV = "3005cf8145a6720daa47e4e273f9e421ff77cb58"

TARGET_PRODUCT:ti33x = "ti335x_linux"
PVR_BUILD = "release"
PVR_WS = "lws-generic"

EXTRA_OEMAKE += 'KERNELDIR="${STAGING_KERNEL_DIR}" BUILD=${PVR_BUILD} \
    WINDOW_SYSTEM=${PVR_WS} PVR_BUILD_DIR=${TARGET_PRODUCT}'

# There are useful flags here that are interpreted by the final kbuild pass
# These variables are not necessary when compiling outside of Yocto
export KERNEL_CC
export KERNEL_LD
export KERNEL_AR
export KERNEL_OBJCOPY
export KERNEL_STRIP

do_install() {
    make -C ${STAGING_KERNEL_DIR} M=${B}/eurasiacon/binary_${TARGET_PRODUCT}_${PVR_WS}_${PVR_BUILD}/target_armhf/kbuild INSTALL_MOD_PATH=${D}${root_prefix} PREFIX=${STAGING_DIR_HOST} modules_install
}

RRECOMMENDS:${PN} += "ti-sgx-ddk-um"

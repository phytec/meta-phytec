DESCRIPTION =  "Kernel drivers for the PowerVR SGX chipset found in the TI SoCs"
HOMEPAGE = "https://git.ti.com/graphics/omap5-sgx-ddk-linux"
LICENSE = "MIT | GPLv2"
LIC_FILES_CHKSUM = "file://eurasia_km/README;beginline=13;endline=22;md5=74506d9b8e5edbce66c2747c50fcef12"

inherit module

COMPATIBLE_MACHINE = "ti33x|ti43x|omap-a15"

MACHINE_KERNEL_PR_append = "i"
PR = "${MACHINE_KERNEL_PR}"

INHIBIT_PACKAGE_STRIP = "1"

PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS = "virtual/kernel"

PROVIDES = "omapdrm-pvr"

RPROVIDES_${PN} = "omapdrm-pvr"
RREPLACES_${PN} = "omapdrm-pvr"
RCONFLICTS_${PN} = "omapdrm-pvr"

BRANCH = "ti-img-sgx/${PV}/k4.4"

SRC_URI = "git://git.ti.com/graphics/omap5-sgx-ddk-linux.git;protocol=git;branch=${BRANCH}"
SRC_URI += "file://0001-srvkm-env-linux-Check-whether-Soc-supports-SGX.patch"
S = "${WORKDIR}/git"


SRCREV = "e68719ba7c54602c710b6615cd665f5b7f47f85e"

TARGET_PRODUCT_omap-a15 = "jacinto6evm"
TARGET_PRODUCT_ti33x = "ti335x"
TARGET_PRODUCT_ti43x = "ti437x"

PARALLEL_MAKE = ""

EXTRA_OEMAKE += 'KERNELDIR="${STAGING_KERNEL_DIR}" TARGET_PRODUCT=${TARGET_PRODUCT}'


do_compile_prepend() {
    cd ${S}/eurasia_km/eurasiacon/build/linux2/omap_linux
}

do_install() {
    make -C ${STAGING_KERNEL_DIR} SUBDIRS=${B}/eurasia_km/eurasiacon/binary2_omap_linux_release/target/kbuild INSTALL_MOD_PATH=${D} PREFIX=${STAGING_DIR_HOST} modules_install
}

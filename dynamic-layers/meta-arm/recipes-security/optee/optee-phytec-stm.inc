SRC_URI += " \
    file://0001-dts-stm32-add-stm32mp157c-phycore-som.patch \
    file://0002-dts-stm32-add-phycore-with-nand-and-512MB-RAM.patch \
    file://0003-plat-stm32mp1-add-stm32mp15-phycore-som.patch \
"

PROVIDES += "virtual-optee-os"
RPROVIDES:${PN} += "virtual-systemd-bootconf virtual-optee-os"

# Configure build dir for externalsrc class usage through devtool
EXTERNALSRC_BUILD:pn-${PN} = "${WORKDIR}/build"

DEPENDS += "dtc-native python3-pycryptodomex-native libgcc"

EXTRA_OEMAKE += "LDFLAGS= "

EXTRA_OEMAKE:append:stm32mp1common = " ${@bb.utils.contains('ST_OPTEE_NO_SHAREDMEMORY', '1', 'CFG_CORE_RESERVED_SHM=n', '', d)}"

OPTEE_ARCH:armv7a = "arm32"
OPTEE_ARCH:armv7ve = "arm32"

do_configure:prepend(){
    chmod 755 ${S}/scripts/bin_to_c.py
}

EXTRA_OEMAKE += "CFG_EMBED_DTB_SOURCE_FILE=${OPTEE_CONF}.dts"
EXTRA_OEMAKE += "$(echo CFG ${STM32MP_SOC_NAME} | awk '{print toupper($1"_"$2)}')=y"
EXTRA_OEMAKE += "OPENSSL_MODULES=${STAGING_LIBDIR_NATIVE}/ossl-modules/"

do_compile:prepend() {
    export CFLAGS="${CFLAGS} --sysroot=${STAGING_DIR_HOST}"

    unset -v CPPFLAGS LDFLAGS LDADD
}

FILES:${PN}-dev = "/usr/include/optee"

# The package is empty but must be generated to avoid apt-get installation issue
ALLOW_EMPTY:${PN} = "1"


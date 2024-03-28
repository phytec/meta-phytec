FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
    file://${LINUX_VERSION}/${LINUX_VERSION}.${LINUX_SUBVERSION}/0001-ARM-5.15.67-stm32mp1-phy1-DT-OVERLAY.patch \
    file://${LINUX_VERSION}/${LINUX_VERSION}.${LINUX_SUBVERSION}/0002-ARM-5.15.67-stm32mp1-phy1-DEVICETREE.patch \
    file://${LINUX_VERSION}/${LINUX_VERSION}.${LINUX_SUBVERSION}/0003-ARM-5.15.67-stm32mp1-phy1-DRM.patch \
    file://${LINUX_VERSION}/${LINUX_VERSION}.${LINUX_SUBVERSION}/0004-ARM-5.15.67-stm32mp1-phy1-SOUND.patch \
    file://${LINUX_VERSION}/${LINUX_VERSION}.${LINUX_SUBVERSION}/0005-ARM-5.15.67-stm32mp1-phy1-RTC.patch \
    file://${LINUX_VERSION}/${LINUX_VERSION}.${LINUX_SUBVERSION}/0006-ARM-5.15.67-stm32mp1-phy1-MEDIA.patch \
    file://${LINUX_VERSION}/${LINUX_VERSION}.${LINUX_SUBVERSION}/0007-ARM-5.15.67-stm32mp1-phy1-SPI.patch \
    file://${LINUX_VERSION}/${LINUX_VERSION}.${LINUX_SUBVERSION}/0001-ARM-5.15.67-stm32mp1-phy2-DT-OVERLAY.patch \
    file://${LINUX_VERSION}/${LINUX_VERSION}.${LINUX_SUBVERSION}/0002-ARM-5.15.67-stm32mp1-phy2-DEVICETREE.patch \
    file://${LINUX_VERSION}/${LINUX_VERSION}.${LINUX_SUBVERSION}/0003-ARM-5.15.67-stm32mp1-phy2-MEDIA.patch \
    file://${LINUX_VERSION}/${LINUX_VERSION}.${LINUX_SUBVERSION}/0004-ARM-5.15.67-stm32mp1-phy2-NET.patch \
"

# -------------------------------------------------------------
# Defconfig
#
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/${LINUX_VERSION}/fragment-06-rtc.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/${LINUX_VERSION}/fragment-07-eeprom.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/${LINUX_VERSION}/fragment-08-spi-nor.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/${LINUX_VERSION}/fragment-09-audio.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/${LINUX_VERSION}/fragment-10-peb-hdmi.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/${LINUX_VERSION}/fragment-11-wifi-r8712u-support.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/${LINUX_VERSION}/fragment-12-add-dp83867-phy-support.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/${LINUX_VERSION}/fragment-13-add-pca953x-led-support.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/${LINUX_VERSION}/fragment-14-RPI-screen.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/${LINUX_VERSION}/fragment-15-camera-mt9v032.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/${LINUX_VERSION}/fragment-16-camera-ar0144.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/${LINUX_VERSION}/fragment-17-phy-dp83826.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/${LINUX_VERSION}/fragment-18-resistive-touch.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/${LINUX_VERSION}/fragment-19-capacitive-touch.config"

SRC_URI += "file://${LINUX_VERSION}/fragment-06-rtc.config;subdir=fragments"
SRC_URI += "file://${LINUX_VERSION}/fragment-07-eeprom.config;subdir=fragments"
SRC_URI += "file://${LINUX_VERSION}/fragment-08-spi-nor.config;subdir=fragments"
SRC_URI += "file://${LINUX_VERSION}/fragment-09-audio.config;subdir=fragments"
SRC_URI += "file://${LINUX_VERSION}/fragment-10-peb-hdmi.config;subdir=fragments"
SRC_URI += "file://${LINUX_VERSION}/fragment-11-wifi-r8712u-support.config;subdir=fragments"
SRC_URI += "file://${LINUX_VERSION}/fragment-12-add-dp83867-phy-support.config;subdir=fragments"
SRC_URI += "file://${LINUX_VERSION}/fragment-13-add-pca953x-led-support.config;subdir=fragments"
SRC_URI += "file://${LINUX_VERSION}/fragment-14-RPI-screen.config;subdir=fragments"
SRC_URI += "file://${LINUX_VERSION}/fragment-15-camera-mt9v032.config;subdir=fragments"
SRC_URI += "file://${LINUX_VERSION}/fragment-16-camera-ar0144.config;subdir=fragments"
SRC_URI += "file://${LINUX_VERSION}/fragment-17-phy-dp83826.config;subdir=fragments"
SRC_URI += "file://${LINUX_VERSION}/fragment-18-resistive-touch.config;subdir=fragments"
SRC_URI += "file://${LINUX_VERSION}/fragment-19-capacitive-touch.config;subdir=fragments"

# ---------------------------------
# Configure devupstream class usage
# ---------------------------------
BBCLASSEXTEND = "devupstream:target"

SRC_URI:class-devupstream = "git://git.phytec.de/linux-stm32mp;protocol=git;branch=v${LINUX_VERSION}-phy"
SRCREV:class-devupstream = "6e5a83d9fa8d0cdd6b97730d51105b386a1d6f7f"

# -----------------------------------------------------------------------------------
# Configure default preference to manage dynamic selection between tarball and github
# -----------------------------------------------------------------------------------
STM32MP_SOURCE_SELECTION ?= "tarball"

DEFAULT_PREFERENCE = "${@bb.utils.contains('STM32MP_SOURCE_SELECTION', 'git.phytec', '-1', '1', d)}"


# Don't forget to add/del for devupstream
SRC_URI:class-devupstream += "file://${LINUX_VERSION}/fragment-03-systemd.config;subdir=fragments"
SRC_URI:class-devupstream += "file://${LINUX_VERSION}/fragment-04-modules.config;subdir=fragments"
SRC_URI:class-devupstream += "file://${LINUX_VERSION}/fragment-05-signature.config;subdir=fragments"

SRC_URI:class-devupstream += "file://${LINUX_VERSION}/fragment-06-rtc.config;subdir=fragments"
SRC_URI:class-devupstream += "file://${LINUX_VERSION}/fragment-07-eeprom.config;subdir=fragments"
SRC_URI:class-devupstream += "file://${LINUX_VERSION}/fragment-08-spi-nor.config;subdir=fragments"
SRC_URI:class-devupstream += "file://${LINUX_VERSION}/fragment-09-audio.config;subdir=fragments"
SRC_URI:class-devupstream += "file://${LINUX_VERSION}/fragment-10-peb-hdmi.config;subdir=fragments"
SRC_URI:class-devupstream += "file://${LINUX_VERSION}/fragment-11-wifi-r8712u-support.config;subdir=fragments"
SRC_URI:class-devupstream += "file://${LINUX_VERSION}/fragment-12-add-dp83867-phy-support.config;subdir=fragments"
SRC_URI:class-devupstream += "file://${LINUX_VERSION}/fragment-13-add-pca953x-led-support.config;subdir=fragments"
SRC_URI:class-devupstream += "file://${LINUX_VERSION}/fragment-14-RPI-screen.config;subdir=fragments"
SRC_URI:class-devupstream += "file://${LINUX_VERSION}/fragment-15-camera-mt9v032.config;subdir=fragments"
SRC_URI:class-devupstream += "file://${LINUX_VERSION}/fragment-16-camera-ar0144.config;subdir=fragments"
SRC_URI:class-devupstream += "file://${LINUX_VERSION}/fragment-17-phy-dp83826.config;subdir=fragments"
SRC_URI:class-devupstream += "file://${LINUX_VERSION}/fragment-18-resistive-touch.config;subdir=fragments"
SRC_URI:class-devupstream += "file://${LINUX_VERSION}/fragment-19-capacitive-touch.config;subdir=fragments"

# ------------------------------------------------------------------------
# Build dtb with symbols to allow bootloader to apply device tree overlays
# ------------------------------------------------------------------------
KERNEL_EXTRA_ARGS += "${@bb.utils.contains('MACHINE_FEATURES', 'phy-expansions', "DTC_FLAGS='-@'", '', d)}"

# -------------------------------------------------------------------------
# Create a symbolic link of the main device tree to get a generic file name
# -------------------------------------------------------------------------
FIRST_DTS = "${KERNEL_DT}.dtb"
DTS_FILE = "oftree"

do_deploy:append() {
    ln -sf ${FIRST_DTS} ${DEPLOYDIR}/${DTS_FILE}
}

do_install:append() {
    ln -sf ${FIRST_DTS} ${KERNEL_OUTPUT_DIR}/dts/${DTS_FILE}
    install -m 0644 ${KERNEL_OUTPUT_DIR}/dts/${DTS_FILE} ${D}/${KERNEL_IMAGEDEST}
}

FILES:${KERNEL_PACKAGE_NAME}-imagebootfs += "boot/${DTS_FILE}"

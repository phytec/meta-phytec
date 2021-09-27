FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
    file://${LINUX_VERSION}/${LINUX_VERSION}.${LINUX_SUBVERSION}/0001-ARM-5.10.10-stm32mp1-phy1-DT-OVERLAY.patch \
    file://${LINUX_VERSION}/${LINUX_VERSION}.${LINUX_SUBVERSION}/0002-ARM-5.10.10-stm32mp1-phy1-DEVICETREE.patch \
    file://${LINUX_VERSION}/${LINUX_VERSION}.${LINUX_SUBVERSION}/0003-ARM-5.10.10-stm32mp1-phy1-DRM.patch \
    file://${LINUX_VERSION}/${LINUX_VERSION}.${LINUX_SUBVERSION}/0004-ARM-5.10.10-stm32mp1-phy1-SOUND.patch \
    file://${LINUX_VERSION}/${LINUX_VERSION}.${LINUX_SUBVERSION}/0005-ARM-5.10.10-stm32mp1-phy1-RTC.patch \
    file://${LINUX_VERSION}/${LINUX_VERSION}.${LINUX_SUBVERSION}/0006-ARM-5.10.10-stm32mp1-phy1-MEDIA.patch \
"

# -------------------------------------------------------------
# Defconfig
#
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/5.10/fragment-06-rtc.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/5.10/fragment-07-eeprom.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/5.10/fragment-08-spi-nor.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/5.10/fragment-09-audio.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/5.10/fragment-10-peb-hdmi.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/5.10/fragment-11-wifi-r8712u-support.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/5.10/fragment-12-add-dp83867-phy-support.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/5.10/fragment-13-add-pca953x-led-support.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/5.10/fragment-14-RPI-screen.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/5.10/fragment-15-camera-mt9v032.config"

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

# ---------------------------------
# Configure devupstream class usage
# ---------------------------------
BBCLASSEXTEND = "devupstream:target"

SRC_URI_class-devupstream = "git://git.phytec.de/linux-stm32mp;protocol=git;branch=v${LINUX_VERSION}-phy"
SRCREV_class-devupstream = "faccf9049ca974aa2489a09ab0959f8a206e5920"

# -----------------------------------------------------------------------------------
# Configure default preference to manage dynamic selection between tarball and github
# -----------------------------------------------------------------------------------
STM32MP_SOURCE_SELECTION ?= "tarball"

DEFAULT_PREFERENCE = "${@bb.utils.contains('STM32MP_SOURCE_SELECTION', 'git.phytec', '-1', '1', d)}"


# Don't forget to add/del for devupstream
SRC_URI_class-devupstream += "file://${LINUX_VERSION}/fragment-03-systemd.config;subdir=fragments"
SRC_URI_class-devupstream += "file://${LINUX_VERSION}/fragment-04-modules.config;subdir=fragments"
SRC_URI_class-devupstream += "file://${LINUX_VERSION}/fragment-05-signature.config;subdir=fragments"

SRC_URI_class-devupstream += "file://${LINUX_VERSION}/fragment-06-rtc.config;subdir=fragments"
SRC_URI_class-devupstream += "file://${LINUX_VERSION}/fragment-07-eeprom.config;subdir=fragments"
SRC_URI_class-devupstream += "file://${LINUX_VERSION}/fragment-08-spi-nor.config;subdir=fragments"
SRC_URI_class-devupstream += "file://${LINUX_VERSION}/fragment-09-audio.config;subdir=fragments"
SRC_URI_class-devupstream += "file://${LINUX_VERSION}/fragment-10-peb-hdmi.config;subdir=fragments"
SRC_URI_class-devupstream += "file://${LINUX_VERSION}/fragment-11-wifi-r8712u-support.config;subdir=fragments"
SRC_URI_class-devupstream += "file://${LINUX_VERSION}/fragment-12-add-dp83867-phy-support.config;subdir=fragments"
SRC_URI_class-devupstream += "file://${LINUX_VERSION}/fragment-13-add-pca953x-led-support.config;subdir=fragments"
SRC_URI_class-devupstream += "file://${LINUX_VERSION}/fragment-14-RPI-screen.config;subdir=fragments"
SRC_URI_class-devupstream += "file://${LINUX_VERSION}/fragment-15-camera-mt9v032.config;subdir=fragments"

# ------------------------------------------------------------------------
# Build dtb with symbols to allow bootloader to apply device tree overlays
# ------------------------------------------------------------------------
KERNEL_EXTRA_ARGS += "${@bb.utils.contains('MACHINE_FEATURES', 'phy-expansions', "DTC_FLAGS='-@'", '', d)}"

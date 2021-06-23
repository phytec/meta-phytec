FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
    file://${LINUX_VERSION}/${LINUX_VERSION}.${LINUX_SUBVERSION}/0001-ARM-stm32mp1-phy1-DEVICETREE.patch \
    file://${LINUX_VERSION}/${LINUX_VERSION}.${LINUX_SUBVERSION}/0002-ARM-stm32mp1-phy1-NET.patch \
    file://${LINUX_VERSION}/${LINUX_VERSION}.${LINUX_SUBVERSION}/0003-ARM-stm32mp1-phy1-DRM.patch \
    file://${LINUX_VERSION}/${LINUX_VERSION}.${LINUX_SUBVERSION}/0004-ARM-stm32mp1-phy1-SOUND.patch \
    file://${LINUX_VERSION}/${LINUX_VERSION}.${LINUX_SUBVERSION}/0099-ARM-stm32mp1-r2.1-st-update.patch \
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

SRC_URI += "file://${LINUX_VERSION}/fragment-06-rtc.config;subdir=fragments"
SRC_URI += "file://${LINUX_VERSION}/fragment-07-eeprom.config;subdir=fragments"
SRC_URI += "file://${LINUX_VERSION}/fragment-08-spi-nor.config;subdir=fragments"
SRC_URI += "file://${LINUX_VERSION}/fragment-09-audio.config;subdir=fragments"
SRC_URI += "file://${LINUX_VERSION}/fragment-10-peb-hdmi.config;subdir=fragments"
SRC_URI += "file://${LINUX_VERSION}/fragment-11-wifi-r8712u-support.config;subdir=fragments"
SRC_URI += "file://${LINUX_VERSION}/fragment-12-add-dp83867-phy-support.config;subdir=fragments"
SRC_URI += "file://${LINUX_VERSION}/fragment-13-add-pca953x-led-support.config;subdir=fragments"
SRC_URI += "file://${LINUX_VERSION}/fragment-14-RPI-screen.config;subdir=fragments"

# ---------------------------------
# Configure devupstream class usage
# ---------------------------------
BBCLASSEXTEND = "devupstream:target"

SRC_URI_class-devupstream = "git://git.phytec.de/linux-stm32mp;protocol=git;branch=v${LINUX_VERSION}-phy"
SRCREV_class-devupstream = "7d824a481049283b319d1d510980cc532c8b1ad0"

# ---------------------------------
# Configure default preference to manage dynamic selection between tarball and github
# ---------------------------------
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

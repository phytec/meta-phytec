FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/5.4/fragment-06-rtc.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/5.4/fragment-07-eeprom.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/5.4/fragment-08-spi-nor.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/5.4/fragment-09-audio.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/5.4/fragment-10-peb-hdmi.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/5.4/fragment-11-wifi-r8712u-support.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/5.4/fragment-12-add-dp83867-phy-support.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/5.4/fragment-13-add-pca953x-led-support.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/5.4/fragment-14-RPI-screen.config"

# ---------------------------------
# Configure devupstream class usage
# ---------------------------------
BBCLASSEXTEND = "devupstream:target"

SRC_URI_class-devupstream = "git://git.phytec.de/linux-stm32mp;protocol=git;branch=v${LINUX_VERSION}-phy"
SRCREV_class-devupstream = "679cb7ea1498c569569759dedddbcabf30ab345e"

# ---------------------------------
# Configure default preference to manage dynamic selection between tarball and github
# ---------------------------------
STM32MP_SOURCE_SELECTION = "git.phytec"

DEFAULT_PREFERENCE = "${@bb.utils.contains('STM32MP_SOURCE_SELECTION', 'git.phytec', '-1', '1', d)}"


# Don't forget to add/del for devupstream
SRC_URI_class-devupstream += "file://${LINUX_VERSION}/fragment-03-systemd.config;subdir=fragments"
SRC_URI_class-devupstream += "file://${LINUX_VERSION}/fragment-04-optee.config;subdir=fragments"
SRC_URI_class-devupstream += "file://${LINUX_VERSION}/fragment-05-modules.config;subdir=fragments"
SRC_URI_class-devupstream += "file://${LINUX_VERSION}/fragment-06-signature.config;subdir=fragments"

SRC_URI_class-devupstream += "file://5.4/fragment-06-rtc.config;subdir=fragments"
SRC_URI_class-devupstream += "file://5.4/fragment-07-eeprom.config;subdir=fragments"
SRC_URI_class-devupstream += "file://5.4/fragment-08-spi-nor.config;subdir=fragments"
SRC_URI_class-devupstream += "file://5.4/fragment-09-audio.config;subdir=fragments"
SRC_URI_class-devupstream += "file://5.4/fragment-10-peb-hdmi.config;subdir=fragments"
SRC_URI_class-devupstream += "file://5.4/fragment-11-wifi-r8712u-support.config;subdir=fragments"
SRC_URI_class-devupstream += "file://5.4/fragment-12-add-dp83867-phy-support.config;subdir=fragments"
SRC_URI_class-devupstream += "file://5.4/fragment-13-add-pca953x-led-support.config;subdir=fragments"
SRC_URI_class-devupstream += "file://5.4/fragment-14-RPI-screen.config;subdir=fragments"

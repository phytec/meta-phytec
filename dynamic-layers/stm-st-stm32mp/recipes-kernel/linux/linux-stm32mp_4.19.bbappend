FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
    file://${LINUX_VERSION}/4.19.9/0065-ARM-stm32mp1-r0-rc1-add-phycore-stm32mp1xx-alpha1-machine.patch \
"

KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-06-rtc.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-07-eeprom.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-08-spi-nor.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-09-audio.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-10-peb-hdmi.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-11-wifi-r8712u-support.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-12-add-dp83867-phy-support.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-13-add-pca953x-led-support.config"

SRC_URI += "file://4.19/fragment-06-rtc.config;subdir=fragments"
SRC_URI += "file://4.19/fragment-07-eeprom.config;subdir=fragments"
SRC_URI += "file://4.19/fragment-08-spi-nor.config;subdir=fragments"
SRC_URI += "file://4.19/fragment-09-audio.config;subdir=fragments"
SRC_URI += "file://4.19/fragment-10-peb-hdmi.config;subdir=fragments"
SRC_URI += "file://4.19/fragment-11-wifi-r8712u-support.config;subdir=fragments"
SRC_URI += "file://4.19/fragment-12-add-dp83867-phy-support.config;subdir=fragments"
SRC_URI += "file://4.19/fragment-13-add-pca953x-led-support.config;subdir=fragments"



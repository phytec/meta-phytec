FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://${LINUX_VERSION}/4.19.94/0032-ARM-stm32mp1-r3-add-phycore-stm32mp1xx-machines-support.patch \
    file://${LINUX_VERSION}/4.19.94/0032-ARM-stm32mp1-r3-mmc-alias-support.patch \
    file://${LINUX_VERSION}/4.19.94/0033-ARM-stm32mp1-r3-bypass-part_number.patch \
    file://${LINUX_VERSION}/4.19.94/0034-ARM-stm32mp1-r3-fix-part_number_opt.patch \
    file://${LINUX_VERSION}/4.19.94/0035-ARM-stm32mp1-r3-debug.patch \
"

SRC_URI += " \
    file://${LINUX_VERSION}/4.19.94/patches/0001-drm-stm-dsi-higher-pll-out-only-in-video-burst-mode.patch \
    file://${LINUX_VERSION}/4.19.94/patches/0002-drm-bridge-synopsys-dsi-allow-sending-longer-LP-comm.patch \
    file://${LINUX_VERSION}/4.19.94/patches/0003-drm-bridge-synopsys-dsi-allows-LP-commands-in-video-.patch \
    file://${LINUX_VERSION}/4.19.94/patches/0004-drm-bridge-synopsys-dsi-add-support-for-non-continuo.patch \
    file://${LINUX_VERSION}/4.19.94/patches/0005-drm-bridge-synopsys-dsi-check-lower-limit-for-escape.patch \
    file://${LINUX_VERSION}/4.19.94/patches/0006-drm-panel-rpi-touchscreen-retry-i2c-read-during-prob.patch \
    file://${LINUX_VERSION}/4.19.94/patches/0007-TMP-drm-panel-rpi-touchscreen-fix-power-on.patch \
    file://${LINUX_VERSION}/4.19.94/patches/0008-drm-panel-rpi-touchscreen-50Hz-vrefresh-freq.patch \
    file://${LINUX_VERSION}/4.19.94/patches/0009-drm-panel-otm8009a-remove-hack-to-force-commands-in-.patch \
    file://${LINUX_VERSION}/4.19.94/patches/0010-drm-panel-otm8009a-allow-using-non-continuous-dsi-cl.patch \
    file://${LINUX_VERSION}/4.19.94/patches/0011-drm-panel-rm68200-allow-using-non-continuous-dsi-clo.patch \
    file://${LINUX_VERSION}/4.19.94/patches/0012-add-support-for-st1633.diff \
"

SRC_URI += " \
	file://${LINUX_VERSION}/4.19.94/dts/phycore-stm32mp157cac-som.dtsi \
        file://${LINUX_VERSION}/4.19.94/dts/phycore-stm32mp157-pinctrl.dtsi \
        file://${LINUX_VERSION}/4.19.94/dts/phycore-stm32mp157cac-pinctrl.dtsi \
	\
	file://${LINUX_VERSION}/4.19.94/dts/phycore-stm32mp1-1.dts \
	file://${LINUX_VERSION}/4.19.94/dts/phycore-stm32mp1-1-m4-examples.dts \
	file://${LINUX_VERSION}/4.19.94/dts/phycore-stm32mp1-1-a7-examples.dts \
	file://${LINUX_VERSION}/4.19.94/dts/phycore-stm32mp1-2.dts \
	\
	file://${LINUX_VERSION}/4.19.94/dts/phyboard-stm32mp1-alpha.dtsi \
	file://${LINUX_VERSION}/4.19.94/dts/phyboard-stm32mp1-alpha-pinctrl.dtsi \
        file://${LINUX_VERSION}/4.19.94/dts/phyboard-stm32mp1-alpha-pi-hat-extension.dtsi \
	\
	file://${LINUX_VERSION}/4.19.94/dts/phyboard-stm32mp1-dsi-lcd-mb1407.dtsi \
        file://${LINUX_VERSION}/4.19.94/dts/phyboard-stm32mp1-dsi-rpi-official-display.dtsi \
	file://${LINUX_VERSION}/4.19.94/dts/phyboard-stm32mp1-peb-av02-lcd.dtsi \
	file://${LINUX_VERSION}/4.19.94/dts/phyboard-stm32mp1-peb-av02-lcd-res.dtsi \
	file://${LINUX_VERSION}/4.19.94/dts/phyboard-stm32mp1-uno-r3-extension.dtsi \
	file://${LINUX_VERSION}/4.19.94/dts/phyboard-stm32mp1-motor-control.dtsi \
	file://${LINUX_VERSION}/4.19.94/dts/phyboard-stm32mp1-motor-control-m4.dtsi \
	file://${LINUX_VERSION}/4.19.94/dts/phyboard-stm32mp1-peb-av01-hdmi.dtsi \
        file://${LINUX_VERSION}/4.19.94/dts/phyboard-stm32mp1-pi-hat-extension.dtsi \
	file://${LINUX_VERSION}/4.19.94/dts/phyboard-stm32mp1-pi-hat-redbear.dtsi \
        \
        file://${LINUX_VERSION}/4.19.94/dts/phycore-stm32mp1-3.dts \
        file://${LINUX_VERSION}/4.19.94/dts/phycore-stm32mp1-3-m4-examples.dts \
        file://${LINUX_VERSION}/4.19.94/dts/phycore-stm32mp1-3-a7-examples.dts \
	\
	file://${LINUX_VERSION}/4.19.94/dts/phyboard-stm32mp1.dtsi \
	file://${LINUX_VERSION}/4.19.94/dts/phyboard-stm32mp1-pinctrl.dtsi \
	\
        file://${LINUX_VERSION}/4.19.94/dts/phycore-stm32mp1-4.dts \
        file://${LINUX_VERSION}/4.19.94/dts/phycore-stm32mp1-4-m4-examples.dts \
        file://${LINUX_VERSION}/4.19.94/dts/phycore-stm32mp1-4-a7-examples.dts \
	\
        file://${LINUX_VERSION}/4.19.94/dts/phycore-stm32mp1-5.dts \
        file://${LINUX_VERSION}/4.19.94/dts/phycore-stm32mp1-5-m4-examples.dts \
        file://${LINUX_VERSION}/4.19.94/dts/phycore-stm32mp1-5-a7-examples.dts \
        \
        file://${LINUX_VERSION}/4.19.94/dts/phycore-stm32mp1-6.dts \
        file://${LINUX_VERSION}/4.19.94/dts/phycore-stm32mp1-6-m4-examples.dts \
        file://${LINUX_VERSION}/4.19.94/dts/phycore-stm32mp1-6-a7-examples.dts \
        \
        file://${LINUX_VERSION}/4.19.94/dts/phycore-stm32mp1-7.dts \
        file://${LINUX_VERSION}/4.19.94/dts/phycore-stm32mp1-7-m4-examples.dts \
        file://${LINUX_VERSION}/4.19.94/dts/phycore-stm32mp1-7-a7-examples.dts \
"

KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-06-rtc.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-07-eeprom.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-08-spi-nor.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-09-audio.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-10-peb-hdmi.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-11-wifi-r8712u-support.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-12-add-dp83867-phy-support.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-13-add-pca953x-led-support.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-14-RPI-screen.config"

SRC_URI += "file://4.19/fragment-06-rtc.config;subdir=fragments"
SRC_URI += "file://4.19/fragment-07-eeprom.config;subdir=fragments"
SRC_URI += "file://4.19/fragment-08-spi-nor.config;subdir=fragments"
SRC_URI += "file://4.19/fragment-09-audio.config;subdir=fragments"
SRC_URI += "file://4.19/fragment-10-peb-hdmi.config;subdir=fragments"
SRC_URI += "file://4.19/fragment-11-wifi-r8712u-support.config;subdir=fragments"
SRC_URI += "file://4.19/fragment-12-add-dp83867-phy-support.config;subdir=fragments"
SRC_URI += "file://4.19/fragment-13-add-pca953x-led-support.config;subdir=fragments"
SRC_URI += "file://4.19/fragment-14-RPI-screen.config;subdir=fragments"

# Copy .dts and .dtsi from SRC_URI to the kernel boot/dts path
# This should go to poky/meta/classes/kernel-devicetree.bbclass
# returns all the elements from the src uri that are .dts or .dtsi files
def find_dtss(d):
    sources=src_patches(d, True)
    dtss_list=[]
    for s in sources:
        base, ext = os.path.splitext(s)
        if ext in [".dtsi", ".dts"]:
            dtss_list.append(s)

    return dtss_list

python do_dtsfixup () {
    import shutil
    srcdir = d.getVar('STAGING_KERNEL_DIR', True)
    arch = d.getVar('ARCH', True)
    for dts in find_dtss(d):
        cptarget=os.path.join(srcdir, "arch", arch, "boot", "dts",
                             os.path.basename(dts))
        bb.note("copying dts from: %s to: %s" % (dts, cptarget))
        shutil.copyfile(dts, cptarget)
}

addtask dtsfixup after do_patch before do_compile

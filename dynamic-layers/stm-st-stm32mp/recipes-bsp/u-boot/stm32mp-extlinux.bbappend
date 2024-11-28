FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
           file://0001-boot-src-cmd-custom-scan_overlays-cmd.patch \
"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

# For FIT image, the extlinux file prefix is the kernel device tree name and not the bootloader device tree name.
do_compile[postfuncs] += "${@bb.utils.contains('MACHINE_FEATURES', 'fit', 'remame_extlinux_file', '', d)}"

remame_extlinux_file() {
	subdir=$(find ${B}/* -maxdepth 0 -type d)
	if [ "$(echo ${KERNEL_DEVICETREE} | wc -w)" -eq 1 ] ; then
		dvtree=$(echo ${KERNEL_DEVICETREE} | cut -d'.' -f1 | cut -d'/' -f2)
		bbnote "Only one kernel devicetree defined: ${dvtree}"
		if [ -f ${subdir}/${dvtree}_extlinux.conf ]; then
			bbnote "Moving ${dvtree}_extlinux.conf to extlinux.conf file"
			mv -f ${subdir}/${dvtree}_extlinux.conf ${subdir}/extlinux.conf
		fi
	fi
}

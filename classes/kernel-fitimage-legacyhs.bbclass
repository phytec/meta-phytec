inherit ti-secdev kernel-uboot uboot-sign-legacyhs

FITIMAGE_HASH_ALGO ?= "sha1"
FITIMAGE_PACK_TEE ?= "0"
FITIMAGE_DTB_BY_NAME ?= "0"
FITIMAGE_TEE_BY_NAME ?= "0"
FITIMAGE_CONF_BY_NAME ?= "0"

python __anonymous () {
    kerneltypes = d.getVar('KERNEL_IMAGETYPES') or ""
    if 'fitImage' in kerneltypes.split():
        depends = d.getVar("DEPENDS")
        depends = "%s u-boot-mkimage-native dtc-native" % depends
        d.setVar("DEPENDS", depends)

        uarch = d.getVar("UBOOT_ARCH")
        if uarch == "arm64":
            replacementtype = "Image"
        elif uarch == "mips":
            replacementtype = "vmlinuz.bin"
        elif uarch == "x86":
            replacementtype = "bzImage"
        elif uarch == "microblaze":
            replacementtype = "linux.bin"
        else:
            replacementtype = "zImage"

        # Override KERNEL_IMAGETYPE_FOR_MAKE variable, which is internal
        # to kernel.bbclass . We have to override it, since we pack zImage
        # (at least for now) into the fitImage .
        typeformake = d.getVar("KERNEL_IMAGETYPE_FOR_MAKE") or ""
        if 'fitImage' in typeformake.split():
            d.setVar('KERNEL_IMAGETYPE_FOR_MAKE', typeformake.replace('fitImage', replacementtype))

        image = d.getVar('INITRAMFS_IMAGE')
        if image:
            d.appendVarFlag('do_assemble_fitimage_initramfs', 'depends', ' ${INITRAMFS_IMAGE}:do_image_complete')

        # Verified boot will sign the fitImage and append the public key to
        # U-boot dtb. We ensure the U-Boot dtb is deployed before assembling
        # the fitImage:
        if d.getVar('UBOOT_SIGN_ENABLE'):
            uboot_pn = d.getVar('PREFERRED_PROVIDER_u-boot') or 'u-boot'
            d.appendVarFlag('do_assemble_fitimage', 'depends', ' %s:do_deploy' % uboot_pn)

        if d.getVar('FITIMAGE_PACK_TEE') == "1":
            d.appendVarFlag('do_assemble_fitimage', 'depends', ' optee-os:do_deploy')
}

# Options for the device tree compiler passed to mkimage '-D' feature:
UBOOT_MKIMAGE_DTCOPTS ??= ""

fitimage_ti_secure() {
	if test -f "${TI_SECURE_DEV_PKG}/scripts/secure-binary-image.sh"; then
		export TI_SECURE_DEV_PKG=${TI_SECURE_DEV_PKG}
		${TI_SECURE_DEV_PKG}/scripts/secure-binary-image.sh $1 $2
	else
		cp $1 $2
	fi
}

#
# Emit the fitImage ITS header
#
# $1 ... .its filename
fitimage_emit_fit_header() {
	cat << EOF >> ${1}
/dts-v1/;

/ {
        description = "U-Boot fitImage for ${DISTRO_NAME}/${PV}/${MACHINE}";
        #address-cells = <1>;
EOF
}

#
# Emit the fitImage section bits
#
# $1 ... .its filename
# $2 ... Section bit type: imagestart - image section start
#                          confstart  - configuration section start
#                          sectend    - section end
#                          fitend     - fitimage end
#
fitimage_emit_section_maint() {
	case $2 in
	imagestart)
		cat << EOF >> ${1}

        images {
EOF
	;;
	confstart)
		cat << EOF >> ${1}

        configurations {
EOF
	;;
	sectend)
		cat << EOF >> ${1}
        };
EOF
	;;
	fitend)
		cat << EOF >> ${1}
};
EOF
	;;
	esac
}

#
# Emit the fitImage ITS kernel section
#
# $1 ... .its filename
# $2 ... Image counter
# $3 ... Path to kernel image
# $4 ... Compression type
fitimage_emit_section_kernel() {

	kernel_csum=${FITIMAGE_HASH_ALGO}

	ENTRYPOINT="${UBOOT_ENTRYPOINT}"
	if test -n "${UBOOT_ENTRYSYMBOL}"; then
		ENTRYPOINT=`${HOST_PREFIX}nm ${S}/vmlinux | \
			awk '$4=="${UBOOT_ENTRYSYMBOL}" {print $2}'`
	fi

	cat << EOF >> ${1}
                kernel-${2} {
                        description = "Linux kernel";
                        data = /incbin/("${3}");
                        type = "kernel";
                        arch = "${UBOOT_ARCH}";
                        os = "linux";
                        compression = "${4}";
                        load = <${UBOOT_LOADADDRESS}>;
                        entry = <${ENTRYPOINT}>;
EOF
	if test -n "${FITIMAGE_HASH_ALGO}"; then
		cat << EOF >> ${1}
                        hash-1 {
                                algo = "${kernel_csum}";
                        };
EOF
	fi
	cat << EOF >> ${1}
                };
EOF
}

#
# Emit the fitImage ITS DTB section
#
# $1 ... .its filename
# $2 ... Image counter/name
# $3 ... Path to DTB image
# $4 ... Load address
fitimage_emit_section_dtb() {

	dtb_csum=${FITIMAGE_HASH_ALGO}
	dtb_loadline="${4}"

	cat << EOF >> ${1}
                ${2} {
                        description = "Flattened Device Tree blob";
                        data = /incbin/("${3}");
                        type = "flat_dt";
                        arch = "${UBOOT_ARCH}";
                        compression = "none";
                        ${dtb_loadline}
EOF
	if test -n "${FITIMAGE_HASH_ALGO}"; then
		cat << EOF >> ${1}
                        hash-1 {
                                algo = "${dtb_csum}";
                        };
EOF
	fi
	cat << EOF >> ${1}
                };
EOF
}

#
# Emit the fitImage ITS TEE section
#
# $1 ... .its filename
# $2 ... Image counter/name
# $3 ... Path to TEE image
fitimage_emit_section_tee() {

	tee_csum=${FITIMAGE_HASH_ALGO}

	cat << EOF >> ${1}
                ${2} {
                        description = "OPTEE OS Image";
                        data = /incbin/("${3}");
                        type = "tee";
                        arch = "${UBOOT_ARCH}";
                        compression = "none";
EOF
	if test -n "${FITIMAGE_HASH_ALGO}"; then
		cat << EOF >> ${1}
                        hash-1 {
                                algo = "${tee_csum}";
                        };
EOF
	fi
	cat << EOF >> ${1}
                };
EOF
}

#
# Emit the fitImage ITS setup section
#
# $1 ... .its filename
# $2 ... Image counter
# $3 ... Path to setup image
fitimage_emit_section_setup() {

	setup_csum=${FITIMAGE_HASH_ALGO}

	cat << EOF >> ${1}
                setup-${2} {
                        description = "Linux setup.bin";
                        data = /incbin/("${3}");
                        type = "x86_setup";
                        arch = "${UBOOT_ARCH}";
                        os = "linux";
                        compression = "none";
                        load = <0x00090000>;
                        entry = <0x00090000>;
EOF
	if test -n "${FITIMAGE_HASH_ALGO}"; then
		cat << EOF >> ${1}
                        hash-1 {
                                algo = "${setup_csum}";
                        };
EOF
	fi
	cat << EOF >> ${1}
                };
EOF
}

#
# Emit the fitImage ITS ramdisk section
#
# $1 ... .its filename
# $2 ... Image counter
# $3 ... Path to ramdisk image
fitimage_emit_section_ramdisk() {

	ramdisk_csum=${FITIMAGE_HASH_ALGO}
	ramdisk_ctype="none"

	case $3 in
		*.gz|*.gz.sec)
			ramdisk_ctype="gzip"
			;;
		*.bz2|*.bz2.sec)
			ramdisk_ctype="bzip2"
			;;
		*.lzma|*.lzma.sec)
			ramdisk_ctype="lzma"
			;;
		*.lzo|*.lzo.sec)
			ramdisk_ctype="lzo"
			;;
		*.lz4|*.lz4.sec)
			ramdisk_ctype="lz4"
			;;
	esac

	cat << EOF >> ${1}
                ramdisk-${2} {
                        description = "ramdisk image";
                        data = /incbin/("${3}");
                        type = "ramdisk";
                        arch = "${UBOOT_ARCH}";
                        os = "linux";
                        compression = "${ramdisk_ctype}";
EOF
	if test -n "${UBOOT_RD_LOADADDRESS}"; then
		cat << EOF >> ${1}
                        load = <${UBOOT_RD_LOADADDRESS}>;
EOF
	fi

	if test -n "${UBOOT_RD_ENTRYPOINT}"; then
		cat << EOF >> ${1}
                        entry = <${UBOOT_RD_ENTRYPOINT}>;
EOF
	fi

	if test -n "${FITIMAGE_HASH_ALGO}"; then
		cat << EOF >> ${1}
                        hash-1 {
                                algo = "${ramdisk_csum}";
                        };
EOF
	fi
	cat << EOF >> ${1}
                };
EOF
}

#
# Emit the fitImage ITS configuration section
#
# $1 ... .its filename
# $2 ... Linux kernel ID
# $3 ... DTB image ID/name
# $4 ... ramdisk ID
# $5 ... config ID
# $6 ... tee ID/name
fitimage_emit_section_config() {

	conf_csum=${FITIMAGE_HASH_ALGO}
	if [ -n "${UBOOT_SIGN_ENABLE}" ] ; then
		conf_sign_keyname="${UBOOT_SIGN_KEYNAME}"
	fi

	sep=""
	conf_desc=""
	kernel_line=""
	fdt_line=""
	ramdisk_line=""
	setup_line=""
	default_line=""

	if [ -n "${2}" ]; then
		conf_desc="Linux kernel"
		sep=", "
		kernel_line="kernel = \"kernel-${2}\";"
	fi

	if [ -n "${3}" ]; then
		conf_desc="${conf_desc}${sep}FDT blob"
		sep=", "
	fi

	if [ -n "${4}" ]; then
		conf_desc="${conf_desc}${sep}ramdisk"
		sep=", "
		ramdisk_line="ramdisk = \"ramdisk-${4}\";"
	fi

	if [ -n "${5}" ]; then
		conf_desc="${conf_desc}${sep}setup"
		sep=", "
		setup_line="setup = \"setup-${5}\";"
	fi

	if [ -n "${6}" -a "x${FITIMAGE_PACK_TEE}" = "x1" ]; then
		if [ "x${FITIMAGE_TEE_BY_NAME}" = "x1" ]; then
			loadables_line="loadables = \"${6}.optee\";"
			loadables_pager_line="loadables = \"${6}-pager.optee\";"
		else
			loadables_line="loadables = \"tee-${6}\";"
			nextnum=`expr ${6} + 1`
			loadables_pager_line="loadables = \"tee-${nextnum}\";"
		fi
		final_conf_desc="${conf_desc}${sep}OPTEE OS Image"
	else
		loadables_line=""
		loadables_pager_line=""
		final_conf_desc="${conf_desc}"
	fi

	dtbcount=1
	for DTB in ${KERNEL_DEVICETREE}; do
		DTB=$(basename "${DTB}")
		dtb_ext=${DTB##*.}
		if [ "x${FITIMAGE_CONF_BY_NAME}" = "x1" ] ; then
			conf_name="${DTB}"
		else
			conf_name="conf-${dtbcount}"
		fi

		if [ "x${FITIMAGE_DTB_BY_NAME}" = "x1" ] ; then
			fdt_line="fdt = \"${DTB}\";"
		else
			fdt_line="fdt = \"fdt-${dtbcount}\";"
		fi

		if [ "x${dtbcount}" = "x1" ]; then
			cat << EOF >> ${1}
                default = "${conf_name}";
EOF
		fi

# Generate a single configuration section
			cat << EOF >> ${1}
                ${conf_name} {
                        description = "${final_conf_desc}";
                        ${fdt_line}
EOF
			if [ "${dtb_ext}" != "dtbo" ]; then
			cat << EOF >> ${1}
                        ${kernel_line}
                        ${ramdisk_line}
                        ${setup_line}
                        ${loadables_line}
EOF
			fi
			if test -n "${FITIMAGE_HASH_ALGO}"; then
				cat << EOF >> ${1}
                        hash-1 {
                                algo = "${conf_csum}";
                        };
EOF
			fi

			if [ ! -z "${conf_sign_keyname}" ] ; then

				sign_line="sign-images = \"kernel\""

				if [ -n "${3}" ]; then
					sign_line="${sign_line}, \"fdt\""
				fi

				if [ -n "${4}" ]; then
					sign_line="${sign_line}, \"ramdisk\""
				fi

				if [ -n "${5}" ]; then
					sign_line="${sign_line}, \"setup\""
				fi

				sign_line="${sign_line};"

				cat << EOF >> ${1}
                        signature-1 {
                                algo = "${conf_csum},rsa2048";
                                key-name-hint = "${conf_sign_keyname}";
                                ${sign_line}
                        };
EOF
			fi

			cat << EOF >> ${1}
                };
EOF
# End single config section

# Generate a single "pager" configuration section
		if [ "${OPTEEPAGER}" = "y" ]; then
			if [ "x${FITIMAGE_CONF_BY_NAME}" = "x1" ] ; then
				conf_name="${DTB}-pager"
			else
				conf_name="conf-${dtbcount}"
			fi

			cat << EOF >> ${1}
                ${conf_name} {
                        description = "${final_conf_desc}";
                        ${fdt_line}
EOF
			if [ "${dtb_ext}" != "dtbo" ]; then
			cat << EOF >> ${1}
                        ${kernel_line}
                        ${ramdisk_line}
                        ${setup_line}
                        ${loadables_pager_line}
EOF
			fi
			if test -n "${FITIMAGE_HASH_ALGO}"; then
				cat << EOF >> ${1}
                        hash-1 {
                                algo = "${conf_csum}";
                        };
EOF
			fi

			if [ ! -z "${conf_sign_keyname}" ] ; then

				sign_line="sign-images = \"kernel\""

				if [ -n "${3}" ]; then
					sign_line="${sign_line}, \"fdt\""
				fi

				if [ -n "${4}" ]; then
					sign_line="${sign_line}, \"ramdisk\""
				fi

				if [ -n "${5}" ]; then
					sign_line="${sign_line}, \"setup\""
				fi

				sign_line="${sign_line};"

				cat << EOF >> ${1}
                        signature-1 {
                                algo = "${conf_csum},rsa2048";
                                key-name-hint = "${conf_sign_keyname}";
                                ${sign_line}
                        };
EOF
			fi

			cat << EOF >> ${1}
                };
EOF
		fi
# End single config section

		dtbcount=`expr ${dtbcount} + 1`
	done
}

#
# Assemble fitImage
#
# $1 ... .its filename
# $2 ... fitImage name
# $3 ... include ramdisk
fitimage_assemble() {
	kernelcount=1
	dtbcount=""
	ramdiskcount=${3}
	setupcount=""
	teecount=1
	rm -f ${1} arch/${ARCH}/boot/${2}

	fitimage_emit_fit_header ${1}

	#
	# Step 1: Prepare a kernel image section.
	#
	fitimage_emit_section_maint ${1} imagestart

	uboot_prep_kimage
	fitimage_ti_secure linux.bin linux.bin.sec
	fitimage_emit_section_kernel ${1} "${kernelcount}" linux.bin.sec "${linux_comp}"

	#
	# Step 2: Prepare a DTB image section
	#
	if test -n "${KERNEL_DEVICETREE}"; then
		dtbcount=1
		dtboaddress="${UBOOT_DTBO_LOADADDRESS}"
		for DTB in ${KERNEL_DEVICETREE}; do
			if echo ${DTB} | grep -q '/dts/'; then
				bbwarn "${DTB} contains the full path to the the dts file, but only the dtb name should be used."
				DTB=`basename ${DTB} | sed 's,\.dts$,.dtb,g'`
			fi
			DTB_PATH="arch/${ARCH}/boot/dts/${DTB}"
			if [ ! -e "${DTB_PATH}" ]; then
				DTB_PATH="arch/${ARCH}/boot/${DTB}"
			fi
			DTB=$(basename "${DTB}")

			dtb_ext=${DTB##*.}
			if [ "${dtb_ext}" = "dtbo" ]; then
				if [ -n "${UBOOT_DTBO_LOADADDRESS}" ]; then
					dtb_loadline="load = <${dtboaddress}>;"
					num1=`printf "%d\n" ${dtboaddress}`
					num2=`printf "%d\n" ${UBOOT_DTBO_OFFSET}`
					num3=`expr $num1 + $num2`
					dtboaddress=`printf "0x%x\n" $num3`
				fi
			elif [ -n "${UBOOT_DTB_LOADADDRESS}" ]; then
				dtb_loadline="load = <${UBOOT_DTB_LOADADDRESS}>;"
			fi

			fitimage_ti_secure ${DTB_PATH} ${DTB_PATH}.sec
			if [ "x${FITIMAGE_DTB_BY_NAME}" = "x1" ] ; then
				fitimage_emit_section_dtb ${1} ${DTB} ${DTB_PATH}.sec "${dtb_loadline}"
			else
				fitimage_emit_section_dtb ${1} "fdt-${dtbcount}" ${DTB_PATH}.sec "${dtb_loadline}"
			fi
			if [ "x${dtbcount}" = "x1" ]; then
				dtbref=${DTB}
			fi
			dtbcount=`expr ${dtbcount} + 1`
		done
	fi

	#
	# Step 2a: Prepare OP/TEE image section
	#
	if [ "x${FITIMAGE_PACK_TEE}" = "x1" ] ; then
		mkdir -p ${B}/usr
		rm -f ${B}/usr/${OPTEEFLAVOR}.optee
		if [ -e "${DEPLOY_DIR_IMAGE}/${OPTEEFLAVOR}.optee" ]; then
			cp ${DEPLOY_DIR_IMAGE}/${OPTEEFLAVOR}.optee ${B}/usr/.
		fi
		TEE_PATH="usr/${OPTEEFLAVOR}.optee"
		fitimage_ti_secure ${TEE_PATH} ${TEE_PATH}.sec
		if [ "x${FITIMAGE_TEE_BY_NAME}" = "x1" ] ; then
			fitimage_emit_section_tee ${1} ${OPTEEFLAVOR}.optee ${TEE_PATH}.sec
		else
			fitimage_emit_section_tee ${1} "tee-${teecount}" ${TEE_PATH}.sec
		fi

		if [ "${OPTEEPAGER}" = "y" ]; then
			teecount=`expr ${teecount} + 1`
			rm -f ${B}/usr/${OPTEEFLAVOR}-pager.optee
			if [ -e "${DEPLOY_DIR_IMAGE}/${OPTEEFLAVOR}-pager.optee" ]; then
				cp ${DEPLOY_DIR_IMAGE}/${OPTEEFLAVOR}-pager.optee ${B}/usr/.
			fi
			TEE_PATH="usr/${OPTEEFLAVOR}-pager.optee"
			fitimage_ti_secure ${TEE_PATH} ${TEE_PATH}.sec
			if [ "x${FITIMAGE_TEE_BY_NAME}" = "x1" ] ; then
				fitimage_emit_section_tee ${1} ${OPTEEFLAVOR}-pager.optee ${TEE_PATH}.sec
			else
				fitimage_emit_section_tee ${1} "tee-${teecount}" ${TEE_PATH}.sec
			fi
		fi
	fi

	#
	# Step 3: Prepare a setup section. (For x86)
	#
	if test -e arch/${ARCH}/boot/setup.bin ; then
		setupcount=1
		fitimage_emit_section_setup ${1} "${setupcount}" arch/${ARCH}/boot/setup.bin
	fi

	#
	# Step 4: Prepare a ramdisk section.
	#
	if [ "x${ramdiskcount}" = "x1" ] ; then
		# Find and use the first initramfs image archive type we find
		for img in cpio.lz4 cpio.lzo cpio.lzma cpio.xz cpio.gz cpio; do
			initramfs_path="${DEPLOY_DIR_IMAGE}/${INITRAMFS_IMAGE}-${MACHINE}.${img}"
			initramfs_local="usr/${INITRAMFS_IMAGE}-${MACHINE}.${img}"
			echo "Using $initramfs_path"
			if [ -e "${initramfs_path}" ]; then
				fitimage_ti_secure ${initramfs_path} ${initramfs_local}.sec
				fitimage_emit_section_ramdisk ${1} "${ramdiskcount}" ${initramfs_local}.sec
				break
			fi
		done
	fi

	fitimage_emit_section_maint ${1} sectend

	# Force the first Kernel and DTB in the default config
	kernelcount=1
	if test -n "${dtbcount}"; then
		dtbcount=1
	fi
	teecount=1

	#
	# Step 5: Prepare a configurations section
	#
	fitimage_emit_section_maint ${1} confstart

	if [ "x${FITIMAGE_DTB_BY_NAME}" != "x1" ] ; then
		dtbref="fdt-${dtbcount}"
	fi
	if [ "x${FITIMAGE_TEE_BY_NAME}" = "x1" ] ; then
		teeref="${OPTEEFLAVOR}"
	else
		teeref="${teecount}"
	fi
	fitimage_emit_section_config ${1} "${kernelcount}" "${dtbref}" "${ramdiskcount}" "${setupcount}" "${teeref}"

	fitimage_emit_section_maint ${1} sectend

	fitimage_emit_section_maint ${1} fitend

	#
	# Step 6: Assemble the image
	#
	uboot-mkimage \
		${@'-D "${UBOOT_MKIMAGE_DTCOPTS}"' if len('${UBOOT_MKIMAGE_DTCOPTS}') else ''} \
		-f ${1} \
		arch/${ARCH}/boot/${2}

	#
	# Step 7: Sign the image and add public key to U-Boot dtb
	#
	if [ "x${UBOOT_SIGN_ENABLE}" = "x1" ] ; then
		uboot-mkimage \
			${@'-D "${UBOOT_MKIMAGE_DTCOPTS}"' if len('${UBOOT_MKIMAGE_DTCOPTS}') else ''} \
			-F -k "${UBOOT_SIGN_KEYDIR}" \
			-K "${DEPLOY_DIR_IMAGE}/${UBOOT_DTB_BINARY}" \
			-r arch/${ARCH}/boot/${2}
	fi
}

do_assemble_fitimage() {
	if echo ${KERNEL_IMAGETYPES} | grep -wq "fitImage"; then
		cd ${B}
		fitimage_assemble fit-image.its fitImage
	fi
}

addtask assemble_fitimage before do_install after do_compile

do_assemble_fitimage_initramfs() {
	if echo ${KERNEL_IMAGETYPES} | grep -wq "fitImage" && \
		test -n "${INITRAMFS_IMAGE}" ; then
		cd ${B}
		fitimage_assemble fit-image-${INITRAMFS_IMAGE}.its fitImage-${INITRAMFS_IMAGE} 1
	fi
}

addtask assemble_fitimage_initramfs before do_deploy after do_install

FITIMAGE_ITS_SUFFIX ?= "its"
FITIMAGE_ITB_SUFFIX ?= "itb"

FITIMAGE_ITS_IMAGE ?= "fitImage-its-${PKGE}-${PKGV}-${PKGR}-${MACHINE}-${DATETIME}.${FITIMAGE_ITS_SUFFIX}"
FITIMAGE_ITS_IMAGE[vardepsexclude] = "DATETIME"
FITIMAGE_ITS_BINARY ?= "fitImage-its.${FITIMAGE_ITS_SUFFIX}"
FITIMAGE_ITS_SYMLINK ?= "fitImage-its-${MACHINE}.${FITIMAGE_ITS_SUFFIX}"

FITIMAGE_ITB_IMAGE ?= "fitImage-linux.bin-${PKGE}-${PKGV}-${PKGR}-${MACHINE}-${DATETIME}.${FITIMAGE_ITB_SUFFIX}"
FITIMAGE_ITB_IMAGE[vardepsexclude] = "DATETIME"
FITIMAGE_ITB_BINARY ?= "fitImage-linux.bin.${FITIMAGE_ITB_SUFFIX}"
FITIMAGE_ITB_SYMLINK ?= "fitImage-linux.bin-${MACHINE}.${FITIMAGE_ITB_SUFFIX}"

FITIMAGE_INITRAMFS_ITS_IMAGE ?= "fitImage-its-${INITRAMFS_IMAGE}-${PKGE}-${PKGV}-${PKGR}-${MACHINE}-${DATETIME}.${FITIMAGE_ITS_SUFFIX}"
FITIMAGE_INITRAMFS_ITS_IMAGE[vardepsexclude] = "DATETIME"
FITIMAGE_INITRAMFS_ITS_BINARY ?= "fitImage-its-${INITRAMFS_IMAGE}.${FITIMAGE_ITS_SUFFIX}"
FITIMAGE_INITRAMFS_ITS_SYMLINK ?= "fitImage-its-${INITRAMFS_IMAGE}-${MACHINE}.${FITIMAGE_ITS_SUFFIX}"

FITIMAGE_INITRAMFS_ITB_IMAGE ?= "fitImage-${INITRAMFS_IMAGE}-${PKGE}-${PKGV}-${PKGR}-${MACHINE}-${DATETIME}.${FITIMAGE_ITB_SUFFIX}"
FITIMAGE_INITRAMFS_ITB_IMAGE[vardepsexclude] = "DATETIME"
FITIMAGE_INITRAMFS_ITB_BINARY ?= "fitImage-${INITRAMFS_IMAGE}.${FITIMAGE_ITB_SUFFIX}"
FITIMAGE_INITRAMFS_ITB_SYMLINK ?= "fitImage-${INITRAMFS_IMAGE}-${MACHINE}.${FITIMAGE_ITB_SUFFIX}"

kernel_do_deploy_append() {
	# Update deploy directory
	if echo ${KERNEL_IMAGETYPES} | grep -wq "fitImage"; then
		cd ${B}
		echo "Copying fit-image.its source file..."
		install -m 0644 fit-image.its ${DEPLOYDIR}/${FITIMAGE_ITS_IMAGE}
		install -m 0644 arch/${ARCH}/boot/fitImage ${DEPLOYDIR}/${FITIMAGE_ITB_IMAGE}

		if [ -n "${INITRAMFS_IMAGE}" ]; then
			echo "Copying fit-image-${INITRAMFS_IMAGE}.its source file..."
			install -m 0644 fit-image-${INITRAMFS_IMAGE}.its ${DEPLOYDIR}/${FITIMAGE_INITRAMFS_ITS_IMAGE}
			install -m 0644 arch/${ARCH}/boot/fitImage-${INITRAMFS_IMAGE} ${DEPLOYDIR}/${FITIMAGE_INITRAMFS_ITB_IMAGE}
		fi

		cd ${DEPLOYDIR}
		ln -sf ${FITIMAGE_ITS_IMAGE} ${FITIMAGE_ITS_SYMLINK}
		ln -sf ${FITIMAGE_ITS_IMAGE} ${FITIMAGE_ITS_BINARY}
		ln -sf ${FITIMAGE_ITB_IMAGE} ${FITIMAGE_ITB_SYMLINK}
		ln -sf ${FITIMAGE_ITB_IMAGE} ${FITIMAGE_ITB_BINARY}

		if [ -n "${INITRAMFS_IMAGE}" ]; then
			ln -sf ${FITIMAGE_INITRAMFS_ITS_IMAGE} ${FITIMAGE_INITRAMFS_ITS_SYMLINK}
			ln -sf ${FITIMAGE_INITRAMFS_ITS_IMAGE} ${FITIMAGE_INITRAMFS_ITS_BINARY}
			ln -sf ${FITIMAGE_INITRAMFS_ITB_IMAGE} ${FITIMAGE_INITRAMFS_ITB_SYMLINK}
			ln -sf ${FITIMAGE_INITRAMFS_ITB_IMAGE} ${FITIMAGE_INITRAMFS_ITB_BINARY}
		fi
	fi
}

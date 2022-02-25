# Class for creating  signed fitimage
# Description:
#
# You have to set the slot images in your recipe file following this example:
#
#    FITIMAGE_SLOTS ?= "kernel fdt setup ramdisk"
#
#    FITIMAGE_SLOT_kernel ?= "linux-yocto"
#    FITIMAGE_SLOT_kernel[type] ?= "kernel"
#
#    FITIMAGE_SLOT_fdt ?= "linux-yocto"
#    FITIMAGE_SLOT_fdt[type] ?= "file"
#    FITIMAGE_SLOT_fdt[file] ?= "${MACHINE}.dtb"
#
#    FITIMAGE_SLOT_ramdisk ?= "core-image-minimal"
#    FITIMAGE_SLOT_ramdisk[type] ?= "ramdisk"
#    FITIMAGE_SLOT_ramdisk[fstype] ?= "cpio.gz"
#
#    FITIMAGE_its ?= "setup.its"  (FIT Image creation File, if set, then no creation if config file)
#
# Additionally you need to provide a certificate and a key file
#
#    FITIMAGE_KEY_FILE ?= "development-1.key.pem"
#    FITIMAGE_CERT_FILE ?= "development-1.cert.pem"

LICENSE = "MIT"
inherit hab
inherit deploy

do_fetch[cleandirs] = "${S}"
do_patch[noexec] = "1"
do_compile[noexec] = "1"
deltask do_package_qa

DEPENDS = "u-boot-mkimage-native dtc-native"
FITIMAGE_HASH ??= "sha256"
FITIMAGE_LOADADDRESS ??= ""
FITIMAGE_LOADADDRESS_mx8m ?= "0x48000000"
FITIMAGE_ENTRYPOINT  ??= ""
FITIMAGE_ENTRYPOINT_mx8m ?= "0x48000000"
FITIMAGE_DTB_LOADADDRESS ??= ""
FITIMAGE_DTB_OVERLAY_LOADADDRESS ??= ""
FITIMAGE_DTB_LOADADDRESS_mx8m ??= "0x49F00000"
FITIMAGE_DTB_OVERLAY_LOADADDRESS_mx8m ??= "0x49FC0000"
FITIMAGE_RD_LOADADDRESS ??= ""
FITIMAGE_RD_ENTRYPOINT ??= ""

FITIMAGE_SIGN ??= "false"
FITIMAGE_SIGN[type] = "boolean"

FITIMAGE_SIGN_ENGINE ??= "software"
FITIMAGE_SIGN_ENGINE_mx8m ??= "nxphab"

FITIMAGE_NO_DTB_OVERLAYS ??= "false"
FITIMAGE_NO_DTB_OVERLAYS[type] = "boolean"

# Create dependency list from images
python __anonymous() {
    for slot in (d.getVar('FITIMAGE_SLOTS') or "").split():
        slotflags = d.getVarFlags('FITIMAGE_SLOT_%s' % slot)
        imgtype = slotflags.get('type') if slotflags else None
        if not imgtype:
            bb.debug(1, "No [type] given for slot '%s', defaulting to 'image'" % slot)
            imgtype = 'image'
        image = d.getVar('FITIMAGE_SLOT_%s' % slot)

        if not image:
            bb.error("No image set for slot '%s'. Specify via 'FITIMAGE_SLOT_%s = \"<recipe-name>\"'" % (slot, slot))
            return

        d.appendVarFlag('do_unpack', 'vardeps', ' FITIMAGE_SLOT_%s' % slot)
        depends = slotflags.get('depends') if slotflags else None
        if depends:
            d.appendVarFlag('do_unpack', 'depends', ' ' + depends)
            continue

        if imgtype == 'ramdisk':
            d.appendVarFlag('do_unpack', 'depends', ' ' + image + ':do_image_complete')
        else:
            d.appendVarFlag('do_unpack', 'depends', ' ' + image + ':do_deploy')
}

S = "${WORKDIR}"
B = "${WORKDIR}/build"

#
# Emit the fitImage ITS header
#
def fitimage_emit_fit_header(d,fd):
     import shutil
     fd.write('/dts-v1/;\n\n/ {\n')
     fd.write(d.expand('\tdescription = "fitImage for ${DISTRO_NAME}/${PV}/${MACHINE}";\n'))
     fd.write('\t#address-cells = <1>;\n')

#
# Emit the fitImage section bits
#
def fitimage_emit_section_maint(d,fd,var):
     import shutil
     if var == 'imagestart':
          fd.write('\timages {\n')
     elif var == 'confstart':
          fd.write('\tconfigurations {\n')
     elif var == 'sectend':
          fd.write('\t};\n')
     elif var == 'fitend':
          fd.write('};\n')

def fitimage_emit_section_kernel(d,fd,imgpath,imgsource,imgcomp):
    kernelcount = 1
    kernel_csum = d.expand("${FITIMAGE_HASH}")
    kernel_entryline = ""
    kernel_loadline = ""
    if len(d.expand("${FITIMAGE_LOADADDRESS}")) > 0:
        kernel_loadline = "load = <%s>;" % d.expand("${FITIMAGE_LOADADDRESS}")
    if len(d.expand("${FITIMAGE_ENTRYPOINT}")) > 0:
        kernel_entryline = "entry = <%s>;" % d.expand("${FITIMAGE_ENTRYPOINT}")
    arch = d.getVar("TARGET_ARCH", True)
    arch = "arm64" if arch == "aarch64" else arch
    fd.write('\t\t'     + 'kernel-%s {\n' % kernelcount)
    fd.write('\t\t\t'   +   'description = "Linux kernel";\n')
    fd.write('\t\t\t'   +   'data = /incbin/("%s/%s");\n' % (imgpath,imgsource))
    fd.write('\t\t\t'   +   'type = "kernel";\n')
    fd.write('\t\t\t'   +   'arch = "%s";\n' % arch)
    fd.write('\t\t\t'   +   'os = "linux";\n')
    fd.write('\t\t\t'   +   'compression = "%s";\n' % imgcomp)
    fd.write('\t\t\t'   +   '%s\n' % kernel_loadline)
    fd.write('\t\t\t'   +   '%s\n' % kernel_entryline)
    fd.write('\t\t\t'   +   'hash-1 {\n')
    fd.write('\t\t\t\t' +     'algo = "%s";\n' % kernel_csum)
    fd.write('\t\t\t'   +   '};\n')
    fd.write('\t\t'     + '};\n')

#
# Emit the fitImage ITS DTB section
#
def fitimage_emit_section_dtb(d,fd,dtb_file,dtb_path):
    dtb_csum = d.expand("${FITIMAGE_HASH}")
    arch = d.getVar("TARGET_ARCH", True)
    arch = "arm64" if arch == "aarch64" else arch

    dtb_loadline=""
    if len(d.expand("${FITIMAGE_DTB_LOADADDRESS}")) > 0:
        dtb_loadline = "load = <%s>;" % d.expand("${FITIMAGE_DTB_LOADADDRESS}")

    fd.write('\t\t'     + 'fdt-%s {\n' % dtb_file)
    fd.write('\t\t\t'   +   'description = "Flattened Device Tree blob";\n')
    fd.write('\t\t\t'   +   'data = /incbin/("%s/%s");\n' % (dtb_path, dtb_file))
    fd.write('\t\t\t'   +   'type = "flat_dt";\n')
    fd.write('\t\t\t'   +   'arch = "%s";\n' % arch)
    fd.write('\t\t\t'   +   'compression = "none";\n')
    fd.write('\t\t\t'   +   '%s\n' % dtb_loadline)
    fd.write('\t\t\t'   +   'hash-1 {\n')
    fd.write('\t\t\t\t' +     'algo = "%s";\n' % dtb_csum)
    fd.write('\t\t\t'   +   '};\n')
    fd.write('\t\t'     + '};\n')

#
# Emit the fitImage ITS DTB overlay section
#
def fitimage_emit_section_dtb_overlay(d,fd,dtb_file,dtb_path):
    dtb_csum = d.expand("${FITIMAGE_HASH}")
    arch = d.getVar("TARGET_ARCH", True)
    arch = "arm64" if arch == "aarch64" else arch

    dtb_loadline=""
    if len(d.expand("${FITIMAGE_DTB_OVERLAY_LOADADDRESS}")) > 0:
        dtb_loadline = "load = <%s>;" % d.expand("${FITIMAGE_DTB_OVERLAY_LOADADDRESS}")

    fd.write('\t\t'     + 'fdt-%s {\n' % dtb_file)
    fd.write('\t\t\t'   +   'description = "Flattened Device Tree Overlay blob";\n')
    fd.write('\t\t\t'   +   'data = /incbin/("%s/%s");\n' % (dtb_path, dtb_file))
    fd.write('\t\t\t'   +   'type = "flat_dt";\n')
    fd.write('\t\t\t'   +   'arch = "%s";\n' % arch)
    fd.write('\t\t\t'   +   'compression = "none";\n')
    fd.write('\t\t\t'   +   '%s\n' % dtb_loadline)
    fd.write('\t\t\t'   +   'hash-1 {\n')
    fd.write('\t\t\t\t' +     'algo = "%s";\n' % dtb_csum)
    fd.write('\t\t\t'   +   '};\n')
    fd.write('\t\t'     + '};\n')


#
# Emit the fitImage ITS ramdisk section
#
def fitimage_emit_section_ramdisk(d,fd,img_file,img_path):
    ramdisk_count = "1"
    ramdisk_csum = d.expand("${FITIMAGE_HASH}")
    arch = d.getVar("TARGET_ARCH", True)
    arch = "arm64" if arch == "aarch64" else arch
    ramdisk_ctype = "none"
    ramdisk_loadline = "load = <00000000>;"
    ramdisk_entryline = "entry = <00000000>;"

    if len(d.expand("${FITIMAGE_RD_ENTRYPOINT}")) > 0:
        ramdisk_entryline = "entry = <%s>;" % d.expand("${FITIMAGE_RD_ENTRYPOINT}")
    if len(d.expand("${FITIMAGE_RD_LOADADDRESS}")) > 0:
        ramdisk_loadline = "load = <%s>;" % d.expand("${FITIMAGE_RD_LOADADDRESS}")

    fd.write('\t\t'     + 'ramdisk-%s {\n' % ramdisk_count)
    fd.write('\t\t\t'   +   'description = "%s";\n' % img_file)
    fd.write('\t\t\t'   +   'data = /incbin/("%s/%s");\n' % (img_path, img_file))
    fd.write('\t\t\t'   +   'type = "ramdisk";\n')
    fd.write('\t\t\t'   +   'arch = "%s";\n' % arch)
    fd.write('\t\t\t'   +   'os = "linux";\n')
    fd.write('\t\t\t'   +   '%s\n' % ramdisk_loadline)
    fd.write('\t\t\t'   +   '%s\n' % ramdisk_entryline)
    fd.write('\t\t\t'   +   'hash-1 {\n')
    fd.write('\t\t\t\t' +     'algo = "%s";\n' % ramdisk_csum)
    fd.write('\t\t\t'   +   '};\n')
    fd.write('\t\t'     + '};\n')

#
# Emit the fitImage ITS configuration section
#
def fitimage_emit_section_config(d,fd,dtb,kernelcount,ramdiskcount,setupcount,i):
    conf_csum = d.expand("${FITIMAGE_HASH}")
    conf_encrypt = d.getVar("FITIMAGE_SIGNATURE_ENCRYPTION", True) or ""
    path, filename = os.path.split(d.getVar("FITIMAGE_SIGN_KEY_PATH", True) or "")
    conf_sign_keyname = filename.split('.')


    conf_desc="Linux kernel"
    kernel_line="kernel = \"kernel-1\";"
    fdt_line=""
    ramdisk_line=""
    setup_line=""
    default_line=""
    if len(dtb) > 0:
         conf_desc = conf_desc + ", FDT blob"
         fdt_line="fdt = \"fdt-%s\";" % dtb
    if len(ramdiskcount) > 0:
         conf_desc = conf_desc + ", ramdisk"
         ramdisk_line="ramdisk = \"ramdisk-%s\";" % ramdiskcount
    if len(setupcount) > 0:
         conf_desc = conf_desc + ", setup"
    if i  == 1:
          default_line="default = \"%s\";" % dtb

    fd.write('\t\t'   + '%s\n' % default_line)
    fd.write('\t\t'   + '%s {\n' % dtb)
    fd.write('\t\t\t' +   'description = "%d %s";\n' % (i,conf_desc))
    fd.write('\t\t\t' +   '%s\n' % kernel_line)
    fd.write('\t\t\t' +   '%s\n' % fdt_line)
    fd.write('\t\t\t' +   '%s\n' % ramdisk_line)
    fd.write('\t\t\t' +   '%s\n' % setup_line)

    if len(conf_sign_keyname) > 0:
       sign_line="sign-images = \"kernel\""
       if len(dtb) > 0:
            sign_line = sign_line + ", \"fdt\""
       if len(ramdiskcount) > 0:
            sign_line = sign_line + ", \"ramdisk\""
       if len(setupcount) > 0:
            sign_line = sign_line + ", \"setup\""
       sign_line = sign_line + ";"
       fd.write('\t\t\t'   + 'signature-1 {\n')
       fd.write('\t\t\t\t' +   'algo = "%s,%s";\n' % (conf_csum, conf_encrypt))
       fd.write('\t\t\t\t' +   'key-name-hint = "%s";\n' % conf_sign_keyname[0])
       fd.write('\t\t\t\t' +   '%s\n' % sign_line)
       fd.write('\t\t\t\t' +   'signer-name = "%s";\n' % d.getVar("FITIMAGE_SIGNER", True))
       fd.write('\t\t\t\t' +   'signer-version = "%s";\n' % d.getVar("FITIMAGE_SIGNER_VERSION", True))
       fd.write('\t\t\t'   + '};\n')
    else:
       bb.warn(d.expand("${FITIMAGE_SIGN_KEY_PATH} No Key File for signing FIT Image => FIT Image don't get a signature"))

    fd.write('\t\t'  + '};\n')

#
# Emits a device tree overlay config section
#
def fitimage_emit_section_config_fdto(d,fd,dtb):
    conf_csum = d.expand("${FITIMAGE_HASH}")
    conf_encrypt = d.getVar("FITIMAGE_SIGNATURE_ENCRYPTION", True) or ""
    path, filename = os.path.split(d.getVar("FITIMAGE_SIGN_KEY_PATH", True) or "")
    conf_sign_keyname = filename.split('.')


    conf_desc="Device Tree Overlay"
    if len(dtb) > 0:
         fdt_line="fdt = \"fdt-%s\";" % dtb

    fd.write('\t\t'   + '%s {\n' % dtb)
    fd.write('\t\t\t' +   'description = "%s";\n' % (conf_desc))
    fd.write('\t\t\t' +   '%s\n' % fdt_line)

    if len(conf_sign_keyname) > 0:
       sign_line="sign-images = \"fdt\""

       fd.write('\t\t\t'   + 'signature-1 {\n')
       fd.write('\t\t\t\t' +   'algo = "%s,%s";\n' % (conf_csum, conf_encrypt))
       fd.write('\t\t\t\t' +   'key-name-hint = "%s";\n' % conf_sign_keyname[0])
       fd.write('\t\t\t\t' +   '%s;\n' % sign_line)
       fd.write('\t\t\t\t' +   'signer-name = "%s";\n' % d.getVar("FITIMAGE_SIGNER", True))
       fd.write('\t\t\t\t' +   'signer-version = "%s";\n' % d.getVar("FITIMAGE_SIGNER_VERSION", True))
       fd.write('\t\t\t'   + '};\n')
    else:
       bb.warn(d.expand("${FITIMAGE_SIGN_KEY_PATH} No Key File for signing FIT Image => FIT Image don't get a signature"))

    fd.write('\t\t'  + '};\n')

def write_manifest(d):
    import shutil

    machine = d.getVar('MACHINE')
    path = d.expand("${S}")
    kernelcount=1
    DTBS = ""
    DTBOS = ""
    ramdiskcount = ""
    setupcount = ""


    try:
        fd = open('%s/manifest.its' % path, 'w')
    except OSError:
        raise bb.build.FuncFailed('Unable to open manifest.its')

    fitimage_emit_fit_header(d,fd)
    fitimage_emit_section_maint(d,fd,'imagestart')

    for slot in (d.getVar('FITIMAGE_SLOTS') or "").split():
         slotflags = d.getVarFlags('FITIMAGE_SLOT_%s' % slot)
         if slotflags and 'type' in slotflags:
            imgtype = slotflags.get('type')
         else:
            imgtype = ''
         if imgtype == 'kernel':
            if slotflags and 'file' in slotflags:
                imgsource = d.getVarFlag('FITIMAGE_SLOT_%s' % slot, 'file')
            else:
                imgsource = "%s-%s.bin" % ("zImage", machine)
            if slotflags and 'comp' in slotflags:
                imgcomp = d.getVarFlag('FITIMAGE_SLOT_%s' % slot, 'comp')
            else:
                imgcomp = "none"
            imgpath = d.getVar("DEPLOY_DIR_IMAGE")
            fitimage_emit_section_kernel(d,fd,imgpath,imgsource,imgcomp)
         elif imgtype == 'fdt':
            if slotflags and 'file' in slotflags:
                imgsource = d.getVarFlag('FITIMAGE_SLOT_%s' % slot, 'file')
            else:
                imgsource = "%s.dtb" % (machine)
            for dtb in (imgsource or "").split():
                dtb_path, dtb_file = os.path.split(dtb)
                DTBS = DTBS + " " + dtb_file
                if len(dtb_path) ==0:
                   dtb_path = d.getVar("DEPLOY_DIR_IMAGE")
                fitimage_emit_section_dtb(d,fd,dtb_file,dtb_path)
         elif imgtype == 'fdto':
                imgsource = d.getVarFlag('FITIMAGE_SLOT_%s' % slot, 'file')
                for dtb in (imgsource or "").split():
                    dtb_path, dtb_file = os.path.split(dtb)
                    DTBOS = DTBOS + " " + dtb_file
                    if len(dtb_path) ==0:
                        dtb_path = d.getVar("DEPLOY_DIR_IMAGE")
                    fitimage_emit_section_dtb_overlay(d,fd,dtb_file,dtb_path)
         elif imgtype == 'ramdisk':
            ramdiskcount = "1"
            if slotflags and 'fstype' in slotflags:
               img_fstype = slotflags.get('fstype')
            img_file = "%s-%s.%s" % (d.getVar('FITIMAGE_SLOT_%s' % slot), machine, img_fstype)
            img_path = d.getVar("DEPLOY_DIR_IMAGE", True)
            fitimage_emit_section_ramdisk(d,fd,img_file,img_path)
    fitimage_emit_section_maint(d,fd,'sectend')
    #
    # Step 5: Prepare a configurations section
    #
    fitimage_emit_section_maint(d,fd,'confstart')
    i = 1
    for dtb in (DTBS or "").split():
        fitimage_emit_section_config(d,fd,dtb,kernelcount,ramdiskcount,setupcount,i)
        i = i +1
    for dtb in (DTBOS or "").split():
        fitimage_emit_section_config_fdto(d,fd,dtb)

    fitimage_emit_section_maint(d,fd,'sectend')
    fitimage_emit_section_maint(d,fd,'fitend')
    fd.close()

do_unpack_append() {
    write_manifest(d)
}

do_fitimagebundle () {
    if [ "${FITIMAGE_SIGN}" = "true" -a "${FITIMAGE_SIGN_ENGINE}" = "software" ] ; then
        if [ -e ${FITIMAGE_SIGN_KEY_PATH} ] ; then
            path_key=$(dirname "${FITIMAGE_SIGN_KEY_PATH}")
            printf '/dts-v1/;\n\n/ {\n};\n' > pubkey.dts
            dtc -O dtb pubkey.dts > pubkey.dtb
            #uboot-mkimage-fit \
            mkimage \
                -f "${S}/manifest.its" \
                -k "${path_key}" \
                -K "${B}/pubkey.dtb" \
                -r "${B}/fitImage"
        else
            bberror "${FITIMAGE_SIGN_KEY_PATH} Key File do not exist for signing FIT Image"
        fi
    else
        #uboot-mkimage-fit \
        mkimage \
            -f "${S}/manifest.its" \
            -r "${B}/fitImage"
    fi
}
do_fitimagebundle[dirs] = "${B}"
addtask fitimagebundle after do_configure before do_build

python do_signhab() {
    if oe.data.typed_value('FITIMAGE_SIGN', d) and d.getVar('FITIMAGE_SIGN_ENGINE') == 'nxphab':
        loadaddr = int(d.getVar('UBOOT_ENTRYPOINT'), 16)
        build_dir = d.getVar("B", True)
        image_path = os.path.join(build_dir, 'fitImage')
        image_size = os.stat(image_path).st_size
        aligned_size = (image_size + 0x1000 - 1) & ~(0x1000 - 1)

        sign_inplace(d, image_path, aligned_size, loadaddr, [])
}
addtask signhab after do_fitimagebundle before do_deploy

do_deploy[vardepsexclude] += "IMAGE_VERSION_SUFFIX"
do_deploy() {
    # Update deploy directory
    install -d ${DEPLOYDIR}

    its_base_name="${PN}-${PV}-${PR}-${MACHINE}${IMAGE_VERSION_SUFFIX}"
    its_symlink_name="${PN}-${MACHINE}"
    printf 'Copying fit-image.its source file...'
    install -m 0644 ${S}/manifest.its ${DEPLOYDIR}/${its_base_name}.its

    linux_bin_symlink_name="fitImage"
    linux_bin_base_name="${linux_bin_symlink_name}-${PN}-${PV}-${PR}-${MACHINE}${IMAGE_VERSION_SUFFIX}"
    printf 'Copying fitImage file...'
    install -m 0644 ${B}/fitImage ${DEPLOYDIR}/${linux_bin_base_name}

    cd ${DEPLOYDIR}
    ln -sf ${its_base_name}.its ${its_symlink_name}.its
    ln -sf ${linux_bin_base_name} ${linux_bin_symlink_name}
}
addtask deploy after do_fitimagebundle before do_build

do_deploy[cleandirs] = "${DEPLOYDIR}"

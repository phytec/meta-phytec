# Class for creating  signed fitimage
# Description:
#
# You have to set the slot images in your recipe file following this example:
#
#    FITIMAGE_SLOTS ?= "kernel fdt fdto setup ramdisk bootscript tee"
#
#    FITIMAGE_SLOT_kernel ?= "${PREFERRED_PROVIDER_virtual/kernel}"
#    FITIMAGE_SLOT_kernel[type] ?= "kernel"
#
#    FITIMAGE_SLOT_fdt ?= "${PREFERRED_PROVIDER_virtual/kernel}"
#    FITIMAGE_SLOT_fdt[type] ?= "fdt"
#    FITIMAGE_SLOT_fdt[file] ?= "${MACHINE}.dtb"
#
#    FITIMAGE_SLOT_fdto ?= "${PREFERRED_PROVIDER_virtual/kernel}"
#    FITIMAGE_SLOT_fdto[type] ?= "fdto"
#    FITIMAGE_SLOT_fdto[file] ?= "list of all dtbo files from KERNEL_DEVICETREE"
#
# Apply a number of overlays to the first devicetree in the KERNEL_DEVICETREE
#
#    FITIMAGE_SLOT_fdtapply ?= "${PREFERRED_PROVIDER_virtual/kernel}"
#    FITIMAGE_SLOT_fdtapply[type] ?= "fdtapply"
#    FITIMAGE_SLOT_fdtapply[file] ?= "${MACHINE}.dtb list of dtbo"
#    FITIMAGE_SLOT_fdtapply[name] ?= "name for new generated fdt"
#
#    FITIMAGE_SLOT_ramdisk ?= "core-image-minimal"
#    FITIMAGE_SLOT_ramdisk[type] ?= "ramdisk"
#    FITIMAGE_SLOT_ramdisk[fstype] ?= "cpio.gz"
#
#    FITIMAGE_SLOT_bootscript ?= "bootscript"
#    FITIMAGE_SLOT_bootscript[file] ?= "boot.scr"
#
#    FITIMAGE_SLOT_tee ?= "optee-os"
#    FITIMAGE_SLOT_tee[type] ?= "tee"
#    FITIMAGE_SLOT_tee[file] ?= "optee/tee.bin"
#
#    FITIMAGE_its ?= "setup.its"  (FIT Image creation File, if set, then no creation if config file)
#
# Additionally, you need to provide a path where to find a key file
#
#    UBOOT_SIGN_KEYDIR = "${CERT_PATH}/fit"
#    UBOOT_SIGN_KEYNAME = "FIT-4096"
#
# or use
#
#    UBOOT_SIGN_ENABLE = "0"
#
# for an unsigned fitimage

LICENSE = "MIT"
inherit_defer ${@ "hab" if "imx-generic-bsp" in d.getVar("OVERRIDES").split(":") else "" }
inherit deploy
inherit signing-helpers

do_fetch[cleandirs] = "${S}"
do_patch[noexec] = "1"
do_compile[noexec] = "1"
deltask do_package_qa
do_unpack[depends] += "dtc-native:do_populate_sysroot"
do_fitimagebundle[depends] += "libp11-native:do_populate_sysroot"
DEPENDS = "u-boot-mkimage-native dtc-native"
FITIMAGE_LOADADDRESS ??= ""
FITIMAGE_LOADADDRESS:mx8m-nxp-bsp ?= "0x48000000"
FITIMAGE_LOADADDRESS:mx91-nxp-bsp ?= "0x88000000"
FITIMAGE_LOADADDRESS:mx93-nxp-bsp ?= "0x88000000"
FITIMAGE_LOADADDRESS:k3 ?= "0x82000000"
FITIMAGE_ENTRYPOINT  ??= ""
FITIMAGE_ENTRYPOINT:mx8m-nxp-bsp ?= "0x48000000"
FITIMAGE_ENTRYPOINT:mx91-nxp-bsp ?= "0x88000000"
FITIMAGE_ENTRYPOINT:mx93-nxp-bsp ?= "0x88000000"
FITIMAGE_ENTRYPOINT:k3 ?= "0x82000000"
FITIMAGE_DTB_LOADADDRESS ??= ""
FITIMAGE_DTB_OVERLAY_LOADADDRESS ??= ""
FITIMAGE_DTB_LOADADDRESS:mx8m-nxp-bsp ??= "0x4A300000"
FITIMAGE_DTB_LOADADDRESS:mx91-nxp-bsp ??= "0x8A380000"
FITIMAGE_DTB_LOADADDRESS:mx93-nxp-bsp ??= "0x8A380000"
FITIMAGE_DTB_OVERLAY_LOADADDRESS:mx8m-nxp-bsp ??= "0x4A3C0000"
FITIMAGE_DTB_OVERLAY_LOADADDRESS:mx91-nxp-bsp ??= "0x8A440000"
FITIMAGE_DTB_OVERLAY_LOADADDRESS:mx93-nxp-bsp ??= "0x8A440000"
FITIMAGE_RD_LOADADDRESS ??= ""
FITIMAGE_RD_ENTRYPOINT ??= ""
FITIMAGE_TEE_LOADADDRESS ??= ""
FITIMAGE_TEE_ENTRYPOINT ??= ""
FIT_CONF_PREFIX ??= ""
FIT_CONF_PREFIX:k3 ?= "conf-"
FIT_CONF_PREFIX:mx8m-nxp-bsp ?= "conf-"

FITIMAGE_SIGN_ENGINE ??= "software"
FITIMAGE_UBOOT_ENTRYPOINT ??= "UBOOT_ENTRYPOINT"
FITIMAGE_UBOOT_ENTRYPOINT:mx8m-nxp-bsp ??= "0x40480000"

FITIMAGE_NO_DTB_OVERLAYS ??= "false"
FITIMAGE_NO_DTB_OVERLAYS[type] = "boolean"

FITIMAGE_IMAGE_SUFFIX ??= ""
FITIMAGE_IMAGE_BASE_NAME ??= "fitImage-${PN}-${PV}-${PR}-${MACHINE}${IMAGE_VERSION_SUFFIX}${FITIMAGE_IMAGE_SUFFIX}"
FITIMAGE_IMAGE_LINK_NAME ??= "fitImage"
FITIMAGE_ITS_BASE_NAME ??= "${PN}-${PV}-${PR}-${MACHINE}${IMAGE_VERSION_SUFFIX}"
FITIMAGE_ITS_LINK_NAME ??= "${PN}-${MACHINE}"

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

S = "${UNPACKDIR}"
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
    kernel_csum = d.expand("${FIT_HASH_ALG}")
    kernel_entryline = ""
    kernel_loadline = ""
    if len(d.expand("${FITIMAGE_LOADADDRESS}")) > 0:
        kernel_loadline = "load = <%s>;" % d.expand("${FITIMAGE_LOADADDRESS}")
        if len(d.expand("${FITIMAGE_DTB_LOADADDRESS}")) > 0 :
            kernel_path = os.path.join(imgpath,imgsource)
            kernel_size = os.stat(kernel_path).st_size
            kernel_ram_endaddress = int(d.expand("${FITIMAGE_LOADADDRESS}"), 16) + kernel_size
            if kernel_ram_endaddress > int(d.expand("${FITIMAGE_DTB_LOADADDRESS}"), 16):
                bb.error("The kernel size is too large for the ram area between kernel and fdt load address")

    if len(d.expand("${FITIMAGE_ENTRYPOINT}")) > 0:
        kernel_entryline = "entry = <%s>;" % d.expand("${FITIMAGE_ENTRYPOINT}")
    arch = d.getVar("TARGET_ARCH")
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
    dtb_csum = d.expand("${FIT_HASH_ALG}")
    arch = d.getVar("TARGET_ARCH")
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
    dtb_csum = d.expand("${FIT_HASH_ALG}")
    arch = d.getVar("TARGET_ARCH")
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
    ramdisk_csum = d.expand("${FIT_HASH_ALG}")
    arch = d.getVar("TARGET_ARCH")
    arch = "arm64" if arch == "aarch64" else arch
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
    fd.write('\t\t\t'   +   'compression = "none";\n')
    fd.write('\t\t\t'   +   '%s\n' % ramdisk_loadline)
    fd.write('\t\t\t'   +   '%s\n' % ramdisk_entryline)
    fd.write('\t\t\t'   +   'hash-1 {\n')
    fd.write('\t\t\t\t' +     'algo = "%s";\n' % ramdisk_csum)
    fd.write('\t\t\t'   +   '};\n')
    fd.write('\t\t'     + '};\n')

def fitimage_emit_section_bootscript(d,fd,imgpath,imgsource):
    bootscript_csum = d.expand("${FIT_HASH_ALG}")
    arch = d.getVar("TARGET_ARCH")
    arch = "arm64" if arch == "aarch64" else arch

    fd.write('\t\t'     + 'bootscr-%s {\n' % imgsource)
    fd.write('\t\t\t'   +   'description = "U-boot script";\n')
    fd.write('\t\t\t'   +   'data = /incbin/("%s/%s");\n' % (imgpath,imgsource))
    fd.write('\t\t\t'   +   'type = "script";\n')
    fd.write('\t\t\t'   +   'arch = "%s";\n' % arch)
    fd.write('\t\t\t'   +   'os = "linux";\n')
    fd.write('\t\t\t'   +   'compression = "none";\n')
    fd.write('\t\t\t'   +   'hash-1 {\n')
    fd.write('\t\t\t\t' +     'algo = "%s";\n' % bootscript_csum)
    fd.write('\t\t\t'   +   '};\n')
    fd.write('\t\t'     + '};\n')

#
# Emit the fitImage Trusted Execution Section
#
def fitimage_emit_section_tee(d,fd,teecount,imgpath,imgsource,imgcomp):
    tee_csum = d.expand("${FIT_HASH_ALG}")
    tee_entryline = ""
    tee_loadline = ""
    if len(d.expand("${FITIMAGE_TEE_LOADADDRESS}")) > 0:
        tee_loadline = "load = <%s>;" % d.expand("${FITIMAGE_TEE_LOADADDRESS}")
    if len(d.expand("${FITIMAGE_TEE_ENTRYPOINT}")) > 0:
        tee_entryline = "entry = <%s>;" % d.expand("${FITIMAGE_TEE_ENTRYPOINT}")
    arch = d.getVar("TARGET_ARCH")
    arch = "arm64" if arch == "aarch64" else arch
    fd.write('\t\t'     + 'tee-%s {\n' % teecount)
    fd.write('\t\t\t'   +   'description = "TEE";\n')
    fd.write('\t\t\t'   +   'data = /incbin/("%s/%s");\n' % (imgpath, imgsource))
    fd.write('\t\t\t'   +   'type = "tee";\n')
    fd.write('\t\t\t'   +   'arch = "%s";\n' % arch)
    fd.write('\t\t\t'   +   'os = "linux";\n')
    fd.write('\t\t\t'   +   'compression = "%s";\n' % imgcomp)
    fd.write('\t\t\t'   +   '%s\n' % tee_loadline)
    fd.write('\t\t\t'   +   '%s\n' % tee_entryline)
    fd.write('\t\t\t'   +   'hash-1 {\n')
    fd.write('\t\t\t\t' +     'algo = "%s";\n' % tee_csum)
    fd.write('\t\t\t'   +   '};\n')
    fd.write('\t\t'     + '};\n')

#
# Emit the fitImage ITS configuration section
#
def fitimage_emit_section_config(d,fd,dtb,teecount,kernelcount,ramdiskcount,setupcount,bootscriptid,i):
    conf_csum = d.expand("${FIT_HASH_ALG}")
    conf_encrypt = d.getVar("FIT_SIGN_ALG") or ""

    conf_sign_keyname = d.getVar("UBOOT_SIGN_KEYNAME")

    conf_desc="Linux kernel"
    kernel_line="kernel = \"kernel-1\";"
    tee_line=""
    if teecount > 0:
        tee_line="tee = \"tee-1\";"
    fdt_line=""
    ramdisk_line=""
    setup_line=""
    bootscript_line=""
    default_line=""
    conf_node="%s%s" % (d.getVar("FIT_CONF_PREFIX"),dtb)
    if len(dtb) > 0:
         conf_desc = conf_desc + ", FDT blob"
         fdt_line="fdt = \"fdt-%s\";" % dtb
    if len(ramdiskcount) > 0:
         conf_desc = conf_desc + ", ramdisk"
         ramdisk_line="ramdisk = \"ramdisk-%s\";" % ramdiskcount
    if len(setupcount) > 0:
         conf_desc = conf_desc + ", setup"
    if len(bootscriptid) > 0:
         conf_desc = conf_desc + ", u-boot script"
         bootscript_line="bootscr = \"bootscr-%s\";" % bootscriptid
    if i  == 1:
          default_line="default = \"%s\";" % conf_node

    fd.write('\t\t'   + '%s\n' % default_line)
    fd.write('\t\t'   + '%s {\n' % conf_node)
    fd.write('\t\t\t' +   'description = "%d %s";\n' % (i,conf_desc))
    fd.write('\t\t\t' +   '%s\n' % kernel_line)
    if teecount > 0:
        fd.write('\t\t\t' +   '%s\n' % tee_line)
    fd.write('\t\t\t' +   '%s\n' % fdt_line)
    fd.write('\t\t\t' +   '%s\n' % ramdisk_line)
    fd.write('\t\t\t' +   '%s\n' % setup_line)
    fd.write('\t\t\t' +   '%s\n' % bootscript_line)

    if len(conf_sign_keyname) > 0:
       sign_line="sign-images = \"kernel\""
       if teecount > 0:
            sign_line = sign_line + ", \"tee\""
       if len(dtb) > 0:
            sign_line = sign_line + ", \"fdt\""
       if len(ramdiskcount) > 0:
            sign_line = sign_line + ", \"ramdisk\""
       if len(setupcount) > 0:
            sign_line = sign_line + ", \"setup\""
       if len(bootscriptid) > 0:
            sign_line = sign_line + ", \"bootscr\""
       sign_line = sign_line + ";"
       fd.write('\t\t\t'   + 'signature-1 {\n')
       fd.write('\t\t\t\t' +   'algo = "%s,%s";\n' % (conf_csum, conf_encrypt))
       fd.write('\t\t\t\t' +   'key-name-hint = "%s";\n' % conf_sign_keyname)
       fd.write('\t\t\t\t' +   '%s\n' % sign_line)
       fd.write('\t\t\t'   + '};\n')
    else:
       bb.warn(d.expand("${UBOOT_SIGN_KEYNAME} No Key File for signing FIT Image => FIT Image don't get a signature"))

    fd.write('\t\t'  + '};\n')

#
# Emits a device tree overlay config section
#
def fitimage_emit_section_config_fdto(d,fd,dtb):
    conf_csum = d.expand("${FIT_HASH_ALG}")
    conf_encrypt = d.getVar("FIT_SIGN_ALG") or ""

    conf_sign_keyname = d.getVar("UBOOT_SIGN_KEYNAME")


    conf_desc="Device Tree Overlay"
    if len(dtb) > 0:
         fdt_line="fdt = \"fdt-%s\";" % dtb

    fd.write('\t\t'   + '%s%s {\n' % (d.getVar("FIT_CONF_PREFIX"),dtb))
    fd.write('\t\t\t' +   'description = "%s";\n' % (conf_desc))
    fd.write('\t\t\t' +   '%s\n' % fdt_line)

    if len(conf_sign_keyname) > 0:
       sign_line="sign-images = \"fdt\""

       fd.write('\t\t\t'   + 'signature-1 {\n')
       fd.write('\t\t\t\t' +   'algo = "%s,%s";\n' % (conf_csum, conf_encrypt))
       fd.write('\t\t\t\t' +   'key-name-hint = "%s";\n' % conf_sign_keyname)
       fd.write('\t\t\t\t' +   '%s;\n' % sign_line)
       fd.write('\t\t\t'   + '};\n')
    else:
       bb.warn(d.expand("${UBOOT_SIGN_KEYNAME} No Key File for signing FIT Image => FIT Image don't get a signature"))

    fd.write('\t\t'  + '};\n')

def write_manifest(d):
    import shutil

    machine = d.getVar('MACHINE')
    path = d.expand("${S}") + "/"
    kernelcount=1
    DTBS = ""
    DTBOS = ""
    ramdiskcount = ""
    setupcount = ""
    teecount=0
    bootscriptid = ""

    def get_dtbs(dtb_suffix):
        _dtbs = d.getVar('KERNEL_DEVICETREE')
        dtbs = str()

        for dtb in _dtbs.split():
            if not dtb.endswith(dtb_suffix):
                continue
            dtbs += os.path.basename(dtb) + ' '

        return dtbs

    try:
        fd = open('%smanifest.its' % path, 'w')
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
                imgsource = get_dtbs("dtb")
                if not imgsource:
                    imgsource = "%s.dtb" % (machine)
            for dtb in (imgsource or "").split():
                dtb_path, dtb_file = os.path.split(dtb)
                DTBS = DTBS + " " + dtb_file
                if len(dtb_path) ==0:
                   dtb_path = d.getVar("DEPLOY_DIR_IMAGE")
                fitimage_emit_section_dtb(d,fd,dtb_file,dtb_path)
         elif imgtype == 'fdto':
                imgsource = d.getVarFlag('FITIMAGE_SLOT_%s' % slot, 'file')
                if not imgsource:
                    imgsource = get_dtbs("dtbo")
                for dtb in (imgsource or "").split():
                    dtb_path, dtb_file = os.path.split(dtb)
                    DTBOS = DTBOS + " " + dtb_file
                    if len(dtb_path) ==0:
                        dtb_path = d.getVar("DEPLOY_DIR_IMAGE")
                    fitimage_emit_section_dtb_overlay(d,fd,dtb_file,dtb_path)
         elif imgtype == 'fdtapply':
                imgsource = d.getVarFlag('FITIMAGE_SLOT_%s' % slot, 'file')
                dtbresult = path + d.getVarFlag('FITIMAGE_SLOT_%s' % slot, 'name')
                dtbcommand = ""
                for dtb in (imgsource or "").split():
                    dtb_path, dtb_file = os.path.split(dtb)
                    if len(dtb_path) ==0:
                        dtb_path = d.getVar("DEPLOY_DIR_IMAGE")
                    if len(dtbcommand) == 0 and dtb_file.endswith('.dtb'):
                        dtbcommand = "fdtoverlay -i " + dtb_path + "/" + dtb_file + " -o " + dtbresult
                    elif dtb_file.endswith('.dtbo'):
                        dtbcommand = dtbcommand + " " + dtb_path + "/" + dtb_file
                if os.system(dtbcommand):
                    bb.error("fdtoverlay error: " + dtbcommand)
                dtb_path, dtb_file = os.path.split(dtbresult)
                DTBS = DTBS + " " + dtb_file
                fitimage_emit_section_dtb(d,fd,dtb_file,dtb_path)
         elif imgtype == 'ramdisk':
            ramdiskcount = "1"
            if slotflags and 'fstype' in slotflags:
               img_fstype = slotflags.get('fstype')
            img_file = "%s-%s.%s" % (d.getVar('FITIMAGE_SLOT_%s' % slot), machine, img_fstype)
            img_path = d.getVar("DEPLOY_DIR_IMAGE")
            fitimage_emit_section_ramdisk(d,fd,img_file,img_path)
         elif imgtype == 'bootscript':
            if bootscriptid:
                bb.fatal("Only 1 boot script supported (already set to: %s)" % bootscriptid)
            if slotflags and 'file' in slotflags:
                imgsource = d.getVarFlag('FITIMAGE_SLOT_%s' % slot, 'file')
            imgpath = d.getVar("DEPLOY_DIR_IMAGE")
            bootscriptid = imgsource
            fitimage_emit_section_bootscript(d,fd,imgpath,imgsource)
         elif imgtype == 'tee':
            teecount = teecount + 1
            imgsource = d.getVarFlag('FITIMAGE_SLOT_%s' % slot, 'file')
            if slotflags and 'comp' in slotflags:
               imgcomp = d.getVarFlag('FITIMAGE_SLOT_%s' % slot, 'comp')
            else:
               imgcomp = "none"
            imgpath = d.getVar("DEPLOY_DIR_IMAGE")
            fitimage_emit_section_tee(d,fd,teecount,imgpath,imgsource,imgcomp)
    fitimage_emit_section_maint(d,fd,'sectend')
    #
    # Step 5: Prepare a configurations section
    #
    fitimage_emit_section_maint(d,fd,'confstart')
    i = 1
    for dtb in (DTBS or "").split():
        fitimage_emit_section_config(d,fd,dtb,teecount,kernelcount,ramdiskcount,setupcount,bootscriptid,i)
        i = i +1
    for dtb in (DTBOS or "").split():
        fitimage_emit_section_config_fdto(d,fd,dtb)

    fitimage_emit_section_maint(d,fd,'sectend')
    fitimage_emit_section_maint(d,fd,'fitend')
    fd.close()

do_unpack:append() {
    write_manifest(d)
}

do_fitimagebundle () {
    is_pkcs11=0
    echo "${UBOOT_SIGN_KEYDIR}" | grep -q "^pkcs11:" && is_pkcs11=1
    if [ "${UBOOT_SIGN_ENABLE}" = "1" -a "${FITIMAGE_SIGN_ENGINE}" != "nxphab" ] ; then
        path_key=""
        engine=""
        if [ $is_pkcs11 -eq 0 ] ; then
            path_key="${UBOOT_SIGN_KEYDIR}"
        else
            # mkimage expects the URI without the pkcs11: prefix
            path_key=$(echo "${UBOOT_SIGN_KEYDIR}" | cut -c 8-)
            engine="-N pkcs11"
            setup_pkcs11_env
        fi

        printf '/dts-v1/;\n\n/ {\n};\n' > pubkey.dts
        dtc -O dtb pubkey.dts > pubkey.dtb
        mkimage ${engine} \
                -f "${S}/manifest.its" \
                -k "${path_key}" \
                -K "${B}/pubkey.dtb" \
                -r "${B}/fitImage"
    else
        mkimage \
            -f "${S}/manifest.its" \
            -r "${B}/fitImage"
    fi
}
do_fitimagebundle[dirs] = "${B}"
addtask fitimagebundle after do_configure before do_build

python do_signhab() {
    if d.getVar('UBOOT_SIGN_ENABLE') == '1' and d.getVar('FITIMAGE_SIGN_ENGINE') == 'nxphab':
        loadaddr = int(d.getVar('FITIMAGE_UBOOT_ENTRYPOINT'), 16)
        build_dir = d.getVar("B")
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

    its_file_name="${FITIMAGE_ITS_BASE_NAME}.its"
    its_symlink_name="${FITIMAGE_ITS_LINK_NAME}.its"
    printf 'Copying fit-image.its source file...'
    install -m 0644 ${S}/manifest.its ${DEPLOYDIR}/${its_file_name}

    printf 'Copying all created fdt from type fdtapply'
    count=`ls -1 ${S}/*.dtb 2>/dev/null | wc -l`
    if [ $count != 0 ]; then
        install -m 0644 ${S}/*.dtb ${DEPLOYDIR}/
    fi

    linux_bin_file_name="${FITIMAGE_IMAGE_BASE_NAME}${FITIMAGE_IMAGE_SUFFIX}"
    linux_bin_symlink_name="${FITIMAGE_IMAGE_LINK_NAME}${FITIMAGE_IMAGE_SUFFIX}"

    printf 'Copying fitImage file...'
    install -m 0644 ${B}/fitImage ${DEPLOYDIR}/${linux_bin_file_name}

    cd ${DEPLOYDIR}
    ln -sf ${its_file_name} ${its_symlink_name}
    ln -sf ${linux_bin_file_name} ${linux_bin_symlink_name}
}
addtask deploy after do_fitimagebundle before do_build

do_deploy[cleandirs] = "${DEPLOYDIR}"

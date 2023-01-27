FILESEXTRAPATHS:prepend := "${THISDIR}/barebox:"

SRC_URI:append:secureboot = " \
        file://dynamic-config.cfg \
"

DEPENDS:append:secureboot = " bison-native nxp-cst-native u-boot-mkimage-native dtc-native"

BAREBOX_BIN_SIGNED ?= "images/${BAREBOX_BASE_BIN}-s.img"
BAREBOX_BIN_SIGNEDUSB ?= "images/${BAREBOX_BASE_BIN}-us.img"

BAREBOX_LINK_NAME ?= "barebox"

BOOTLOADER_SIGN ??= "true"
BOOTLOADER_SIGN[type] = "boolean"
CERT_PATH ??= "${OEROOT}/../../phytec-dev-ca"
# for NXP HABv4 based systems
BOOTLOADER_SIGN_IMG_PATH ??= "${CERT_PATH}/nxp_habv4_pki/crts/IMG1_1_sha256_4096_65537_v3_usr_crt.pem"
BOOTLOADER_SIGN_CSF_PATH ??= "${CERT_PATH}/nxp_habv4_pki/crts/CSF1_1_sha256_4096_65537_v3_usr_crt.pem"
BOOTLOADER_SIGN_SRKFUSE_PATH ??= "${CERT_PATH}/nxp_habv4_pki/crts/SRK_1_2_3_4_table.bin"

FITIMAGE_SIGN ??= "true"
FITIMAGE_SIGN[type] = "boolean"
FITIMAGE_SIGN_KEY_PATH ??= "${CERT_PATH}/fit/FIT-4096.key"
FITIMAGE_HASH ??= "sha256"
FITIMAGE_SIGNATURE_ENCRYPTION ??= "rsa4096"
FITIMAGE_SIGNER ??= ""
FITIMAGE_SIGNER_VERSION ??= "${PN}"
FITIMAGE_PUBKEY_SIGNATURE_PATH ??= "${WORKDIR}/signature_node.dtsi"

#Create an empty device tree
def write_signature_node(d):
     import shutil

     path = d.getVar("WORKDIR")

     try:
        manifest = open('%s/signature_node.dts' % path, 'w')
     except OSError:
        raise bb.build.FuncFailed('Unable to open signature_node.dts')

     manifest.write('/dts-v1/;\n\n/ {\n};\n')
     manifest.close()

def write_signature_creation(d):
     import shutil
     import os
     path_manifest = d.getVar("WORKDIR")

     path, file = os.path.split(d.getVar("FITIMAGE_SIGN_KEY_PATH"))
     str_keynamehint = file.split('.')

     try:
        manifest = open('%s/signature_creation.its' % path_manifest, 'w')
     except OSError:
        raise bb.build.FuncFailed('Unable to open signature_creation.its')

     manifest.write('/dts-v1/;\n\n/ {\n')
     manifest.write('\t'        + 'images {\n')
     manifest.write('\t\t'      +   'fdt-1 {\n')
     manifest.write('\t\t\t'    +     'description = "Flattened Device Tree blob";\n')
     manifest.write('\t\t\t'    +     'data = /incbin/("./signature_node.dtb");\n')
     manifest.write('\t\t\t'    +     'type = "flat_dt";\n')
     manifest.write('\t\t\t'    +     'arch = "arm";\n')
     manifest.write('\t\t\t'    +      'compression = "none";\n')
     manifest.write('\t\t\t'    +      'hash-1 {\n')
     manifest.write('\t\t\t\t'  +        'algo = "%s";\n' % d.getVar("FITIMAGE_HASH"))
     manifest.write('\t\t\t'    +      '};\n')
     manifest.write('\t\t'      +   '};\n')
     manifest.write('\t'        + '};\n')
     manifest.write('\t'        + 'configurations {\n')
     manifest.write('\t\t'      +   'default = "conf-1";\n')
     manifest.write('\t\t'      +   'conf-1 {\n')
     manifest.write('\t\t\t'    +      'description = "Boot Linux kernel with FDT blob";\n')
     manifest.write('\t\t\t'    +      'fdt = "fdt-1";\n')
     manifest.write('\t\t\t'    +      'signature-1 {\n')
     manifest.write('\t\t\t\t'  +        'algo = "%s,%s";\n' % (d.getVar("FITIMAGE_HASH"), d.getVar("FITIMAGE_SIGNATURE_ENCRYPTION")))
     manifest.write('\t\t\t\t'  +        'key-name-hint = "%s";\n' % str_keynamehint[0])
     manifest.write('\t\t\t\t'  +        'sign-images = "fdt";\n')
     manifest.write('\t\t\t\t'  +        'signer = "%s";\n' %  d.getVar("FITIMAGE_SIGNER"))
     manifest.write('\t\t\t\t'  +        'signer-version = " %s";\n' % d.getVar("FITIMAGE_SIGNER_VERSION"))
     manifest.write('\t\t\t'    +      '};\n')
     manifest.write('\t\t'      +   '};\n')
     manifest.write('\t'        + '};\n')
     manifest.write('};\n')
     manifest.close()

def write_command(name,out):
     import shlex, subprocess

     newenv = dict(os.environ)
     args = shlex.split(name)
     #p = subprocess.Popen(args, shell=True, env=newenv, stdout=subprocess.PIPE)
     if (len(out) > 0):
         file = open(out,'w')
         p = subprocess.Popen(args,stdout=file)
         file.close
     else:
          p = subprocess.Popen(args, stdout=subprocess.PIPE)
          #p = subprocess.Popen(args, shell=True, env=newenv, stdout=subprocess.PIPE)
          stdout, stderr = p.communicate()
          output = stdout.decode("utf-8")
          return "%s" % (output)


def check_fitimage_keyring(d):
    # check for problematic certificate setups
    shasum = write_command("sha256sum " + d.getVar("FITIMAGE_SIGN_KEY_PATH"),'')
    if ((len(shasum) > 0) and (shasum.split(' ',1)[0] == "6f92252aab834bbe8090e92c44f051b2c40db8e3953c8c26c04c14e7ae2db7d8")) or \
        ((len(shasum) > 0) and (shasum.split(' ',1)[0] == "1e3eb95fe6a7d1e45db761bff6eedafb9291661480e1a1ad10eb6f5b8b9961c1")):
            bb.warn("!! CRITICAL SECURITY WARNING: You're using Phytec's Development Keyring for Secure Boot in the fit-image. Please create your own!!")


def check_bootloader_keyring(d):
    # check for problematic certificate setups
    shasumSRK=write_command("sha256sum "+ d.getVar("BOOTLOADER_SIGN_SRKFUSE_PATH"),'')
    if ((len(shasumSRK) >0) and (shasumSRK.split(' ',1)[0] == "0d5dbc6ed8b0a55414648b19727e217453c54d1527cef3a62784ae818c9777e7")):
        bb.warn("!! CRITICAL SECURITY WARNING: You're using Phytec's Development Keyring for Secure Boot in the bootloader. Please create your own!!")

do_patch:append:secureboot() {
    import os
    import subprocess

    pathCFG = d.getVar("WORKDIR") + "/dynamic-config.cfg"

    if not os.path.exists(d.getVar("WORKDIR")):
        os.makedirs(d.getVar("WORKDIR"))

    # noch Verzeinis Pruefen ansonsten anlegen
    file = open(pathCFG,"w")

    file.write("# CONFIG_CMD_OF_OVERLAY is not set\n")
    file.write("# CONFIG_NET_NETCONSOLE is not set\n")

    if oe.data.typed_value("BOOTLOADER_SIGN", d):
        check_bootloader_keyring(d)
        #activated HAB
        file.write("CONFIG_HAB=y\n")
        file.write("CONFIG_HABV4=y\n")
        file.write('CONFIG_HABV4_TABLE_BIN="%s"\n' % d.getVar("BOOTLOADER_SIGN_SRKFUSE_PATH"))
        file.write('CONFIG_HABV4_CSF_CRT_PEM="%s"\n' % d.getVar("BOOTLOADER_SIGN_CSF_PATH"))
        file.write('CONFIG_HABV4_IMG_CRT_PEM="%s"\n' % d.getVar("BOOTLOADER_SIGN_IMG_PATH"))
        #add hab command for burning fuses
        file.write("CONFIG_CMD_HAB=y\n")
        #signing images
        file.write("CONFIG_HABV4_IMAGE_SIGNED=y\n")
        file.write("CONFIG_HABV4_IMAGE_SIGNED_USB=y\n")
        #no loading of environment from the flash and use compile in environment
        file.write("# CONFIG_ENV_HANDLING is not set\n")
        file.write("CONFIG_DEFAULT_ENVIRONMENT=y\n")
        file.write("# CONFIG_CMD_SAVEENV is not set\n")
        file.write("# CONFIG_CMD_LOADENV is not set\n")


    if oe.data.typed_value("FITIMAGE_SIGN", d):
        check_fitimage_keyring(d)
        file.write("CONFIG_FITIMAGE=y\n")
        file.write("CONFIG_FITIMAGE_SIGNATURE=y\n")
        file.write("CONFIG_BOOTM_FITIMAGE=y\n")
        file.write("CONFIG_BOOTM_FITIMAGE_SIGNATURE=y\n")
        file.write("CONFIG_BOOTM_FORCE_SIGNED_IMAGES=y\n")
        file.write('CONFIG_BOOTM_FITIMAGE_PUBKEY="%s"\n' % d.getVar("FITIMAGE_PUBKEY_SIGNATURE_PATH") )
        file.write("CONFIG_CRYPTO_RSA=y\n")
        file.write("CONFIG_SHA1=y\n")
        file.write("CONFIG_SHA256=y\n")
        file.write("CONFIG_DIGEST_SHA1_GENERIC=y\n")
        file.write("CONFIG_DIGEST_SHA256_GENERIC=y\n")
        file.write("# CONFIG_CMD_GO is not set\n")

        if bb.utils.contains("MACHINE_FEATURES", "optee", True, False, d):
            file.write("CONFIG_BOOTM_OPTEE=y\n")

    file.close()
}

python do_create_dynamic_dtree(){
}

python do_create_dynamic_dtree:append:secureboot (){
    import os

    pathDD = d.getVar("WORKDIR")
    if not os.path.exists(pathDD):
        os.makedirs(pathDD)

    #Create Pubkey Signature for Barebox
    if oe.data.typed_value("FITIMAGE_SIGN", d):
        write_signature_node(d)
        write_signature_creation(d)
        write_command("dtc -O dtb " +  pathDD + "/signature_node.dts",  pathDD + "/signature_node.dtb")
        path, file = os.path.split(d.getVar("FITIMAGE_SIGN_KEY_PATH"))
        write_command("mkimage -f " +  pathDD + "/signature_creation.its -k " + path + " -K " + pathDD + "/signature_node.dtb -r " + pathDD + "/dummy.img", '')
        write_command("dtc -I dtb " + pathDD + "/signature_node.dtb", pathDD + "/signature_node.tmp")
        os.sync()
        with open(pathDD + "/signature_node.tmp", 'r') as fin:
            data = fin.readlines()
        with open(d.getVar("FITIMAGE_PUBKEY_SIGNATURE_PATH"), 'w') as fout:
            for line in data :
                if "dts-v1" not in line:
                    fout.write(line)
        os.sync()
}

do_create_dynamic_dtree[depends] += "\
    dtc-native:do_populate_sysroot \
    u-boot-mkimage-native:do_populate_sysroot \
"
addtask do_create_dynamic_dtree before do_configure after do_patch

do_install:append:secureboot () {
    if [ "${PN}" = "barebox" ] ; then
        bbnote "Installing signed barebox image on target rootfs"
        install ${B}/${BAREBOX_BIN_SIGNED} ${D}${base_bootdir}/${BAREBOX_IMAGE_NAME}-s.bin
        ln -sf ${BAREBOX_IMAGE_NAME}-s.bin ${D}${base_bootdir}/${BAREBOX_LINK_NAME}-s.bin
        install ${B}/${BAREBOX_BIN_SIGNEDUSB} ${D}${base_bootdir}/${BAREBOX_IMAGE_NAME}-us.bin
        ln -sf ${BAREBOX_IMAGE_NAME}-us.bin ${D}${base_bootdir}/${BAREBOX_LINK_NAME}-us.bin
    fi
}

do_deploy:append:secureboot () {
    if [ "${PN}" = "barebox" ] ; then
        bbnote "Deploying signed barebox"
        install -m 644 ${B}/${BAREBOX_BIN_SIGNED} ${DEPLOYDIR}/${BAREBOX_IMAGE_NAME}-s.bin
        ln -sf ${BAREBOX_IMAGE_NAME}-s.bin ${DEPLOYDIR}/${BAREBOX_LINK_NAME}-s.bin

        bbnote "Deploying signed USB barebox"
        install -m 644 ${B}/${BAREBOX_BIN_SIGNEDUSB} ${DEPLOYDIR}/${BAREBOX_IMAGE_NAME}-us.bin
        ln -sf ${BAREBOX_IMAGE_NAME}-us.bin ${DEPLOYDIR}/${BAREBOX_LINK_NAME}-us.bin
    fi
}

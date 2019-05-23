FILESEXTRAPATHS_prepend := "${THISDIR}/barebox:"

SRC_URI_append = " \
        file://dynamic-config.cfg \
"

DEPENDS += "nxp-cst-native dtc-native u-boot-mkimage-native"

#Create an empty device tree
def write_signature_node(d):
     import shutil

     path = d.getVar("WORKDIR", True)

     try:
        manifest = open('%s/signature_node.dts' % path, 'w')
     except OSError:
        raise bb.build.FuncFailed('Unable to open signature_node.dts')

     manifest.write('/dts-v1/;\n\n/ {\n};\n')
     manifest.close()

def write_signature_creation(d):
     import shutil
     import os
     path_manifest = d.getVar("WORKDIR", True)

     path, file = os.path.split(d.getVar("FITIMAGE_SIGN_KEY_PATH", True))
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
     manifest.write('\t\t\t'    +      'hash@1 {\n')
     manifest.write('\t\t\t\t'  +        'algo = "%s";\n' % d.getVar("FITIMAGE_HASH", True))
     manifest.write('\t\t\t'    +      '};\n')
     manifest.write('\t\t'      +   '};\n')
     manifest.write('\t'        + '};\n')
     manifest.write('\t'        + 'configurations {\n')
     manifest.write('\t\t'      +   'default = "conf-1";\n')
     manifest.write('\t\t'      +   'conf-1 {\n')
     manifest.write('\t\t\t'    +      'description = "Boot Linux kernel with FDT blob";\n')
     manifest.write('\t\t\t'    +      'fdt = "fdt-1";\n')
     manifest.write('\t\t\t'    +      'signature-1 {\n')
     manifest.write('\t\t\t\t'  +        'algo = "%s,%s";\n' % (d.getVar("FITIMAGE_HASH", True), d.getVar("FITIMAGE_SIGNATURE_ENCRYPTION", True)))
     manifest.write('\t\t\t\t'  +        'key-name-hint = "%s";\n' % str_keynamehint[0])
     manifest.write('\t\t\t\t'  +        'sign-images = "fdt";\n')
     manifest.write('\t\t\t\t'  +        'signer = "%s";\n' %  d.getVar("FITIMAGE_SIGNER", True))
     manifest.write('\t\t\t\t'  +        'signer-version = " %s";\n' % d.getVar("FITIMAGE_SIGNER_VERSION", True))
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
        shasum = write_command("sha256sum " +  d.getVar("FITIMAGE_SIGN_KEY_PATH",True),'')
        if (len(shasum) > 0) and \
           (shasum.split(' ',1)[0] == "fda2863c40b971a6909ff5c278d27988dc14361d10920299c51e9a1a163984dc") :
                bb.warn("!! CRITICAL SECURITY WARNING: You're using Phytec's Development Keyring for Secure Boot in the fit-image. Please create your own!!")


def check_bootloader_keyring(d):
        # check for problematic certificate setups
        shasumIMG=write_command("sha256sum " + d.getVar("BAREBOX_SIGN_IMG_PATH", True),'')
        shasumCSF=write_command("sha256sum " + d.getVar("BAREBOX_SIGN_CSF_PATH", True),'')
        shasumSRK=write_command("sha256sum " + d.getVar("BAREBOX_SIGN_SRKFUSE_PATH", True),'')
        if ((len(shasumIMG) > 0) and (shasumIMG.split(' ',1)[0] == "e8cb6f5c1aa9bdeb6229ecf370657ff07a04cdde844398ed1608b133869c207f")) or \
           ((len(shasumCSF) >0) and (shasumCSF.split(' ',1)[0] == "4a88f679dee78597ef7204e6e134047e6428ed83d8b66fd894dbe208a199f6b8")) or \
           ((len(shasumSRK) >0) and (shasumSRK.split(' ',1)[0] == "0d5dbc6ed8b0a55414648b19727e217453c54d1527cef3a62784ae818c9777e7")):
                bb.warn("!! CRITICAL SECURITY WARNING: You're using Phytec's Development Keyring fore Secure Boot in the bootloader. Please create your own!!")

#python do_create_dynamic_cfg () {
do_patch_append() {
    import os
    import subprocess

    pathCFG = d.getVar("WORKDIR", True) + "/dynamic-config.cfg"

    if not os.path.exists(d.getVar("WORKDIR", True)):
        os.makedirs(d.getVar("WORKDIR", True))

    # noch Verzeinis Pruefen ansonsten anlegen
    file = open(pathCFG,"w")

    #if (d.getVar("BAREBOX_SIGN", True) == "1"):
    if oe.data.typed_value("BAREBOX_SIGN", d):
        check_bootloader_keyring(d)
        file.write("CONFIG_HAB=y\n")
        file.write("CONFIG_HABV4=y\n")
        file.write('CONFIG_HABV4_TABLE_BIN="%s"\n' % d.getVar("BAREBOX_SIGN_SRKFUSE_PATH", True))
        file.write('CONFIG_HABV4_CSF_CRT_PEM="%s"\n' % d.getVar("BAREBOX_SIGN_CSF_PATH", True))
        file.write('CONFIG_HABV4_IMG_CRT_PEM="%s"\n' % d.getVar("BAREBOX_SIGN_IMG_PATH", True))
        file.write("CONFIG_CMD_HAB=y\n")


    #if (d.getVar("FITIMAGE_SIGN", True) == "1"):
    if oe.data.typed_value("FITIMAGE_SIGN", d):
        check_fitimage_keyring(d)
        file.write("CONFIG_FITIMAGE=y\n")
        file.write("CONFIG_FITIMAGE_SIGNATURE=y\n")
        file.write("CONFIG_BOOTM_FITIMAGE=y\n")
        file.write("CONFIG_BOOTM_FITIMAGE_SIGNATURE=y\n")
        file.write("CONFIG_BOOTM_FORCE_SIGNED_IMAGES=y\n")
        file.write('CONFIG_BOOTM_FITIMAGE_PUBKEY="%s"\n' % d.getVar("FITIMAGE_PUBKEY_SIGNATURE_PATH", True) )
        file.write("CONFIG_CRYPTO_RSA=y\n")
        file.write("CONFIG_SHA1=y\n")
        file.write("CONFIG_SHA256=y\n")
        file.write("CONFIG_DIGEST_SHA1_GENERIC=y\n")
        file.write("CONFIG_DIGEST_SHA256_GENERIC=y\n")
        file.write("# CONFIG_CMD_GO is not set\n")

    file.close()
}
#addtask do_create_dynamic_cfg before do_configure

python do_create_dynamic_dtree (){
    import os

    pathDD = d.getVar("WORKDIR", True)
    if not os.path.exists(pathDD):
        os.makedirs(pathDD)

    #Create Pubkey Signature for Barebox
    #if (d.getVar("FITIMAGE_SIGN", True) == "1"):
    if oe.data.typed_value("FITIMAGE_SIGN", d):
        write_signature_node(d)
        write_signature_creation(d)
        write_command("dtc -O dtb " +  pathDD + "/signature_node.dts",  pathDD + "/signature_node.dtb")
        path, file = os.path.split(d.getVar("FITIMAGE_SIGN_KEY_PATH", True))
        write_command("mkimage -f " +  pathDD + "/signature_creation.its -k " + path + " -K " + pathDD + "/signature_node.dtb -r " + pathDD + "/dummy.img", '')
        write_command("dtc -I dtb " + pathDD + "/signature_node.dtb", d.getVar("FITIMAGE_PUBKEY_SIGNATURE_PATH", True))

}
addtask do_create_dynamic_dtree before do_configure after do_patch



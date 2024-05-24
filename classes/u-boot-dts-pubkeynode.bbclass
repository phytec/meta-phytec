# Class for creating a pubkey node in the bootloader device-tree
# to verify a signed fitImage

LICENSE = "MIT"

inherit signing-helpers
DEPENDS:append:secureboot = " u-boot-mkimage-native dtc-native libp11-native"

FITIMAGE_SIGN ??= "true"
FITIMAGE_SIGN[type] = "boolean"
FITIMAGE_SIGN_ENGINE ??= "software"
FITIMAGE_SIGN_ENGINE:mx8m-nxp-bsp ??= "nxphab"
FITIMAGE_SIGN_KEY_PATH ??= "${CERT_PATH}/fit/FIT-4096.key"
FITIMAGE_HASH ??= "sha256"
FITIMAGE_SIGNATURE_ENCRYPTION ??= "rsa4096"
FITIMAGE_SIGNER ??= ""
FITIMAGE_SIGNER_VERSION ??= "${PN}"
FITIMAGE_PUBKEY_SIGNATURE_PATH ??= "${WORKDIR}/signature_node.dtsi"

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

    try:
        manifest = open('%s/signature_creation.its' % path_manifest, 'w')
    except OSError:
        raise bb.build.FuncFailed('Unable to open signature_creation.its')

    key_name_hint = get_key_name_hint(d.getVar("FITIMAGE_SIGN_KEY_PATH"))
    manifest.write('/dts-v1/;\n\n/ {\n')
    manifest.write('\tdescription = "Signature blob";\n')
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
    manifest.write('\t\t\t\t'  +        'key-name-hint = "%s";\n' % key_name_hint)
    manifest.write('\t\t\t\t'  +        'sign-images = "fdt";\n')
    manifest.write('\t\t\t\t'  +        'signer = "%s";\n' %  d.getVar("FITIMAGE_SIGNER"))
    manifest.write('\t\t\t\t'  +        'signer-version = " %s";\n' % d.getVar("FITIMAGE_SIGNER_VERSION"))
    manifest.write('\t\t\t'    +      '};\n')
    manifest.write('\t\t'      +   '};\n')
    manifest.write('\t'        + '};\n')
    manifest.write('};\n')
    manifest.close()

def exec_command(cmd):
    import shlex
    import subprocess
    result = subprocess.run(shlex.split(cmd), capture_output=True, encoding='UTF-8')
    if result.returncode != 0:
        bb.fatal("Failed to run cmd:", cmd, "\nstderr: ", result.stderr, "\nstdout:", result.stdout)
    return result.stdout

def check_fitimage_keyring(d):
    # check for problematic certificate setups
    path = d.getVar("FITIMAGE_SIGN_KEY_PATH")
    if not path.startswith("pkcs11:"):
        shasum = exec_command("sha256sum " + path)
        if ((len(shasum) > 0) and (shasum.split(' ',1)[0] == "6f92252aab834bbe8090e92c44f051b2c40db8e3953c8c26c04c14e7ae2db7d8")) or \
            ((len(shasum) > 0) and (shasum.split(' ',1)[0] == "1e3eb95fe6a7d1e45db761bff6eedafb9291661480e1a1ad10eb6f5b8b9961c1")):
                bb.warn("!! CRITICAL SECURITY WARNING: You're using Phytec's Development Keyring for Secure Boot in the fit-image. Please create your own!!")

# Creates a device-tree containing a /signature node with a pubkey
# to verify FIT-images with the bootloader
python do_create_dynamic_dtree(){
}

python do_create_dynamic_dtree:append:secureboot() {
    import os

    workdir = d.getVar("WORKDIR")
    if not os.path.exists(workdir):
        os.makedirs(workdir)

    if oe.data.typed_value("FITIMAGE_SIGN", d) and d.getVar('FITIMAGE_SIGN_ENGINE') == 'software':
        write_signature_node(d)
        write_signature_creation(d)

        signature_node_path_dts = os.path.join(workdir, "signature_node.dts")
        signature_node_path_dtb = os.path.join(workdir, "signature_node.dtb")
        signature_node_path_tmp = os.path.join(workdir, "signature_node.tmp")

        exec_command("dtc -O dtb {0} -o {1}".format(signature_node_path_dts, signature_node_path_dtb))

        key_path = d.getVar("FITIMAGE_SIGN_KEY_PATH")
        key_path = d.getVar("FITIMAGE_SIGN_KEY_PATH")

        engine = ""

        if key_path.startswith("pkcs11:"):
            engine = "-N pkcs11"

            setup_pkcs11_env(d)

        path = get_mkimage_key_path(key_path)

        exec_command("mkimage {0} -f {1} -k {2} -K {3} -r {4}".format(engine, workdir + "/signature_creation.its",      path, signature_node_path_dtb, workdir + "/dummy.img"))

        exec_command("dtc -I dtb {0} -o {1}".format(signature_node_path_dtb,  signature_node_path_tmp))

        os.sync()
        with open(signature_node_path_tmp, 'r') as fin:
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

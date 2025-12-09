# Class for creating a pubkey node in the bootloader device-tree
# to verify a signed fitImage

LICENSE = "MIT"

inherit signing-helpers

FITIMAGE_SIGN_ENGINE ??= "software"
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

def exec_command(cmd):
    import shlex
    import subprocess
    result = subprocess.run(shlex.split(cmd), capture_output=True, encoding='UTF-8')
    if result.returncode != 0:
        bb.fatal("Failed to run cmd:", cmd, "\nstderr: ", result.stderr, "\nstdout:", result.stdout)
    return result.stdout

def check_fitimage_keyring(d):
    # check for problematic certificate setups
    key_path = d.getVar("UBOOT_SIGN_KEYDIR")
    if not key_path.startswith("pkcs11:"):
        path = f"{key_path}/{d.getVar('UBOOT_SIGN_KEYNAME')}.key"
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

    if d.getVar("UBOOT_SIGN_ENABLE") == "1" and d.getVar('FITIMAGE_SIGN_ENGINE') != 'nxphab':
        write_signature_node(d)

        signature_node_path_dts = os.path.join(workdir, "signature_node.dts")
        signature_node_path_dtb = os.path.join(workdir, "signature_node.dtb")
        signature_node_path_tmp = os.path.join(workdir, "signature_node.tmp")

        exec_command("dtc -O dtb {0} -o {1}".format(signature_node_path_dts, signature_node_path_dtb))

        path = d.getVar("UBOOT_SIGN_KEYDIR")

        if path.startswith("pkcs11:"):
            path = path[7:]
            setup_pkcs11_env(d)

        if d.getVar('FIT_SIGN_INDIVIDUAL') == "1":
            exec_command(f"mkimage {d.getVar('UBOOT_MKIMAGE_SIGN_ARGS')} \
                -f auto \
                -k {path} \
                -g {d.getVar('UBOOT_SIGN_IMG_KEYNAME')} \
                -o {d.getVar('FIT_HASH_ALG')},{d.getVar('FIT_SIGN_ALG')} \
                -K {signature_node_path_dtb} \
                -d /dev/null \
                -r {workdir + '/dummy.img'}")

        exec_command(f"mkimage {d.getVar('UBOOT_MKIMAGE_SIGN_ARGS')} \
            -f auto-conf \
            -k {path} \
            -g {d.getVar('UBOOT_SIGN_KEYNAME')} \
            -o {d.getVar('FIT_HASH_ALG')},{d.getVar('FIT_SIGN_ALG')} \
            -K {signature_node_path_dtb} \
            -d /dev/null \
            -r {workdir + '/dummy.img'}")

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
    ${@bb.utils.contains('DISTRO_FEATURES', 'secureboot', 'libp11-native:do_populate_sysroot', '', d)} \
"
addtask do_create_dynamic_dtree before do_patch

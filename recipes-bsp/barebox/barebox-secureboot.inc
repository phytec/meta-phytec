FILESEXTRAPATHS:prepend := "${THISDIR}/barebox:"

SRC_URI:append:secureboot = " \
        file://dynamic-config.cfg \
"

DEPENDS:append:secureboot = " bison-native nxp-cst-native libp11-native"

BAREBOX_BIN_SIGNED ?= "images/${BAREBOX_BASE_BIN}-s.img"
BAREBOX_BIN_SIGNEDUSB ?= "images/${BAREBOX_BASE_BIN}-us.img"

BAREBOX_LINK_NAME ?= "barebox"

# for NXP HABv4 based systems
BOOTLOADER_SIGN_IMG_PATH ??= "${CERT_PATH}/nxp_habv4_pki/crts/IMG1_1_sha256_4096_65537_v3_usr_crt.pem"
BOOTLOADER_SIGN_CSF_PATH ??= "${CERT_PATH}/nxp_habv4_pki/crts/CSF1_1_sha256_4096_65537_v3_usr_crt.pem"
BOOTLOADER_SIGN_SRKFUSE_PATH ??= "${CERT_PATH}/nxp_habv4_pki/crts/SRK_1_2_3_4_table.bin"
BOOTLOADER_HABV4_SRK_INDEX ??= "0"

inherit u-boot-dts-pubkeynode

def check_bootloader_keyring(d):
    # check for problematic certificate setups
    path = d.getVar("BOOTLOADER_SIGN_SRKFUSE_PATH")
    if not path.startswith("pkcs11:"):
        shasumSRK=exec_command("sha256sum "+ path)
        if ((len(shasumSRK) >0) and (shasumSRK.split(' ',1)[0] == "0d5dbc6ed8b0a55414648b19727e217453c54d1527cef3a62784ae818c9777e7")):
            bb.warn("!! CRITICAL SECURITY WARNING: You're using Phytec's Development Keyring for Secure Boot in the bootloader. Please create your own!!")

do_patch:append:secureboot() {
    import os
    import subprocess

    # Only create dynamic-config for barebox package, other packages (targettools and hosttools)
    # which include barebox recipe don't need this.
    if d.getVar('PN') == "barebox":
        workdir = d.getVar("WORKDIR")
        if not os.path.exists(workdir):
            os.makedirs(workdir)

        pathCFG = os.path.join(workdir, "dynamic-config.cfg")
        file = open(pathCFG,"w")

        file.write("# CONFIG_CMD_OF_OVERLAY is not set\n")
        file.write("# CONFIG_NET_NETCONSOLE is not set\n")

        check_bootloader_keyring(d)
        #activated HAB
        file.write("CONFIG_HAB=y\n")
        file.write("CONFIG_HABV4=y\n")
        file.write('CONFIG_HABV4_SRK_INDEX=%s\n' % d.getVar("BOOTLOADER_HABV4_SRK_INDEX"))
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

        if d.getVar("UBOOT_SIGN_ENABLE") == "1":
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

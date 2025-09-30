inherit signing-helpers

do_assemble_fitimage:prepend:secureboot() {
    if echo "${UBOOT_SIGN_KEYDIR}" | grep -q "pkcs11:"; then
        setup_pkcs11_env
    fi
}

do_assemble_fitimage_initramfs:prepend:secureboot() {
    if echo "${UBOOT_SIGN_KEYDIR}" | grep -q "pkcs11:"; then
        setup_pkcs11_env
    fi
}

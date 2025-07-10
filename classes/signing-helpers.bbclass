def setup_pkcs11_env(d):
    import os
    os.environ["OPENSSL_ENGINES"] = d.getVar("WORKDIR") + "/recipe-sysroot-native/usr/lib/engines-3"
    os.environ["PKCS11_MODULE_PATH"] = d.getVar("PKCS11_MODULE_PATH")


setup_pkcs11_env() {
    engines_dir=`basename $( openssl version -e | sed -e 's/.*"\(.*\)".*/\1/g')`
    export OPENSSL_ENGINES="${WORKDIR}/recipe-sysroot-native/usr/lib/${engines_dir}"
    export PKCS11_MODULE_PATH="${PKCS11_MODULE_PATH}"
}

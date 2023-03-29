def get_key_name_hint(path):
    import re
    if path.startswith("pkcs11:"):
        labels=re.findall(".*object=([^;]*)", path)
        if len(labels) == 0:
            bb.fatal("Error: A label, specified by 'object=label', is required in a PKCS#11 URI")
        return labels[0]
    basename = os.path.basename(path)
    # It's expected without trailing extensions
    return basename.split(".")[0]

def get_mkimage_key_path(path):
    import re
    if not path.startswith("pkcs11:"):
        return os.path.dirname(path) # ordinary key path on the file system
    return path[7:] # strip "pkcs11:" because mkimag expects it without this prefix

def setup_pkcs11_env(d):
    import os
    os.environ["OPENSSL_ENGINES"] = d.getVar("WORKDIR") + "/recipe-sysroot-native/usr/lib/engines-3"
    os.environ["PKCS11_MODULE_PATH"] = d.getVar("PKCS11_MODULE_PATH", True)
    os.environ["CST_KEY_SOURCE"] = d.getVar("CST_KEY_SOURCE", True)


setup_pkcs11_env() {
    engines_dir=`basename $( openssl version -e | sed -e 's/.*"\(.*\)".*/\1/g')`
    export OPENSSL_ENGINES="${WORKDIR}/recipe-sysroot-native/usr/lib/${engines_dir}"
    export PKCS11_MODULE_PATH="${PKCS11_MODULE_PATH}"
    export CST_KEY_SOURCE="${CST_KEY_SOURCE}"
}

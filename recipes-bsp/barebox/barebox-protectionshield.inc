EXTRAPATHS:prepend := "${THISDIR}/barebox:"

SRC_URI:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'protectionshield','file://set-password.cfg', '', d)} \
"

PROTECTION_SHIELD_ROOT_PASSWORD ??= "root"

def set_root_password(d, hashvalue):
    pathS = d.getVar("S", True)
    fn = "passwdfile"
    pathFN = pathS + "/" + fn
    if not os.path.exists(pathS):
        os.makedirs(pathS)
    try:
        manifest = open(pathFN,'w')
    except OSError:
        raise bb.build.FuncFailed('Unable to open passwdfile')
    manifest.write(hashvalue)
    manifest.close()

def set_shield_password(d):
    import hashlib
    passwd = d.getVar('PROTECTION_SHIELD_ROOT_PASSWORD', True) or ""
    hashvalue = hashlib.sha512(passwd.encode()).hexdigest()
    set_root_password(d,hashvalue)


do_patch:append:shieldlow (){
    set_shield_password(d)
}

do_patch:append:shieldmedium (){
    set_shield_password(d)
}

python do_patch:append:shieldhigh () {
    import binascii
    #random hash
    hashvalue = binascii.hexlify(os.urandom(64)).decode('ascii')
    set_root_password(d,hashvalue)
}

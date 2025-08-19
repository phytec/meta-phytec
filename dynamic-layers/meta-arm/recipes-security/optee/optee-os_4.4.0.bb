FILESEXTRAPATHS:prepend := "${THISDIR}/optee-os-4.4.0:"
require recipes-security/optee/optee-os.inc

SRCREV = "8f645256efc0dc66bd5c118778b0b50c44469ae1"

SRC_URI += " \
    file://0003-optee-enable-clang-support.patch \
"

# Diff between inc file from 4.1.0 to 4.4.0
EXTRA_OEMAKE:remove = "NOWERROR=1"
INSANE_SKIP:${PN}:remove = "buildpaths"

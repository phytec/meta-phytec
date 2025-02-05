DESCRIPTION = "Code Signing Tool - used for signing images for IMX platforms"

SRCREV = "ca55059b5c9bff5ca27809c4f8d56cbdc8b9ceee"
BRANCH = "debian/unstable"
require nxp-cst.inc

INSANE_SKIP:${PN} += "already-stripped"

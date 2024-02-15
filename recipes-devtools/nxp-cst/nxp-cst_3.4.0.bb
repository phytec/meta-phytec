DESCRIPTION = "The link to the download.phytec.de server is only a \
        placeholder. Please download the NXP cst tool from \
        https://www.nxp.com/webapp/Download?colCode=IMX_CST_TOOL \
        and store it as tar file in the yocto download folder"

LICENSE = "Proprietary"

LIC_FILES_CHKSUM = " \
    file://LICENSE.bsd3;md5=14aba05f9fa6c25527297c8aac95fcf6 \
    file://LICENSE.hidapi;md5=e0ea014f523f64f0adb13409055ee59e \
    file://LICENSE.openssl;md5=3441526b1df5cc01d812c7dfc218cea6 \
"
SRCREV = "ca55059b5c9bff5ca27809c4f8d56cbdc8b9ceee"
BRANCH = "debian/unstable"
require nxp-cst.inc

INSANE_SKIP:${PN} += "already-stripped"

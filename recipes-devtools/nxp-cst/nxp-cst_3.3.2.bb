DESCRIPTION = "The link to the download.phytec.de server is only a \
        placeholder. Please download the NXP cst tool from \
        https://www.nxp.com/webapp/Download?colCode=IMX_CST_TOOL \
        and store it as tar file in the yocto download folder"

LICENSE = "Proprietary"

LIC_FILES_CHKSUM = " \
    file://LICENSE.bsd3;md5=1fbcd66ae51447aa94da10cbf6271530 \
    file://LICENSE.hidapi;md5=e0ea014f523f64f0adb13409055ee59e \
    file://LICENSE.openssl;md5=06698624268f7be8151210879bbcbcab \
"
SRCREV = "a981faeb029dd5b7ee3e5edfa26339a204f25e65"
BRANCH = "debian/unstable"
require nxp-cst.inc

INSANE_SKIP:${PN} += "already-stripped"

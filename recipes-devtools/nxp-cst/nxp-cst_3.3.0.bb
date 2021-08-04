DESCRIPTION = "The link to the download.phytec.de server is only a \
        placeholder. Please download the NXP cst tool from \
        https://www.nxp.com/webapp/Download?colCode=IMX_CST_TOOL \
        and store it as tar file in the yocto download folder"

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = " \
    file://release/LICENSE.bsd3;md5=1fbcd66ae51447aa94da10cbf6271530 \
    file://release/LICENSE.hidapi;md5=e0ea014f523f64f0adb13409055ee59e \
    file://release/LICENSE.openssl;md5=06698624268f7be8151210879bbcbcab \
"

require nxp-cst.inc

SRC_URI = " \
        https://download.phytec.de/cst-${PV}.tar;name=tarball \
"

SRC_URI[tarball.md5sum] = "6c2d4c26d9da23f3014a8a26850b316f"
SRC_URI[tarball.sha256sum] = "097a70ad6233225053c51281c220179a7c72275e1075bdf3373651367a69276c"

INSANE_SKIP:${PN} += "already-stripped"

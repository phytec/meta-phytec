DESCRIPTION = "Serial test application"
HOMEPAGE = "https://github.com/cbrake/linux-serial-test"
SECTION = "utils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSES/MIT;md5=544799d0b492f119fa04641d1b8868ed"
SRCREV = "4ff814f1fbdb5970046d040fb1d3ed7389125fbe"

SRC_URI = "git://github.com/cbrake/linux-serial-test.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit cmake

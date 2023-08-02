DESCRIPTION = "Serial test application"
HOMEPAGE = "https://github.com/cbrake/linux-serial-test"
SECTION = "utils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSES/MIT;md5=544799d0b492f119fa04641d1b8868ed"
SRCREV = "2ee61484167eab846f7b7c565284d7c350d738d3"

SRC_URI = "git://github.com/cbrake/linux-serial-test.git;branch=master;protocol=git"

S = "${WORKDIR}/git/"

inherit cmake

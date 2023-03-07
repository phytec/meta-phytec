DESCRIPTION = "This tool is intended for reading from and writing to PHYTEC SOM EEPROM chips. \
Use of this tool requires a properly-formatted configuration file for each \
target PHYTEC platform (PCM-057.yml for PCM-057 boards, for example). \
By default, this tool looks for configuration files in a 'configs' subdirectory \
to where the script is currently located."
HOMEPAGE = "https://github.com/phytec/phytec-eeprom-flashtool"
SECTION = "devel"

S="${WORKDIR}/${BPN}-${PV}"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING.MIT;md5=1117940313875d7598ccbb05f20129a7"

SRC_URI = "https://github.com/phytec/phytec-eeprom-flashtool/archive/refs/tags/v${PV}.tar.gz"

SRC_URI[md5sum] = "ed8d28a9406c3417c6687cad6e259bc0"
SRC_URI[sha256sum] = "376ed751f905f41b0816dce541d13672b53883d6741d4032070f8bc804efffbb"

do_install () {
        install -d ${D}${bindir}/${BPN}
        install -m 0644 README.rst ${D}${bindir}/${BPN}
        install -d ${D}${bindir}/${BPN}/tool
        install -m 0755 src/phytec_eeprom_flashtool.py ${D}${bindir}/${BPN}/tool
        install -d ${D}${bindir}/${BPN}/configs
        install -m 0644 configs/* ${D}${bindir}/${BPN}/configs
}

RDEPENDS:${PN} = "python3-pyyaml"
RDEPENDS:${PN} += "python3-crc8"

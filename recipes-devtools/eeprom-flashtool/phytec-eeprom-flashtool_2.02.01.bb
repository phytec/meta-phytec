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

SRC_URI[md5sum] = "862d3cff70e2f609a190fc945d1084be"
SRC_URI[sha256sum] = "ac05996f5261646dad3b203c72374482d7055a664d6fd5f8576a20e5765adb35"

do_install () {
        install -d ${D}${bindir}/${BPN}
        install -m 0644 README.rst ${D}${bindir}/${BPN}
        install -d ${D}${bindir}/${BPN}/tool
        install -m 0755 src/phytec_eeprom_flashtool.py ${D}${bindir}/${BPN}/tool
        install -d ${D}${bindir}/${BPN}/configs
        install -m 0644 configs/* ${D}${bindir}/${BPN}/configs
}

RDEPENDS_${PN} = "python3-pyyaml"
RDEPENDS_${PN} += "python3-crc8"

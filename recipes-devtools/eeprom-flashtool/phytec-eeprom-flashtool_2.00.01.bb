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

SRC_URI[md5sum] = "68dbe79e16c149c138bff9b8fb600600"
SRC_URI[sha256sum] = "2ab49bc9bfa91f707dcb5fac83f169da5781f151ab116b8da9086205ce2ae2c8"

do_install () {
        install -d ${D}${bindir}/${BPN}
        install -m 0644 README ${D}${bindir}/${BPN}
        install -m 0755 phytec_eeprom_flashtool.py ${D}${bindir}/${BPN}
        install -d ${D}${bindir}/${BPN}/configs
        install -m 0644 configs/* ${D}${bindir}/${BPN}/configs
}

RDEPENDS_${PN} = "python3-pyyaml"

COMPATIBLE_MACHINE = "am57xx"

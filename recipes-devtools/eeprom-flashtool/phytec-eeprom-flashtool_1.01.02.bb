DESCRIPTION = "This tool is intended for reading from and writing to PHYTEC SOM EEPROM chips. \
Use of this tool requires a properly-formatted configuration file for each \
target PHYTEC platform (PCM-057.yml for PCM-057 boards, for example). \
By default, this tool looks for configuration files in a 'configs' subdirectory \
to where the script is currently located."
HOMEPAGE = "www.phytec.com"
SECTION = "devel"

S="${WORKDIR}/git"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING.MIT;md5=1117940313875d7598ccbb05f20129a7"

SRC_URI = "git://git.phytec.de/phytec-eeprom-flashtool.git"
SRCREV = "b562fc6e40e53b5b59f8085d0d79267adfe36d0f"

do_install () {
        install -d ${D}${bindir}/${PN}
        install -m 0644 ${S}/README ${D}${bindir}/${PN}
        install -m 0755 ${S}/phytec_eeprom_flashtool.py ${D}${bindir}/${PN}
        install -d ${D}${bindir}/${PN}/configs
        install -m 0644 ${S}/configs/* ${D}${bindir}/${PN}/configs
}

RDEPENDS_${PN} = "python3-pyyaml"

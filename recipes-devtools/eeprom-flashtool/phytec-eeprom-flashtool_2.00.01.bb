DESCRIPTION = "This tool is intended for reading from and writing to PHYTEC SOM EEPROM chips. \
Use of this tool requires a properly-formatted configuration file for each \
target PHYTEC platform (PCM-057.yml for PCM-057 boards, for example). \
By default, this tool looks for configuration files in a 'configs' subdirectory \
to where the script is currently located."
HOMEPAGE = "https://github.com/phytec/phytec-eeprom-flashtool"
SECTION = "devel"

S = "${UNPACKDIR}/${BPN}-${PV}"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING.MIT;md5=1117940313875d7598ccbb05f20129a7"

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI = " \
        git://github.com/phytec/${BPN}.git;protocol=https;branch=main \
        file://0001-configs-PCM-057-uprev-known-configuration-BOMs.patch \
        file://0001-phytec_eeprom_flashtool-Fix-for-pyyaml-6.0-kirkstone.patch \
"

SRCREV = "6e582ed3316d6492656ab9931c1f07c37c4a6937"


do_install () {
        install -d ${D}${bindir}/${BPN}
        install -m 0644 README ${D}${bindir}/${BPN}
        install -m 0755 phytec_eeprom_flashtool.py ${D}${bindir}/${BPN}
        install -d ${D}${bindir}/${BPN}/configs
        install -m 0644 configs/* ${D}${bindir}/${BPN}/configs
}

RDEPENDS:${PN} = "python3-pyyaml"

COMPATIBLE_MACHINE = "am57xx"

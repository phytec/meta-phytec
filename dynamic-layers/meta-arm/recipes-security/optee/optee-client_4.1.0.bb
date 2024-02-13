FILESEXTRAPATHS:prepend := "${OEROOT}/../meta-arm/meta-arm/recipes-security/optee/${PN}:"
require recipes-security/optee/optee-client.inc
require optee-phytec.inc

SRCREV = "f7e4ced15d1fefd073bbfc484fe0e1f74afe96c2"

inherit pkgconfig
DEPENDS += "util-linux"
EXTRA_OEMAKE += "PKG_CONFIG=pkg-config"

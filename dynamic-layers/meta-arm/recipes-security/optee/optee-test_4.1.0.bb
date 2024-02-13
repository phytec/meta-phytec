FILESEXTRAPATHS:prepend := "${OEROOT}/../meta-arm/meta-arm/recipes-security/optee/${PN}:"
require recipes-security/optee/optee-test.inc
require optee-phytec.inc

SRCREV = "2e1e7a9c9d659585566a75fc8802f4758c42bcb2"

EXTRA_OEMAKE:append = " OPTEE_OPENSSL_EXPORT=${STAGING_INCDIR}"
DEPENDS:append = " openssl"
CFLAGS:append = " -Wno-error=deprecated-declarations"

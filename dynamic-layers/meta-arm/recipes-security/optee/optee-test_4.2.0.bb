FILESEXTRAPATHS:prepend := "${OEROOT}/../meta-arm/meta-arm/recipes-security/optee/${PN}:"
require recipes-security/optee/optee-test.inc
require optee-phytec.inc

SRCREV = "526d5bac1b65f907f67c05cd07beca72fbab88dd"

EXTRA_OEMAKE:append = " OPTEE_OPENSSL_EXPORT=${STAGING_INCDIR}"
DEPENDS:append = " openssl"
CFLAGS:append = " -Wno-error=deprecated-declarations"

DESCRIPTION = "Bundle creator for rauc"
HOMEPAGE = "http://www.phytec.de"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/COPYING.MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit bundle

FILESEXTRAPATHS_prepend := "${THISDIR}/openssl:"

RAUC_BUNDLE_COMPATIBLE_ti33x ?= "${DISTRO}"
RAUC_BUNDLE_VERSION ?= "${DISTRO_VERSION}"

RAUC_BUNDLE_SLOTS ?= "rootfs kernel dtb"

RAUC_SLOT_rootfs ?= "phytec-headless-image"
RAUC_SLOT_rootfs[fstype] = "ubifs"

RAUC_SLOT_kernel ?= "${PREFERRED_PROVIDER_virtual/kernel}"
RAUC_SLOT_kernel[type] ?= "kernel"

RAUC_SLOT_dtb ?= "${PREFERRED_PROVIDER_virtual/kernel}"
RAUC_SLOT_dtb[type] ?= "file"
RAUC_SLOT_dtb[file] ?= "${KERNEL_DEVICETREE}"

# TODO Fix these pathes
RAUC_KEY_FILE ?= "${WORKDIR}/ca.key.pem"
RAUC_CERT_FILE ?= "${WORKDIR}/ca.cert.pem"

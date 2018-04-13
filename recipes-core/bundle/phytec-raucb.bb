# Copyright (C) 2018 Daniel Schultz <d.schultz@phytec.de>
# Released under the MIT license (see COPYING.MIT for the terms)

inherit bundle

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append := " \
    file://ca.key.pem \
    file://ca.cert.pem \
"


RAUC_BUNDLE_COMPATIBLE ?= "BSP-Yocto-${SOC_FAMILY}"
RAUC_BUNDLE_VERSION ?= "${DISTRO_VERSION}"

RAUC_BUNDLE_SLOTS ?= "rootfs kernel dtb"

RAUC_SLOT_rootfs ?= "phytec-headless-image"
RAUC_SLOT_rootfs[fstype] = "ubifs"

RAUC_SLOT_kernel ?= "${PREFERRED_PROVIDER_virtual/kernel}"
RAUC_SLOT_kernel[type] ?= "kernel"

RAUC_SLOT_dtb ?= "${PREFERRED_PROVIDER_virtual/kernel}"
RAUC_SLOT_dtb[type] ?= "file"
RAUC_SLOT_dtb[file] ?= "${KERNEL_DEVICETREE}"

# TODO Fix these paths
RAUC_KEY_FILE ?= "ca.key.pem"
RAUC_CERT_FILE ?= "ca.cert.pem"


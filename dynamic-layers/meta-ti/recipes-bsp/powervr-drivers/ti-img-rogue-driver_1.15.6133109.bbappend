FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI_append = "\
    file://0001-build-Use-aarch64-linux-gnu-as-target-for-all-vendor.patch \
"

COMPATIBLE_MACHINE_append = "|phyboard-izar-am68x-1"
TARGET_PRODUCT_phyboard-izar-am68x-1 = "j721s2_linux"

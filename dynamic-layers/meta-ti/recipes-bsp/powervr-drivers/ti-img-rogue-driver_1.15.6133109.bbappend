FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI_append = "\
    file://0001-build-Use-aarch64-linux-gnu-as-target-for-all-vendor.patch \
"

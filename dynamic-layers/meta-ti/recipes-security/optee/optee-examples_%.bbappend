FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
EXTRA_OEMAKE_append_am62xx = " LIBGCC_LOCATE_CFLAGS='--sysroot=${STAGING_DIR_HOST}'"

# Avoid QA Issue: No GNU_HASH in the elf binary
INSANE_SKIP_${PN} += "ldflags"

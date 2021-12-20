#
# Usage of bbclass patch-auto-append:
#
#    # In a bbappend
#    inherit patch-auto-append
#    PATCH_AUTO_APPEND_DIRS:prepend := "${THISDIR}/${BPN}"
#    FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"
#
# NOTES:
#   - Use := instead of +=. Otherwise ${THISDIR} is not resolved to directory
#     of bbappend.
#   - Every path in PATCH_AUTO_APPEND_DIRS must also be in FILESEXTRAPATHS.
#     Bitbake won't find the patches.

PATCH_AUTO_APPEND_DIRS ??= ""

# bbclass internal:

def _list_patches_in_dirs(d):
    import glob, os
    dirs = d.getVar("PATCH_AUTO_APPEND_DIRS").split()
    src_uri_patches = []
    for dir in dirs:  # Use order of directory in PATCH_AUTO_APPEND_DIRS
        for filename in sorted(glob.glob(os.path.join(dir, "*.patch"))):
            if os.path.isfile(filename):
                src_uri_patches.append("file://" + os.path.basename(filename))

    return " ".join(src_uri_patches)

SRC_URI:append = " ${@_list_patches_in_dirs(d)}"

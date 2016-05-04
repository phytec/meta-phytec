#
# Usage of bbclass patch-auto-append:
#
#    # In a bbappend
#    inherit patch-auto-append
#    PATCH_AUTO_APPEND_DIRS_prepend := "${THISDIR}/${PN}"
#    FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
#
# NOTES:
#   - Use := instead of +=. Otherwise ${THISDIR} is not resolved to directory
#     of bbappend.
#   - Every path in PATCH_AUTO_APPEND_DIRS must also be in FILESEXTRAPATHS.
#     Bitbake won't find the patches.

PATCH_AUTO_APPEND_DIRS ??= ""

# bbclass internal:

def _list_patches_in_dirs(d):
    import os
    # TODO improve whitespace parsing '.strip(" ")' will not catch all cases.
    dirs = d.getVar("PATCH_AUTO_APPEND_DIRS", True).strip(" ").split(" ")
    src_uri_patches = []
    for dir in dirs:  # Use order of directory in PATCH_AUTO_APPEND_DIRS
        for filename in (f for f in os.listdir(dir)):
            path = os.path.join(dir, filename)
            print(path, filename)

            if os.path.isfile(path) and filename.endswith(".patch"):
                # TODO Escape evil characters ;-)
                src_uri_patches.append("file://" + filename)

    return " ".join(src_uri_patches)

SRC_URI_append = " ${@_list_patches_in_dirs(d)}"

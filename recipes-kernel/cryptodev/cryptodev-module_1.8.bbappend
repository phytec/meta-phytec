FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "\
	file://0001-Adjust-to-another-change-in-the-user-page-API.patch \
"

# For the i.MX8 Alpha release we are using the NXP meta layer. This layers
# are add this patch allready. So we have to remove them for the i.MX8 boards

SRC_URI_remove_mx8 = " file://0001-Adjust-to-another-change-in-the-user-page-API.patch"

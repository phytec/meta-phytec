FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append := " \
	file://0001-tools-mkfwumdata-manage-bank-accepted-entry.patch \
"

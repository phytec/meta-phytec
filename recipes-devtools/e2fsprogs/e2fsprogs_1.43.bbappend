FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
	file://0001-e2fsck-exit-with-exit-status-0-if-no-errors-were-fix.patch \
"

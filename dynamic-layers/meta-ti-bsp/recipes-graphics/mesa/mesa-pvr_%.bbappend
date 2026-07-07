# mesa-pvr-25.inc in meta-ti skips dev-so for "${PN}-megadriver", but
# the package is named mesa-megadriver, so the symlinks fail do_package_qa.
# Drop once fixed in meta-ti.
INSANE_SKIP:mesa-megadriver += "dev-so"

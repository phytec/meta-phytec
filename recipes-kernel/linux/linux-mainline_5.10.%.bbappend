RDEPENDS_${KERNEL_PACKAGE_NAME}-base += "\
    ${@bb.utils.contains("DISTRO_FEATURES","secureboot", "" , "phytec-dt-overlays", d)} \
"

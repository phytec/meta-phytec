# Copyright (C) 2014 Stefan Mueller-Klieser <s.mueller-klieser@phytec.de>
# PHYTEC Messtechnik GmbH
SUMMARY = "Apollo Test System Config File Generator"
HOMEPAGE = "http://www.phytec.de"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING.GPLv2;md5=751419260aa954499f7abaabaa882bbe"
SECTION = "devel"

SRC_URI = "file://COPYING.GPLv2"
S = "${WORKDIR}"

inherit deploy

do_fetch() {
}
do_configure() {
}
do_compile() {
}
do_install() {
}

APOLLO_CFG_FILE = "${DEPLOYDIR}/Apollo-${BSP_VERSION}-${MACHINE}.tcl"

do_deploy() {
    bbnote "Deploying Apollo Config File"
    install -d ${DEPLOYDIR}
    #note: continue line operator and $ needs to be escaped
    tee ${APOLLO_CFG_FILE} <<EOF
################################################################################
#  Package:     ---                                                            #
#  Namespace:   phyTEST::Settings::Target                                      #
#  Description: Target configuration file for ${MACHINE}            #
#  Procedures:  ---                                                            #
################################################################################

namespace eval ::phyTEST::Settings::Target \\
{
 variable module   "${MACHINE}"
 variable version  "${BSP_VERSION}"
 variable kit      "Standard"

 variable images_dir [file join \$phyTEST::Settings::Host::application_tftpboot_dir \\
                                \$module \$version \$kit]

 variable xloader_image "${BAREBOX_IPL_BIN_DEPLOY}"
 variable uboot_image   [file join \$images_dir ${BAREBOX_BIN_DEPLOY}]
 variable kernel_image  [file join \$images_dir ${KERNEL_IMAGE_BASE_NAME}.bin]
 variable rootfs_image  [file join \$images_dir ${IMAGE_NAME}.rootfs.ubifs]
 variable oftree_image  "${KERNEL_DEVICETREE}"

 variable supported_displays  [list \\
                               [list "N/A"  "No display configuration required"] \\
                              ]

 variable supported_cameras   [list \\
                               [list "N/A"         "Camera not supported"] \\
                              ]

 # U-Boot
 variable uboot_v1_prompt "uboot> "
 variable uboot_v1_uboot_location 0xa0000000
 variable uboot_v1_env_location "0xa0020000"
 variable uboot_v1_ram_address "${UBOOT_LOADADDRESS}"
 variable uboot_v2_prompt "uboot:/"
 variable barebox_prompt  "barebox:/ "
 #variable barebox_prompt  "barebox.*?Phytec phyCORE-AM335x:/"
 #w                    ^[[1;32mbarebox@^[[1;31mPhytec phyCORE pcm049:/^[[0m
 variable uboot_v2_xloader_location    "nand"
 variable uboot_v2_bootloader_location "nand"
 variable uboot_v2_kernel_location     "nand"
 variable uboot_v2_rootfs_location     "nand"
 variable uboot_v2_oftree_location     "nand"

 # Linux
 variable login_prompt "${MACHINE} login: "
 variable shell_prompt "root@${MACHINE}"
 variable root_password ""

 variable bsp_test_cases [list            \\
                          "bootloader_devices"          \\
                         ]
EOF
    # tee EOF mechanism does not work with closing brackets
    echo "}" >> ${APOLLO_CFG_FILE}
}
addtask deploy
do_deploy[nostamp] = "1"

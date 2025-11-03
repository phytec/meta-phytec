FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit kernel-deploy-oftree

KBUILD_DEFCONFIG = "defconfig"
KBUILD_DEFCONFIG:arm = "multi_v7_defconfig"

SRC_URI:append:k3 = " \
	file://platform-k3.scc \
"

KERNEL_FEATURES:append:k3 = " \
	platform-k3.scc \
"

# Linux 6.18 introduced the Kconfig 'transitional' keyword,
# which is not supported by 'symbol_why.py' shipped with
# Scarthgap. Therefore, do_kernel_configcheck fails when
# using SCC files.
do_kernel_configcheck[noexec] = "1"

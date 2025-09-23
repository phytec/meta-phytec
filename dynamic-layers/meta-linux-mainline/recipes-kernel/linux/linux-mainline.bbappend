FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit kernel-deploy-oftree

KBUILD_DEFCONFIG = "defconfig"

SRC_URI:append:k3 = " \
	file://platform-k3.scc \
"

KERNEL_FEATURES:append:k3 = " \
	platform-k3.scc \
"
KBUILD_DEFCONFIG:arm = "multi_v7_defconfig"

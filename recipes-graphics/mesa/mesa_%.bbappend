FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

PACKAGE_ARCH_mx6 = "${MACHINE_SOCARCH}"

PACKAGECONFIG[kmsro] = ""

GALLIUMDRIVERS_remove ="${@bb.utils.contains('PACKAGECONFIG', 'imx', ',imx', '', d)}"
GALLIUMDRIVERS_append ="${@bb.utils.contains('PACKAGECONFIG', 'kmsro', ',kmsro', '', d)}"

# use Etnaviv gallium driver for imxgpu3d machines
# gpu needs to be covered by MACHINE_SOCARCH
PACKAGECONFIG_append_imxgpu3d = " gallium kmsro"
GALLIUMDRIVERS_imxgpu3d = "etnaviv,vc4,swrast"

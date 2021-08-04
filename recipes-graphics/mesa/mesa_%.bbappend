FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

PACKAGE_ARCH:mx6 = "${MACHINE_SOCARCH}"

PACKAGECONFIG[kmsro] = ""

GALLIUMDRIVERS:remove ="${@bb.utils.contains('PACKAGECONFIG', 'imx', ',imx', '', d)}"
GALLIUMDRIVERS:append ="${@bb.utils.contains('PACKAGECONFIG', 'kmsro', ',kmsro', '', d)}"

# use Etnaviv gallium driver for imxgpu3d machines
# gpu needs to be covered by MACHINE_SOCARCH
PACKAGECONFIG:append:imxgpu3d = " gallium kmsro"
GALLIUMDRIVERS:imxgpu3d = "etnaviv,vc4,swrast"

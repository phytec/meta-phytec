FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

PACKAGE_ARCH:mx6-generic-bsp = "${MACHINE_SOCARCH}"

# use Etnaviv gallium driver for imxgpu3d machines
# gpu needs to be covered by MACHINE_SOCARCH
PACKAGECONFIG:append:imxgpu3d = " gallium kmsro"
GALLIUMDRIVERS:imxgpu3d = "etnaviv,vc4,swrast"

PACKAGECONFIG:append:am62xx = " imagination"

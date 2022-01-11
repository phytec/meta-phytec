FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

PACKAGE_ARCH_mx6 = "${MACHINE_SOCARCH}"

# use Etnaviv gallium driver for imxgpu3d machines
# gpu needs to be covered by MACHINE_SOCARCH
PACKAGECONFIG_append_imxgpu3d = " gallium kmsro"
GALLIUMDRIVERS_imxgpu3d = "etnaviv,vc4,swrast"

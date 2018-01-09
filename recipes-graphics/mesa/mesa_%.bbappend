PACKAGE_ARCH = "${MACHINE_SOCARCH}"

# use Etnaviv gallium driver for imxgpu3d machines
# gpu needs to be covered by MACHINE_SOCARCH
PACKAGECONFIG_append_imxgpu3d = " gallium"
GALLIUMDRIVERS_imxgpu3d = "etnaviv,imx"

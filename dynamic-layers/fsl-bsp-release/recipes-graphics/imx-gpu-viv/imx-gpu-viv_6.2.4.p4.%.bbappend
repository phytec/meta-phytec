# https://source.codeaurora.org/external/imx/meta-fsl-bsp-release
# on thud-4.19.35-1.0.0 this will be done with commit
# 18f708993 imx-gpu-viv-v6.inc: Align with the community changes

# git://git.yoctoproject.org/meta-freescale
# on thud this will be done with commit
# 692cd2f2 imx-gpu-viv: libgl-imx-dev: remove conflicting rdepends

RDEPENDS_libgl-imx-dev_remove = "libgl-mesa-dev"

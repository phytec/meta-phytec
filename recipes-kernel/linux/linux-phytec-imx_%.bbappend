# Load moal before btnxpuart since loading order matters for NXP IW612.
# URL: https://github.com/Freescale/meta-freescale/issues/1837
KERNEL_MODULE_PROBECONF += "btnxpuart"
module_conf_btnxpuart = "softdep btnxpuart pre: moal"

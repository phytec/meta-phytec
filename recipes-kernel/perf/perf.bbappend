# perf changed dependencies on python to pyhton3,
# because the upstream kernel can now handle this.
# The support for that was added in kernel v4.17-rc1.
# As long as we use an older kernel, we can:
# 1) disable scripting PACKAGECONFIG
# 2) backport kernel patch to our kernel (if we need scripting)
# 3) undo perf recipe changes / host our own (if we need scripting)
# For now we simply disable scripting PACKAGECONFIG
PACKAGECONFIG_remove_mx6 = "scripting"
PACKAGECONFIG_remove_mx6ul = "scripting"
PACKAGECONFIG_remove_mx8 = "scripting"
PACKAGECONFIG_remove_rk3288 = "scripting"
PACKAGECONFIG_remove_ti33x = "scripting"

RDEPENDS_${PN}-tests += "bash"

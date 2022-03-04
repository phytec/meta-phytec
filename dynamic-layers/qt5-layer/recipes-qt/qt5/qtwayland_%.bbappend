# etnaviv mesa does not have glx
PACKAGECONFIG:remove:mx6 = "xcomposite-glx"
PACKAGECONFIG:remove:mx8m-generic-bsp = "wayland-vulkan-server-buffer"

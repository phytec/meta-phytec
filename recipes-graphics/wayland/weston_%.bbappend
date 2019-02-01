PACKAGECONFIG_rk3288 = " wayland kms fbdev egl systemd launch clients"
PACKAGECONFIG_rk3288[wayland] = "--enable-wayland-compositor,--disable-wayland-compositor"
PACKAGECONFIG_rk3288[kms] = "--enable-drm-compositor,--disable-drm-compositor,drm udev mtdev"
EXTRA_OECONF_append_rk3288 = "\
                WESTON_NATIVE_BACKEND=drm-backend.so \
                "

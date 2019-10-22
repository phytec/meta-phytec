PACKAGECONFIG_rk3288 = " wayland kms fbdev egl systemd launch clients"
EXTRA_OECONF_append_rk3288 = "\
                WESTON_NATIVE_BACKEND=drm-backend.so \
                "

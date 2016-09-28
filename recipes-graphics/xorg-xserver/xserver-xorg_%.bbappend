# Avoid dependency virtual/gl when buliding core-image-sato
PACKAGECONFIG_remove_mx6 = "dri"
PACKAGECONFIG_remove_mx6 = "glx"
PACKAGECONFIG_remove_ti33x = "dri"
PACKAGECONFIG_remove_ti33x = "glx"
PACKAGECONFIG_remove_rk3288 = "dri"
PACKAGECONFIG_remove_rk3288 = "glx"

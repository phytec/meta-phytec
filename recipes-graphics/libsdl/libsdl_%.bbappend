# Avoid dependency to mesa when building core-image-sato
DEPENDS_remove_mx6 = "libglu"
DEPENDS_remove_mx6 = "virtual/libgl"
DEPENDS_remove_ti33x = "libglu"
DEPENDS_remove_ti33x = "virtual/libgl"
DEPENDS_remove_rk3288 = "libglu"
DEPENDS_remove_rk3288 = "virtual/libgl"

# Enable fbcon
EXTRA_OECONF_remove = " --disable-video-fbcon"
EXTRA_OECONF_append = " --enable-video-fbcon"

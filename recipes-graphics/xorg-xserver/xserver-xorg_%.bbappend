# Avoid dependency virtual/gl when buliding core-image-sato
PACKAGECONFIG:remove:imx = "dri"
PACKAGECONFIG:remove:imx = "glx"
PACKAGECONFIG:remove:ti33x = "dri"
PACKAGECONFIG:remove:ti33x = "glx"
PACKAGECONFIG:remove:rk3288 = "dri"
PACKAGECONFIG:remove:rk3288 = "glx"

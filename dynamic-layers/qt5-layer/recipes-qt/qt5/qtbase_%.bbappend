
# The string 'gl' is in PACKAGECONFIG because "opengl" is in ampliphy's
# DISTRO_FEATURES. Since our boards only support egl/gles2 and not the full
# opengl, we have to disable gl and enable gles2 by hand here.
PACKAGECONFIG_GL:mx6ul-generic-bsp = "no-opengl linuxfb"
PACKAGECONFIG_GL:imxgpu3d = "gles2 eglfs gbm kms"
PACKAGECONFIG_GL:ti33x = "gles2 eglfs"
PACKAGECONFIG_GL:rk3288 = "gles2 eglfs kms"

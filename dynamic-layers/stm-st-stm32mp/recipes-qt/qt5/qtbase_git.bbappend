# If MACHINE is not equipped with GPU, use linuxfb
OPENGL = "${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'gles2', '', d)}"
PACKAGECONFIG_GL = "${@bb.utils.contains('MACHINE_FEATURES', 'gpu', '${OPENGL}', 'linuxfb', d)}"



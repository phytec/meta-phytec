# am62xx has a Rogue GPU core
PREFERRED_PROVIDER_virtual/egl_am62xx = "ti-img-rogue-umlibs"
PREFERRED_PROVIDER_virtual/libgles1_am62xx = "ti-img-rogue-umlibs"
PREFERRED_PROVIDER_virtual/libgles2_am62xx = "ti-img-rogue-umlibs"
PREFERRED_PROVIDER_virtual/libgbm_am62xx = "ti-img-rogue-umlibs"
PREFERRED_PROVIDER_virtual/gpudriver_am62xx = "ti-img-rogue-driver"

# Install modules in initramfs and roofs
PACKAGE_INSTALL_append = " kernel-modules"

INITRAMFS_FSTYPES = "wic.xz"

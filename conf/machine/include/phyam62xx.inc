# am62xx has a Rogue GPU core
PREFERRED_PROVIDER_virtual/egl:am62xx = "ti-img-rogue-umlibs"
PREFERRED_PROVIDER_virtual/libgles1:am62xx = "ti-img-rogue-umlibs"
PREFERRED_PROVIDER_virtual/libgles2:am62xx = "ti-img-rogue-umlibs"
PREFERRED_PROVIDER_virtual/libgbm:am62xx = "ti-img-rogue-umlibs"
PREFERRED_PROVIDER_virtual/gpudriver:am62xx = "ti-img-rogue-driver"

# Install modules in initramfs and roofs
PACKAGE_INSTALL:append = " kernel-modules"

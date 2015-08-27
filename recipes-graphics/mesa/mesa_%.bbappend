# Copyright (C) 2015 PHYTEC Messtechnik GmbH,
# Author: Stefan Christ <s.christ@phytec.de>

# Why does this work?
# The original provides of mesa are
#    PROVIDES="mesa virtual/libgl virtual/libgles1 virtual/libgles2 virtual/egl virtual/mesa"
# Qt5 will depend only on virtual/libgles2 and virtual/egl. Since we remove the
# provides from the mesa recipe, yocto selects our package "gpu-viv-bin-mx6q"
# for these dependences.
PROVIDES_remove_mx6 = "virtual/libgles2 virtual/egl virtual/libgles1"

# Avoid error while building 'core-image-sato' from poky
#   ERROR: Multiple .bb files are due to be built which each provide virtual/libgles1
#    ([...]/meta-phytec/recipes-graphics/libgles/libgles-omap3_5.01.01.02.bb
#     [...]/poky/meta/recipes-graphics/mesa/mesa_10.4.4.bb).
#    This usually means one provides something the other doesn't and should.
#   ERROR: Multiple .bb files are due to be built which each provide virtual/libgles2 ...
#   ERROR: Multiple .bb files are due to be built which each provide virtual/egl ...
#    This usually means one provides something the other doesn't and should.
PROVIDES_remove_ti33x = "virtual/libgles2 virtual/egl virtual/libgles1"

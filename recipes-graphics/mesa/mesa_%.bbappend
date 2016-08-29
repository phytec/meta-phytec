# Copyright (C) 2015 PHYTEC Messtechnik GmbH,
# Author: Stefan Christ <s.christ@phytec.de>

# Why does this work?
# The original provides of mesa are
#    PROVIDES="mesa virtual/libgl virtual/libgles1 virtual/libgles2 virtual/egl virtual/mesa"
# Qt5 will depend only on virtual/libgles2 and virtual/egl. Since we remove the
# provides from the mesa recipe, yocto selects our package "imx-gpu-viv"
# for these dependences.
PROVIDES_remove_mx6 = "virtual/libgles2 virtual/egl virtual/libgles1"
PROVIDES_remove_rk3288 = "virtual/libgles2 virtual/libgles1 virtual/egl"

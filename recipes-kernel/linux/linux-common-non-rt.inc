# Copyright (C) 2023 PHYTEC Messtechnik GmbH,
# Author: Daniel Schultz <d.schultz@phytec.de>
require recipes-kernel/linux/linux-common.inc

#Skip this recipe if DISTRO_FEATURES contains the PREEMPT-RT value and
# a kernel with real-time is desired
python () {
    if 'preempt-rt' in d.getVar("DISTRO_FEATURES"):
        raise bb.parse.SkipPackage("Disable 'preempt-rt' in DISTRO_FEATURES!")
}

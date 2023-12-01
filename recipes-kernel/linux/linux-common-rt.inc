# Copyright (C) 2023 PHYTEC Messtechnik GmbH,
# Author: Daniel Schultz <d.schultz@phytec.de>
require recipes-kernel/linux/linux-common.inc

#Skip this recipe if DISTRO_FEATURES doesn't contain the PREEMPT-RT value and
# a kernel without real-time is desired
python () {
    if 'preempt-rt' not in d.getVar("DISTRO_FEATURES"):
        raise bb.parse.SkipPackage("Enable 'preempt-rt' in DISTRO_FEATURES!")
}

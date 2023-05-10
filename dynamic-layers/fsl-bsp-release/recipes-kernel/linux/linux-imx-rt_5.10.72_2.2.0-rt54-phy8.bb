# Copyright (C) 2022 PHYTEC Messtechnik GmbH,
# Author: Christian Hemp <c.hemp@phytec.de>

require linux-imx_5.10.72_2.2.0-phy16.bb

BRANCH = "v5.10.72_2.2.0-rt54-phy"
GIT_URL = "git://git.phytec.de/linux-imx"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "362e6383116ee776b06bb2e190c68d96d27f9592"

INTREE_DEFCONFIG += "imx8_phytec_rt.config"

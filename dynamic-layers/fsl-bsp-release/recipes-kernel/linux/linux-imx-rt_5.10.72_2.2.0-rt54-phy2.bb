# Copyright (C) 2022 PHYTEC Messtechnik GmbH,
# Author: Christian Hemp <c.hemp@phytec.de>

require linux-imx_5.10.72_2.2.0-phy10.bb

BRANCH = "v5.10.72_2.2.0-rt54-phy"
GIT_URL = "git://git.phytec.de/linux-imx"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "4eaf47352505b92d276151b994c9a870f6060e84"

INTREE_DEFCONFIG += "imx8_phytec_rt.config"

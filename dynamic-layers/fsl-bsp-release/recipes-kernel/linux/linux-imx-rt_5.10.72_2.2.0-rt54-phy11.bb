# Copyright (C) 2022 PHYTEC Messtechnik GmbH,
# Author: Christian Hemp <c.hemp@phytec.de>

require linux-imx_5.10.72_2.2.0-phy18.bb

BRANCH = "v5.10.72_2.2.0-rt54-phy"
GIT_URL = "git://git.phytec.de/linux-imx"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "99c185fad9c8f6c148dfd3f93b13f0fd159765f4"

INTREE_DEFCONFIG += "imx8_phytec_rt.config"

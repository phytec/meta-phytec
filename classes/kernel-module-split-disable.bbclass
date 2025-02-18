# Copyright (C) 2015 PHYTEC Messtechnik GmbH,
# Author: Stefan Christ <s.christ@phytec.de>

# The package 'kernel-modules' (real name in KERNEL_MODULES_META_PACKAGE) is a
# empty package which depends on all kernel module packages.  Since the package
# 'kernel-modules' is installed often by default on the rootfs, all available
# kernel modules will be pulled onto the rootfs.
#
# If you want to install some kernel modules 'by hand', e.g. in a image recipe
# or package group, you can append the package name to the variable
#
#   KERNEL_MODULES_RDEPENDS_DISABLE
#
# This will remove the package from the RDEPENDS list of the package
# 'kernel_modules' and therefore the kernel module is not installed by default.

KERNEL_MODULES_RDEPENDS_DISABLE ??= ""


# The task 'split_kernel_module_packages' is defined in
# <poky>/meta/classes/kernel-module-split.bbclass.
python split_kernel_module_packages:append () {
    # Get all modules which shouldn't be in RDEPENDS.
    if modules:
        disabled_modules = set(s.strip() for s in
                                  d.getVar("KERNEL_MODULES_RDEPENDS_DISABLE").split(' '))

        # Remove packages in set disabled_modules from variable
        #    RDEPENDS:${KERNEL_MODULES_META_PACKAGE}.
        # After that the package 'kernel-modules' doesn't pull these packages onto
        # the rootfs automatically.
        kernel_modules = d.getVar("KERNEL_MODULES_META_PACKAGE")
        rdepends = d.getVar("RDEPENDS:%s" % (kernel_modules,))
        if rdepends is not None:
            rdepends = [pkg for pkg in rdepends.split(" ") if pkg not in disabled_modules]
            d.setVar("RDEPENDS:%s" % (kernel_modules), ' '.join(rdepends))

}

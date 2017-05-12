
BSP-Tools Documentation
=======================

Some documentation about the scripts and datastructures.


Script switch_machine.py
------------------------

It's possible to hide some machines from the user in the selectable machine
list. That's useful if you want to have machines in the BSP, which are not
fully supported for the general public., e.g. we only support a bootloader but
not the userland.

To hide a machine put the string '[hide]' into the @DESCRIPTION field of the
machine configuration. An Example:

    #@TYPE: Machine
    #@NAME: phyflex-imx6-3
    #@DESCRIPTION: PFL-A-02-xxxxxxx.xx/PBA-B-01 (i.MX6 Quad, 2GB RAM on two banks) [hide]
    #from http://www.phytec.de
    [...]

Note: An user can make hidden machines visible again, if he/she passes the
argument '--all' to the switch_machine.py script.

Script copy-deploy-images
-------------------------

The script copies the latest images files from the deploy/images directory.
It's useful for deploying images for a Release or KSP projects, because also it
creates checksums.  To deploy a release images execute

    $ ./for_all_machines.py bitbake phytec-qt5demo-image   # or
    $ MACHINE=ksp-machine-1  bitbake phytec-qt5demo-image  # and execute
    $ ./copy-deploy-images deploy/images ~/deploy/folder/images/

To check the files against the checksum after deployment, execute

    $ find ~/deploy/folder/images -name sha1sum.txt -execdir sha1sum -c sha1sum.txt ";" \
      | grep -v OK



Script wipe-deploy-images
-------------------------

Remove all images in the directory ${DEPLOY_DIR}/images. Use like

    $ . sources/poky/oe-init-build-env
    $ ../sources/meta-phytec/scripts/wipe-deploy-images

Useful to free harddisk space after a lot of builds.


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

    $ MACHINE=ksp-machine-1  bitbake phytec-headless-image
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


Script update-recipe.py
-----------------------

Update a linux, u-boot or barebox recipe to the newest tag on its
integration branch. The script queries the upstream repository for new
-phyN tags, renames the recipe accordingly, updates SRCREV and creates
a commit including the upstream changelog. Use like

    $ ./scripts/update-recipe.py recipes-kernel/linux/linux-phytec-ti_6.12.57-11.02.11-phy7.bb

Pass '-n'/'--dry-run' to only report what would change.

The script only handles bumps to a newer -phyN tag on the same
integration branch.

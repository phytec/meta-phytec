
BSP-Tools Documentation
=======================

Some documentation about the scripts and datastructures.


Script switch_machine.py
-------------------------

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

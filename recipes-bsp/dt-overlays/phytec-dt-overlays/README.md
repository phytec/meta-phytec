Devicetree Overlay Handling for PHYTEC Cameras on i.MX 6
========================================================

Introduction
------------

Since BSP-Yocto-i.MX6-PD21.1.0 Devicetree Overlays replace the
`of_camera_selection` tool from the Barebox to select the connected
camera module.


How to apply Devicetree Overlays
--------------------------------

Devicetree Overlays for the PHYTEC cameras can be applied from Linux
Userspace and from Barebox. When Overlays are selected by Linux
Userspace and Barebox, **only** the Linux Userspace setting is applied.

### Linux Userspace
In order to select which Devicetree Overlays will be applied during the
next boot process, write the selected Overlays in the first line of
`/overlays/select` separated by Spaces. The file does not exist by
default and needs to be created.

Note: The Barebox evaluates **only** the first line of this file. So
make sure to write all required overlays into the first line, separated
by Spaces.

A list of available Overlays can be found below.

This setting will overwrite settings selected in the Barebox. To be able
to select Overlays again from the Barebox simply delete `/overlays/select`.

### Barebox
In order to select Overlays from the Barebox simple write all required
Overlays into the non volatile `overlays.select` Variable and make sure
to save the environment afterwards.

	barebox$ nv overlays.select="imx6-vm010-bw-0.dtbo"
	barebox$ saveenv


Supported Cameras
-----------------

The naming for the camera overlays follows this schema:
   imx6-<camera>-<opt.color>-<CSIinterface>.dtbo

To use the phyCAM-S+ interface an additional overlay needs to be applied
with the camera overlay:
   imx6-cam-0-lvds.dtbo (CSI-0)
   imx6-cam-1-lvds.dtbo (CSI-1)


Supported Boards
----------------

Currently following Boards are supported:
 - phyBOARD-Mira
 - phyBOARD-Nunki
 - phyFLEX

meta-phytec
===========

This is the official hardware support layer for PHYTEC Boards.
Meta-phytec is a BSP layer, it is distribution independent and delivers
bootloader and kernel to our customers.
If you are interested in a fully tested and configured Linux
distribution, consider working with

  <https://git.phytec.de/meta-ampliphy>

which is PHYTEC's distribution based on Poky.

Releases are functional tested, supported and maintained. They can be
accessed by our repo manifest.

  <https://git.phytec.de/phy2octo>

We have a small wrapper to guide you through the release setup.

```
wget https://download.phytec.de/Software/Linux/Yocto/Tools/phyLinux
chmod +x ./phyLinux
./phyLinux init
```

Further information about the hardware can be found on our webpage:

  <https://www.phytec.de>

If you experience any problem with this software, please contact our
<support@phytec.de> or the maintainer directly.
Please try to do the following first:

* look in the
  [Yocto Project Bugzilla](http://bugzilla.yoctoproject.org/)
  to see if a problem has already been reported
* look through recent entries of the
  [Yocto mailing list archives](https://lists.yoctoproject.org/pipermail/yocto/)
  to see if other people have run into similar
  problems or had similar questions answered.

License
=======

This layer is distributed under the MIT license if not noted otherwise.
This includes all recipes, configuration files and meta data created by
Phytec. Source code included in the tree is distributed under the
license stated in the corresponding recipe or as mentioned in the code.
There is some work of others companies included or referenced.
Attribution is kept as required. The receipt meta data is mostly MIT,
if not noted otherwise. The binaries and code compiled for the target
rootfs is distributed under the vendors license. The licenses are
provided in the /licenses subdirectory to be collected by bitbake.
Please be aware that you need to agree to the specific vendor licenses
if you use the proprietary code for your product.

Maintainer
==========

M:  Stefan Müller-Klieser <s.mueller-klieser@phytec.de>  
M:  Norbert Wesp <n.wesp@phytec.de>

Dependencies
============

This layer depends on Openembedded-Core and Bitbake:  
<http://git.openembedded.org/bitbake>  
<http://git.openembedded.org/openembedded-core>

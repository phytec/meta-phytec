# meta-phytec

meta-phytec is a meta layer for The Yocto Project. It is a BSP layer and contains the official hardware support for PHYTEC Boards. It is independent of any distribution and delivers bootloader and kernel to our customers.

If you are interested in a fully tested and configured Linux distribution, consider working with [meta-ampliphy](https://git.phytec.de/meta-ampliphy) which is PHYTEC's distribution based on Poky.

## Releases
Releases of meta-phytec are functional tested, supported and maintained. They can be accessed by our repo manifest, using the google repo tool: [phy2octo](https://git.phytec.de/phy2octo)

We provide a small wrapper script to guide you through the release setup:

```bash
wget https://download.phytec.de/Software/Linux/Yocto/Tools/phyLinux
chmod +x ./phyLinux
./phyLinux init
```

Further information about the hardware supported by meta-phytec can be found on our [webpage](https://www.phytec.de).

## kas build configuration
The `kas` subfolder contains build configurations for the kas setup tool. These configurations are used in our CI or cover certain use cases, but they are not considered official releases. Releases are provided through our repo manifest repository.

You can find more information about the kas tool in the [kas documentation](https://kas.readthedocs.io/en/latest/).

Here are some example use cases covered by the kas configurations:

1. Build a Poky image for PHYTEC hardware:
```bash
kas build meta-phytec/kas/poky-phytec.yml
```

2. Work on a Poky upstream feature:
```bash
kas checkout meta-phytec/kas/poky-phytec.yml
# Perform work in the layers
kas build meta-phytec/kas/poky-phytec-local.yml
```

## Support
If you experience any problems with the software in this layer, please contact our [support](mailto:support@phytec.de) or the maintainer directly.

Before reaching out, please try the following:

- Read through the PHYTEC BSP documentation
- Build and deploy an unmodified release version
- Check the [Yocto Project Bugzilla](http://bugzilla.yoctoproject.org/) to see if the issue has already been reported
- Review recent entries in the [Yocto mailing list archives](https://lists.yoctoproject.org/pipermail/yocto/) for similar problems or questions

## Contributing
We welcome external contributions and are open to considering new use cases from our customers. To contribute, you can either open a pull request on GitHub or send an email using git send-email directly to our mailing list: <upstream@phytec.de>

## License
This layer is distributed under the MIT license, unless noted otherwise. This includes all recipes, configuration files, and metadata created by PHYTEC. Source code included in the tree is distributed under the license stated in the corresponding recipe or as mentioned in the code.

Some work from other companies is also included or referenced. Attribution is kept as required. The receipt metadata is mostly MIT-licensed, if not noted otherwise. The binaries and code compiled for the target rootfs are distributed under the vendors' licenses. The licenses are provided in the `/licenses` subdirectory to be collected by bitbake.

Please be aware that you need to agree to the specific vendor licenses if you use the proprietary code for your product.

## Maintainers
* Stefan Müller-Klieser <s.mueller-klieser@phytec.de>
* Norbert Wesp <n.wesp@phytec.de>

## Dependencies
This layer depends on Openembedded-Core and Bitbake:

* [bitbake](http://git.openembedded.org/bitbake)
* [openembedded-core](http://git.openembedded.org/openembedded-core)

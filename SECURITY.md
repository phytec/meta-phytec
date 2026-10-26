How to Report a Vulnerability
=============================

PHYTEC handles security issues through its Product Security Incident Response
Team (PSIRT). The reporting process is described on the
[PHYTEC PSIRT page](https://www.phytec.eu/en/support/psirt/).

Please report vulnerabilities in meta-phytec or in PHYTEC products to
<psirt@phytec.eu>. Encrypted communication is possible with the PSIRT's public
PGP key, available via Web Key Directory (WKD), which most PGP clients query
automatically.

To process a report, the PSIRT needs:

* description of the vulnerability
* date and time of discovery
* affected products and versions
* operating system of the affected component
* step-by-step instructions to reproduce the issue

For general problems, please contact <support@phytec.de>. If you have a
patch for a public issue ready, submit it following the same procedure
as for other patches, as described in README.md.

Development Keys and Certificates
---------------------------------

The secure boot and signing support in this layer (e.g. with the `secureboot`
distro feature) uses the development CA `phytec-dev-ca` by default, which is
provided by [meta-ampliphy](https://github.com/phytec/meta-ampliphy) and
published at <https://github.com/phytec/phytec-dev-ca>. Its private keys are
public and only meant for evaluating PHYTEC development kits.

Never use the development keys and certificates in products. Create your own
PKI and point `CERT_PATH` and the related signing variables to your own keys
before deploying devices. Devices signed or fused with the development keys
are not protected.

This is security by design and not by default, because we provide development
releases. Please note this and do not report it as a security incident.

Branches Maintained with Security Fixes
---------------------------------------

Security fixes are delivered through PHYTEC BSP LTS release streams. PHYTEC
recommends using the latest supported release. See the
[PHYTEC website](https://www.phytec.eu) for releases and their maintenance
status.

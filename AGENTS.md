# Agent Guide for meta-phytec

meta-phytec is a Yocto Project BSP layer providing bootloader and kernel
support for PHYTEC boards. It is normally built as part of a repo-managed
BSP workspace, not standalone.

## Build workspace

Builds use a workspace set up with `phyLinux init` (see README.md) from
the [phy2octo](https://git.phytec.de/phy2octo) manifests. Enter an
existing workspace with:

```sh
source sources/oe-core/oe-init-build-env
```

Rules:

- **Reuse the existing workspace**; never re-run `phyLinux init` or
  `repo sync`. Work on `sources/meta-phytec`.
- Honor pre-existing environment and configuration: do not override
  `DL_DIR`, `SSTATE_DIR`, `MACHINE`, or entries in `build/conf/*.conf`
  that are already set.
- Do not delete or re-create `build/`, `downloads/`, or `sstate-cache/`.

## Validation

Routinely, from the workspace build directory:

```sh
MACHINE=<machine> bitbake <recipe-you-changed>   # e.g. MACHINE=phyboard-lyra-am62xx-4 bitbake virtual/kernel
```

## Related repositories

Some changes must land together with other repositories:

- **phy2octo** (manifest): layer pins;
- **meta-ampliphy**: PHYTEC's distro layer; image and distro policy
  belongs there, not in this BSP layer.

## Commits

- One atomic change per commit; no fixup commits within a series —
  correct the patch that introduced the problem.
- Subject style: path-prefixed, e.g.
  `recipes-kernel: linux-phytec-ti: Update to v6.18.13-12.00.00.07-phy6`
  or `conf: machine: Weaken SERIAL_CONSOLES in the K3 includes`.
- Body explains the problem before describing the change.
- Every commit needs a `Signed-off-by` trailer taken from the local git
  configuration (`git config user.name` / `user.email`). Never fabricate
  a name or email.

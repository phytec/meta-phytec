# Warn when a PHYTEC TI recipe's TI SDK version doesn't match the pinned meta-ti.
# PV convention: "<component>-<ti-sdk>-phy<N>", e.g. "6.18.13-12.00.00.07-phy3".

def ti_recipe_sdk_version(d):
    try:
        component, ti_sdk_version, phy = (d.getVar('PV') or '').split('-')
    except ValueError:
        return None
    return ti_sdk_version

def ti_metati_head_tags(d):
    import os

    metatibase = d.getVar('METATIBASE')
    if not metatibase or not os.path.isdir(metatibase):
        bb.note("ti-sdk-version-check: meta-ti layer (METATIBASE) not found; skipping")
        return None

    try:
        out, _ = bb.process.run('git tag --points-at HEAD', cwd=metatibase)
    except (bb.process.ExecutionError, bb.process.NotFoundError) as e:
        bb.note("ti-sdk-version-check: cannot read meta-ti git tags (%s); skipping" % e)
        return None

    return out.split()

python () {
    sdk = ti_recipe_sdk_version(d)
    if sdk is None:
        bb.warn("ti-sdk-version-check: cannot derive a TI SDK version from PV '%s'; "
                "skipping" % d.getVar('PV'))
        return

    tags = ti_metati_head_tags(d)
    if tags is None:
        return
    if sdk not in tags:
        bb.warn("ti-sdk-version-check: %s targets TI SDK %s, but pinned meta-ti is %s"
                % (d.getVar('PN'), sdk, ', '.join(tags) or 'none'))
}

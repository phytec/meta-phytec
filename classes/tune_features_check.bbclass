# Allow checking of required and conflicting TUNE_FEATURES
#
# REQUIRED_TUNE_FEATURES: ensure every item on this list is included
#                           in TUNE_FEATURES.
# CONFLICT_TUNE_FEATURES: ensure no item in this list is included in
#                           TUNE_FEATURES.

python () {
    required_tune_features = d.getVar('REQUIRED_TUNE_FEATURES', True)
    if required_tune_features:
        required_tune_features = required_tune_features.split()
        tune_features = (d.getVar('TUNE_FEATURES', True) or "").split()
        for f in required_tune_features:
            if f in tune_features:
                continue
            else:
                raise bb.parse.SkipPackage("missing required tune feature '%s' (not in TUNE_FEATURES)" % f)

    conflict_tune_features = d.getVar('CONFLICT_TUNE_FEATURES', True)
    if conflict_tune_features:
        conflict_tune_features = conflict_tune_features.split()
        tune_features = (d.getVar('TUNE_FEATURES', True) or "").split()
        for f in conflict_tune_features:
            if f in tune_features:
                raise bb.parse.SkipPackage("conflicting tune feature '%s' (in TUNE_FEATURES)" % f)
}

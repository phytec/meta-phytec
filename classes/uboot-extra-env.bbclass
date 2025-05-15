# U-Boot extra environment handling

# The data file for environment is placed into the folder _ENV_DIR, so bitbake
# feature 'cleandirs' cleans the folder every time before starting the task
# 'do_env'. This garantuees a clean state and no stall information.
_ENV_DIR = "${WORKDIR}/.extraenv"
_ENV_FILE = "${_ENV_DIR}/extra.env"
do_env[cleandirs] += "${_ENV_DIR}"

def env_add(d, key, value):
    extra_env = d.getVar('_ENV_FILE')
    with open(extra_env, "a") as f:
        f.write('%s=%s\n' % (key, value))

python do_env() {
}
addtask do_env after do_fetch before do_configure

python do_build_env_str() {
    extra_env = d.getVar('_ENV_FILE')
    export_env = os.path.join(d.getVar('WORKDIR'), "export-extra-env.sh")

    # Read all non-empty lines, dedupe+preserve order
    with open(extra_env) as f:
        unique = list(dict.fromkeys(
            ln.strip() for ln in f
            if ln.strip()
        ))

    # build the null-separated string
    env = "\\0".join(unique) + "\\0"
    export = f"export KCPPFLAGS=\" -DCFG_EXTRA_ENV_SETTINGS='\\\"{env}\\\"' \""

    with open(export_env, "w") as f:
        f.write(export)
}
addtask do_build_env_str after do_env before do_configure

do_compile:prepend () {
    . ${WORKDIR}/export-extra-env.sh
}

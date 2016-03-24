
BAREBOX_COMMON_ENVDIR = "${WORKDIR}/commonenv"
BAREBOX_MACHINE_ENVDIR = "${WORKDIR}/machineenv"
BAREBOX_BOARD_ENVDIR = "${WORKDIR}/boardenv"

do_configure_append() {
    # Add environment directories to .config if they exist
    env_dirs=""
    for env_dir in .commonenv .boardenv .machineenv; do   # order is important!
        if test -d "${S}/$env_dir"; then
            env_dirs="$env_dirs $env_dir"
        fi
    done

    if test ! -z "$env_dirs"; then
        kconfig_set DEFAULT_ENVIRONMENT_PATH "\"$env_dirs\""
    fi
}

# its possible to give three environment folders to barebox: commonenv,
# boardenv and machineenv.
python do_prepare_env() {
    import shutil, subprocess
    S = d.getVar('S', True)
    bb.note("cleanup environment in source directory")
    source_env_dirs = [ oe.path.join(S, '.machineenv'),
                        oe.path.join(S, '.boardenv'),
                        oe.path.join(S, '.commonenv') ]
    for env_dir in source_env_dirs:
        if os.path.isdir(env_dir):
            oe.path.remove(env_dir)

    machine_env_dir = d.getVar('BAREBOX_MACHINE_ENVDIR', True)
    board_env_dir = d.getVar('BAREBOX_BOARD_ENVDIR', True)
    common_env_dir = d.getVar('BAREBOX_COMMON_ENVDIR', True)
    if os.path.isdir(common_env_dir):
        bb.note("copying common environment to source directory")
        oe.path.copytree(common_env_dir, oe.path.join(S, '.commonenv'))
    if os.path.isdir(board_env_dir):
        bb.note("copying board environment to source directory")
        oe.path.copytree(board_env_dir, oe.path.join(S, '.boardenv'))
    if os.path.isdir(machine_env_dir):
        bb.note("copying machine environment to source directory")
        oe.path.copytree(machine_env_dir, oe.path.join(S, '.machineenv'))
}
addtask prepare_env after do_patch before do_configure

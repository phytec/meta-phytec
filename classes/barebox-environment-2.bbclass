# Barebox Environment Handling - The Second version
#
# - Binary files in Environment are not supported. String must be Unicode
#   encodeable for JSON.

# Set to '1' if you want to see a dump of the environment in the build process.
ENV_VERBOSE ??= "0"

python do_env() {
    # Here add your env_add() and env_rm() calls via do_env_append or
    # do_env_prepend. Examples:
    #    env_add(d, "nv/linux.bootargs.rootfs", "rootwait ro fsck.repair=yes\n")
    #    env_rm(d, "nv/allow_color")
}
addtask env after do_patch before do_env_write

# BBClass internal function code:

do_configure_append() {
    # Add environment directory
    kconfig_set DEFAULT_ENVIRONMENT_PATH "\".env-extra\""
}

# The data file for environment is placed into the folder _ENV_DIR, so bitbake
# feature 'cleandirs' cleans the folder every time before starting the task
# 'do_env'. This garantuees a clean state and no stall information.
_ENV_DIR = "${WORKDIR}/env"
_ENV_FILE = "${_ENV_DIR}/json"
do_env[cleandirs] += "${_ENV_DIR}"

python do_env_write() {
    from os.path import join, dirname
    from shutil import rmtree
    env_path = join(d.getVar('S', True), ".env-extra")
    tree = _env_read(d)

    # Remove directory so there are no stall files
    try:
        rmtree(env_path)
    except OSError:
        # Ignore errors. E.g. file doesn't exists
        pass

    # create directory, so it exists hence there are not files in env
    os.makedirs(env_path)

    def do_tree(path, tree):
        for filename, tree_or_file in tree.items():
            path_of_file = join(path, filename)  # file or directory
            if isinstance(tree_or_file, dict):
                # Create directory
                os.makedirs(path_of_file)
                do_tree(path_of_file, tree_or_file)
            else:
                # Write file
                with open(path_of_file, "w") as f:
                    f.write(tree_or_file)

    do_tree(env_path, tree)

    if d.getVar("ENV_VERBOSE", True) == "1":
        _env_print(d)
}
addtask env_write after do_env before do_configure

def _env_read(d):
    import os.path
    import json
    env_file = d.getVar('_ENV_FILE', True)
    try:
        with open(env_file, "r") as f:
            content = f.read()
        tree = json.loads(content)
    except IOError:
        # If file doesn't exist, error is ok. We will write the file.
        if os.path.exists(env_file):
            raise   # forward error
        tree = {}  # start with empty dict, if file doesn't exists

    return tree

def _env_write(d, tree):
    import json
    env_file = d.getVar('_ENV_FILE', True)

    content = json.dumps(tree)
    # RACY: use posix-atomic rename instead.
    with open(env_file, "w") as f:
        f.write(content)

def _env_splitpath(path):
    parts = path.split("/")
    if any(len(part) == 0 for part in parts):
        bb.error("Environment path '%s' not sane. No leading or ending flash or double '/' characters please!" % (path,))
        sys.exit(0)  # abort
    return parts

def env_add(d, path, content):
    # RACY: If function is used simultaneously by two actors, only one actor
    # will succeed.
    tree = _env_read(d)
    parts = _env_splitpath(path)

    if len(parts) == 0:
        bb.error("todo")
        sys.exit(0)

    def do_tree(parts, tree):
        filename, new_parts = parts[0], parts[1:]
        if len(new_parts) == 0:
            # Place file into tree
            if filename in tree:
                bb.warn("Overwriting file %s in barebox environment." % (filename,))
            tree[filename] = content
        else:
            if filename in tree:
                # Directory of file already exists
                if not isinstance(tree[filename], dict):
                    # It's a file
                    bb.warn("Overwriting file %s in barebox environment." % (filename,))
                    tree[filename] = {}
                # Descend into directory
            else:
                # Create new directory, because it doesn't exists
                tree[filename] = {}
            do_tree(new_parts, tree[filename])

    do_tree(parts, tree)  # dict is modified *in-place*
    _env_write(d, tree)

def env_rm(d, path):
    # RACY: If function is used simultaneously by two actors, only one actor
    # will succeed.
    tree = _env_read(d)
    parts = _env_splitpath(path)

    def do_tree(parts, tree):
        filename, new_parts = parts[0], parts[1:]
        if len(new_parts) == 0:
            # Last level reached. Delete entry here.
            if filename not in tree:
                bb.warn("Cannot delete file %s in barebox environment. It doesn't exist!" % (filename,))
            else:
                if isinstance(tree[filename], dict):
                    bb.warn("Removing directory %s in barebox environment!" % (filename,))
                del tree[filename]
        else:
            if filename not in tree:
                bb.warn("Cannot delete file %s in barebox environment. It doesn't exist!" % (filename,))
            else:
                do_tree(new_parts, tree[filename])

    do_tree(parts, tree)  # dict is modified *in-place*
    _env_write(d, tree)

def _env_print(d):
    from os.path import join
    tree = _env_read(d)

    def do_tree(path, tree):
        for filename in sorted(tree):
            tree_or_file = tree[filename]
            if isinstance(tree_or_file, dict):
                # Is directory
                do_tree(join(path, filename), tree_or_file)
            else:
                # Is file
                if tree_or_file.count("\n") <= 1:
                    # The file contains only a single line. Print the content
                    # in the same line.
                    bb.plain("File '%s' content \"%s\"" %
                             (join(path, filename), tree_or_file.rstrip("\n")))
                else:
                    # The file consists of multiple lines. Print the content in
                    # the next lines.
                    bb.plain("File '%s' content:\n%s" %
                             (join(path, filename), tree_or_file.rstrip("\n")))

    do_tree("", tree)

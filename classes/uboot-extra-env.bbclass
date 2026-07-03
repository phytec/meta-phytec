# U-Boot extra environment handling
#
# Adds variables to U-Boot's default environment using the .env file mechanism
# (since u-boot v2021.07). A wrapper .env file owned by this class is
# generated under board/<vendor>/<board>/, and CONFIG_ENV_SOURCE_FILE is
# pointed at it. The wrapper #includes the board's original .env and appends
# env_add() entries; u-boot's scripts/env2string.awk dedupes by key (last
# value wins), so env_add() can both ADD new variables and OVERRIDE existing
# defaults. The upstream .env file is never modified.
#
# Note: the dedup only covers the .env text. Variables that also have a
# Kconfig-generated default in u-boot's include/env_default.h (bootdelay,
# loadaddr, bootcmd, bootargs, ...) keep that C string compiled into the
# default environment. Overriding them with env_add() still works, as on env
# import the last entry wins, but the Kconfig value remains visible in the
# binary and pre-relocation env_get_f() returns it. Prefer a Kconfig option
# or defconfig fragment for such variables.
#
# Usage:
#   inherit uboot-extra-env
#   python do_env:append() {
#       env_add(d, "mmcdev", "1")
#       env_add(d, "boot_targets", "mmc1 mmc0 spi_flash dhcp")
#   }

UBOOT_EXTRA_ENV_DIR = "${WORKDIR}/.extraenv"
UBOOT_EXTRA_ENV_FILE = "${UBOOT_EXTRA_ENV_DIR}/extra.env"
UBOOT_EXTRA_ENV_NAME ??= "oe-extra"
do_env[cleandirs] += "${UBOOT_EXTRA_ENV_DIR}"

def env_add(d, key, value):
    with open(d.getVar('UBOOT_EXTRA_ENV_FILE'), "a") as f:
        f.write("%s=%s\n" % (key, value))

python do_env() {
}
addtask do_env after do_fetch before do_configure

# Return the value of an 'OPTION="value"' line from u-boot's .config, without
# the quotes. Returns "" if the option is not set.
def uboot_config_get(config, option):
    with open(config) as f:
        for line in f:
            if line.startswith(option + "="):
                value = line.split("=", 1)[1]
                return value.strip().strip('"')
    return ""

# Set 'OPTION="value"' in u-boot's .config. Replaces the existing line (also
# a '# OPTION is not set' line), or appends one if there is none.
def uboot_config_set(config, option, value):
    new_line = '%s="%s"\n' % (option, value)

    with open(config) as f:
        old_lines = f.readlines()

    new_lines = []
    found = False
    for line in old_lines:
        if line.startswith(option + "=") or line.startswith("# " + option + " "):
            line = new_line
            found = True
        new_lines.append(line)
    if not found:
        new_lines.append(new_line)

    with open(config, "w") as f:
        f.writelines(new_lines)

python do_apply_extra_env() {
    import os

    # Nothing to do if no recipe called env_add().
    extra_env = d.getVar('UBOOT_EXTRA_ENV_FILE')
    if not os.path.exists(extra_env) or os.path.getsize(extra_env) == 0:
        return

    # Find the board's env directory and the name of its original .env file.
    config = os.path.join(d.getVar('B'), '.config')
    if not os.path.exists(config):
        bb.fatal("uboot-extra-env: %s not found - did do_configure run?" % config)

    board = uboot_config_get(config, 'CONFIG_SYS_BOARD')
    vendor = uboot_config_get(config, 'CONFIG_SYS_VENDOR')
    base = uboot_config_get(config, 'CONFIG_ENV_SOURCE_FILE')
    if not board:
        bb.fatal("uboot-extra-env: CONFIG_SYS_BOARD missing from .config")
    if not base:
        base = board

    # os.path.join drops the empty component when vendor is unset.
    env_dir = os.path.join(d.getVar('S'), 'board', vendor, board)
    if not os.path.exists(os.path.join(env_dir, base + '.env')):
        bb.fatal("uboot-extra-env: %s/%s.env not found - board may still use "
                 "CFG_EXTRA_ENV_SETTINGS in its config header." % (env_dir, base))

    name = d.getVar('UBOOT_EXTRA_ENV_NAME')
    if name == base:
        bb.fatal("uboot-extra-env: UBOOT_EXTRA_ENV_NAME (%s) collides with "
                 "the base .env name; choose a different name." % name)

    # Write our wrapper .env file: first include the board's original .env,
    # then add our extra lines. Fully rewritten on every run.
    with open(extra_env) as f:
        extras = f.read()
    with open(os.path.join(env_dir, name + '.env'), 'w') as f:
        f.write('#include "%s"\n\n' % os.path.join(env_dir, base + '.env'))
        f.write(extras)

    # Point u-boot at the wrapper. kbuild regenerates include/autoconf.mk
    # from .config during do_compile, so no explicit olddefconfig is needed.
    uboot_config_set(config, 'CONFIG_ENV_SOURCE_FILE', name)
}
addtask apply_extra_env after do_configure before do_compile

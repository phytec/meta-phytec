PARTUP_PACKAGE_SUFFIX ?= ".partup"
PARTUP_PACKAGE_FILES ??= ""
PARTUP_PACKAGE_DEPENDS ??= ""

PARTUP_LAYOUT_CONFIG ??= "${IMAGE_LINK_NAME}.yaml"
PARTUP_SEARCH_PATH ?= "${THISDIR}:${@':'.join('%s/partup' % p for p in '${BBPATH}'.split(':'))}"
PARTUP_LAYOUT_CONFIG_FULL_PATH = "${@bb.utils.which(d.getVar('PARTUP_SEARCH_PATH'), d.getVar('PARTUP_LAYOUT_CONFIG'))}"

USING_PARTUP = "${@bb.utils.contains('IMAGE_FSTYPES', 'partup', True, False, d)}"
USING_PARTUP[type] = "boolean"

PARTUP_BUILD_DIR = "${WORKDIR}/build-partup"
PSEUDO_IGNORE_PATHS .= ",${WORKDIR}/build-partup"

PARTUP_SECTIONS ??= ""
PARTUP_ARRAYS ??= ""

def partup_sha256sum(filename, d):
    import hashlib

    filepath = os.path.join(d.getVar('PARTUP_BUILD_DIR'), filename)
    h = hashlib.sha256()
    buf = bytearray(128 * 1024)
    view = memoryview(buf)

    with open(filepath, 'rb', buffering=0) as f:
        while True:
            size = f.readinto(buf)
            if size == 0:
                break
            h.update(view[:size])

    return h.hexdigest()

python do_copy_source_files() {
    import shutil

    deploy_dir_image = d.getVar('DEPLOY_DIR_IMAGE')
    imgdeploydir = d.getVar('IMGDEPLOYDIR')
    build_dir = d.getVar('PARTUP_BUILD_DIR')

    # We always regenerate files in the build directory, so remove previous ones
    if os.path.exists(build_dir):
        shutil.rmtree(build_dir)

    os.mkdir(build_dir)

    # Copy source files from deploy directory to build directory
    for f in d.getVar('PARTUP_PACKAGE_FILES').split():
        deploy_dir_image_f = os.path.join(deploy_dir_image, f)
        imgdeploydir_f = os.path.join(imgdeploydir, f)
        build_dir_f = os.path.join(build_dir, f)
        if os.path.exists(deploy_dir_image_f):
            shutil.copy(deploy_dir_image_f, build_dir_f)
        elif os.path.exists(imgdeploydir_f):
            shutil.copy(imgdeploydir_f, build_dir_f)
        else:
            bb.fatal('File \'{}\' neither found in DEPLOY_DIR_IMAGE nor IMGDEPLOYDIR'.format(f))
}
do_copy_source_files[vardeps] += "PARTUP_PACKAGE_FILES"

python do_layout_config() {
    import re

    config_in = d.getVar('PARTUP_LAYOUT_CONFIG_FULL_PATH')
    config_out = os.path.join(d.getVar('PARTUP_BUILD_DIR'), 'layout.yaml')

    sections = d.getVar('PARTUP_SECTIONS').split()
    arrays = d.getVar('PARTUP_ARRAYS').split()
    reflags = re.DOTALL | re.MULTILINE

    with open(config_in, 'r') as f:
        content = d.expand(f.read())
        for s in sections:
            d.setVarFlag('PARTUP_SECTION_' + s, 'type', 'boolean')
            d.appendVarFlag('do_layout_config', 'vardeps', ' PARTUP_SECTION_' + s)
            if oe.data.typed_value('PARTUP_SECTION_' + s, d):
                exp = r'#\s?(BEGIN|END)_{0}(\n|$)'.format(s.upper())
                content = re.sub(exp, r'', content, flags=reflags)
            else:
                exp = r'#\s?BEGIN_{0}(.*?)(?:#\s?END_{0}(\n|$))'.format(s.upper())
                content = re.sub(exp, r'', content, flags=reflags)
        for a in arrays:
            exp = r'#\s?ARRAY_{0}(.*?)(?:#\s?ARRAY_{0})'.format(a.upper())
            inst = re.findall(exp, content, flags=reflags)
            for i in inst:
                repl = ''
                for e in d.getVar('PARTUP_ARRAY_' + a).split():
                    repl += i.replace(r'@KEY@', e)
                content = re.sub(exp, repl, content, flags=reflags)

    with open(config_out, 'w') as f:
        f.write(re.sub(r'[\t ]+\n', r'\n', content, flags=reflags))
}
PARTUP_LAYOUT_CONFIG_CHECKSUM = "${@'${PARTUP_LAYOUT_CONFIG_FULL_PATH}:%s' % os.path.exists('${PARTUP_LAYOUT_CONFIG_FULL_PATH}') if oe.data.typed_value('USING_PARTUP', d) else ''}"
do_layout_config[file-checksums] += "${PARTUP_LAYOUT_CONFIG_CHECKSUM}"
do_layout_config[vardeps] += " \
    PARTUP_LAYOUT_CONFIG \
    PARTUP_SECTIONS \
    PARTUP_ARRAYS \
"

DEPENDS += "partup-native"

IMAGE_CMD:partup() {
    partup \
        package \
        ${IMGDEPLOYDIR}/${IMAGE_NAME}${PARTUP_PACKAGE_SUFFIX} \
        -C ${PARTUP_BUILD_DIR} \
        ${PARTUP_PACKAGE_FILES} layout.yaml
}
do_image_partup[depends] += "partup-native:do_populate_sysroot"
# Make sure build artifacts are deployed
do_image_partup[recrdeptask] += "do_deploy"
do_image_partup[deptask] += "do_image_complete"

def image_typedeps(d):
    fstypes = []
    for t in d.getVar('IMAGE_TYPES').split():
        for p in d.getVar('PARTUP_PACKAGE_FILES').split():
            if p.endswith(t):
                fstypes.append(t)
    return ' '.join(fstypes)

IMAGE_TYPEDEP:partup = "${@image_typedeps(d)}"

python() {
    if oe.data.typed_value('USING_PARTUP', d):
        task_deps = ['do_image_' + f.split('.')[0] for f in image_typedeps(d).split(' ')]

        # Add deploy task dependencies for do_copy_source_files
        package_deps = []
        for dep in d.getVar('PARTUP_PACKAGE_DEPENDS').split():
            package_deps.append(dep + ':do_deploy')
        d.appendVarFlag('do_copy_source_files', 'depends', ' '.join(package_deps))

        bb.build.addtask('do_copy_source_files', 'do_layout_config', ' '.join(task_deps), d)
        bb.build.addtask('do_layout_config', 'do_image_partup', 'do_image', d)
        bb.build.addtask('do_image_partup', 'do_image_complete', None, d)
}

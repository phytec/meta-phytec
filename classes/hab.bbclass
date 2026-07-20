# A set of helper functions to work with HABv4 signatures, required by multiple recipes

do_patch[depends] += "imx-cst-native:do_populate_sysroot"

def readfull(path):
    content = None
    with open(path, 'r') as fd:
        content = fd.read()
    return content


def readfull_bin(path):
    content = None
    with open(path, 'rb') as fd:
        content = fd.read()
    return content


def execcmd(name):
    import shlex
    import subprocess
    cmd = shlex.split(name)
    result = subprocess.run(cmd)
    return result.returncode


# Helper that formats the blocks in the passed list so they can be passed to NXP's cst tool
def make_csf_hab_block(blocks: list):
    def _block2str(block: dict):
        return '0x%X 0x%X 0x%X "%s"' % (block['addr'],
                                        block['offset'],
                                        block['size'],
                                        block['filename'])
    return ', \\\n'.join(_block2str(b) for b in blocks)


# Replaces the content of a .csf template file
def gen_csf(d, template_content: str, blocks: str, outfile):
    import hashlib
    template_content = template_content.replace('{HAB_BLOCKS}', blocks)
    srk_table_path = d.getVar('BOOTLOADER_SIGN_SRKFUSE_PATH')
    hash_sha256 = hashlib.sha256(readfull_bin(srk_table_path)).hexdigest()
    if hash_sha256 == '0d5dbc6ed8b0a55414648b19727e217453c54d1527cef3a62784ae818c9777e7':
        bb.warn("!! CRITICAL SECURITY WARNING: You're using Phytec's Development Keyring for signatures. Please create your own keys!!")
    template_content = template_content.replace('{SRK_TABLE_PATH}', srk_table_path)
    template_content = template_content.replace('{INSTALL_CSFK_PATH}', d.getVar('BOOTLOADER_SIGN_CSF_PATH'))
    template_content = template_content.replace('{INSTALL_KEY_PATH}', d.getVar('BOOTLOADER_SIGN_IMG_PATH'))
    template_content = template_content.replace('{HABV4_SRK_INDEX}', d.getVar('BOOTLOADER_HABV4_SRK_INDEX'))

    with open(outfile, 'w') as outfd:
        outfd.write(template_content)
    return True


def cst_sign(d, input_csf_path : str, output_image_path : str):
    pkcs11_module_path = d.getVar("PKCS11_MODULE_PATH")
    if pkcs11_module_path:
        os.environ["PKCS11_MODULE_PATH"] = pkcs11_module_path

    cmd = 'cst -i {0} -o {1}'.format(input_csf_path, output_image_path)
    if d.getVar("CST_KEY_SOURCE") == "token":
    # Add pkcs11 backend
        cmd += " -b pkcs11"
    return execcmd(cmd) == 0


def find_offset(embedded_image_path, image_path):
    # Search the offset of an embeeded file, -1 for not found
    from pathlib import Path
    embedded_image_path =  Path(embedded_image_path).read_bytes()
    image = Path(image_path).read_bytes()
    return image.find(embedded_image_path)


def store_resign_info(filename: str,
                      csf_spl_offset: int, csf_fit_offset: int,
                      spl_blocks: list, fit_blocks: list,
                      csf_spl_template, csf_fit_template, signed_image):
    # Store information collected from build to resign the bootloader images.
    import os
    import json

    # Collect HAB block list and offsets, make a copy of the lists
    json_data = {
        'csf_spl_offset': csf_spl_offset,
        'csf_fit_offset': csf_fit_offset,
        'spl_blocks': [],
        'fit_blocks': [],
        'csf_spl_template': csf_spl_template,
        'csf_fit_template': csf_fit_template,
        'signed_image': os.path.basename(signed_image),
    }

    # replace the complete path by the name of the file.
    # add the offset of the copy in signed_image
    for block in spl_blocks:
        new_block = block.copy()
        new_block['filename'] = os.path.basename(block['filename'])
        new_block['signed-offset'] = find_offset(block['filename'], signed_image)
        json_data['spl_blocks'].append(new_block)

    for block in fit_blocks:
        new_block = block.copy()
        new_block['filename'] = os.path.basename(block['filename'])
        new_block['signed-offset'] = find_offset(block['filename'], signed_image)
        json_data['fit_blocks'].append(new_block)

    # store the infos in json file
    with open(filename, 'w') as output:
        json.dump(json_data, output)

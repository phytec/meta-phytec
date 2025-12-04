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

def create_csf_template():
    return (
    '''[Header]
    Version = 4.3
    Hash Algorithm = sha256
    Engine = CAAM
    Engine Configuration = 0
    Certificate Format = X509
    Signature Format = CMS'''
    '\n\n'
    '''[Install SRK]
        # Index of the key location in the SRK table to be installed
        File = "{SRK_TABLE_PATH}"
        Source index = {HABV4_SRK_INDEX}'''
    '\n\n'
    '''[Install CSFK]
        # Key used to authenticate the CSF data
        File = "{INSTALL_CSFK_PATH}"'''
    '\n\n'
    '''[Authenticate CSF]'''
    '\n\n'
    '''[Install Key]
        # Key slot index used to authenticate the key to be installed
        Verification index = 0
        # Target key slot in HAB key store where key will be installed
        Target index = 2
        # Key to install
        File = "{INSTALL_KEY_PATH}"'''
    '\n\n'
    '''[Authenticate Data]
        # Key slot index used to authenticate the image data
        Verification index = 2
        # Authenticate Start Address, Offset, Length and file
        Blocks = {HAB_BLOCKS}
    ''')


# Helper that formats the blocks in the passed list so they can be passed to NXP's cst tool
def make_csf_hab_block(blocks: list):
    def _block2str(block: dict):
        return '0x%X 0x%X 0x%X "%s"' % (block['addr'],
                                        block['offset'],
                                        block['size'],
                                        block['filename'])
    return ', \\\n'.join(_block2str(b) for b in blocks)

# Generates a HAB instruction vector table from the passed parameters
def gen_ivt(signature: int, loadaddr: int, res1: int, dcdptr: int,
            bootdata: int, selfptr: int, csfptr: int, res2: int):
    import struct
    return struct.pack('<LLLLLLLL', signature, loadaddr, res1, dcdptr, bootdata, selfptr, csfptr, res2)


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


# Extracts the size specified in the header of an uncompressed ARM64 Linux Image
def get_linux_image_size(image_path):
    import struct
    with open(image_path, 'rb') as fd:
        data = fd.read(24)
        header = struct.Struct('<IIQQ')
        unpacked = header.unpack(data)
        return unpacked[3]


def cst_sign(d, input_csf_path : str, output_image_path : str):
    pkcs11_module_path = d.getVar("PKCS11_MODULE_PATH")
    if pkcs11_module_path:
        os.environ["PKCS11_MODULE_PATH"] = pkcs11_module_path

    cmd = 'cst -i {0} -o {1}'.format(input_csf_path, output_image_path)
    if d.getVar("CST_KEY_SOURCE") == "token":
    # Add pkcs11 backend
        cmd += " -b pkcs11"
    return execcmd(cmd) == 0

# Signs an image using NXP's cst tool. Appends IVT and CSF to the end of the image
#
# image_path: Path of the image to be signed. It will be padded (if necessary)
# padding_offset: The offset (from file start) until zeros must be appended to the file. If the file
# is already aligned to a 4096 byte boundary, this is equal to the file's size
# loadaddr: load address for IVT
# additional_blocks: list of dicts of files that must be signed too. The CSF will be appended to 'image_path'
def sign_inplace(d, image_path : str, padding_offset : int, loadaddr : int, additional_blocks : list):
    fd = open(image_path, 'ab')
    current_pos = fd.tell()
    padding_remaining = padding_offset - current_pos
    for i in range(0, padding_remaining):
        fd.write(b'\x00')

    headermagic = 0x412000D1
    # self ptr to the IVT. it follows the paddded image immediately, so we know the address easily
    selfptr = loadaddr+fd.tell()
    # csf follows ivt immediately. ivt is always 32 bytes, so just add it
    csfptr = selfptr+32
    ivt = gen_ivt(headermagic, loadaddr, 0x0, 0x0, 0x0, selfptr, csfptr, 0x0)

    fd.write(ivt)
    fd.flush()

    current_pos = fd.tell()

    # hab block for our image
    imageblock = dict()
    imageblock['addr'] = loadaddr
    imageblock['offset'] = 0x00
    imageblock['size'] = current_pos
    imageblock['filename'] = image_path

    workdir = d.getVar('WORKDIR')
    csf_image_path = os.path.join(workdir, 'csf_image.txt')
    csf_image_path_bin = os.path.join(workdir, 'csf_image.bin.signed')

    blocks = []
    blocks.append(imageblock)
    blocks.extend(additional_blocks)

    gen_csf(d, create_csf_template(), make_csf_hab_block(blocks), csf_image_path)

    if not cst_sign(d, csf_image_path, csf_image_path_bin):
        raise Exception("Failed to sign image: {0}".format(image_path))

    signed_image_csf = readfull_bin(csf_image_path_bin)
    fd.write(signed_image_csf)
    fd.close()


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

# A set of helper functions to work with HABv4 signatures, required by multiple recipes


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
        Source index = 0'''
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
    result = ''
    if(len(blocks) > 1):
        for block in blocks[:-1]:
            result += '0x%X 0x%X 0x%X "%s", \\\n' % (block['addr'],
                                                     block['offset'],
                                                     block['size'],
                                                     block['filename'])
    block = blocks[-1]
    result += '0x%X 0x%X 0x%X "%s"' % (block['addr'],
                                       block['offset'],
                                       block['size'],
                                       block['filename'])
    return result


# Generates a HAB instruction vector table from the passed parameters
def gen_ivt(signature: int, loadaddr: int, res1: int, dcdptr: int,
            bootdata: int, selfptr: int, csfptr: int, res2: int):
    import struct
    return struct.pack('<LLLLLLLL', signature, loadaddr, res1, dcdptr, bootdata, selfptr, csfptr, res2)


# Replaces the content of a .csf template file
def gen_csf(d, template_content: str, blocks: str, outfile):
    import hashlib
    template_content = template_content.replace('{HAB_BLOCKS}', blocks)
    srk_table_path = d.getVar('BOOTLOADER_SIGN_SRKFUSE_PATH', True)
    hash_sha256 = hashlib.sha256(readfull_bin(srk_table_path)).hexdigest()
    if hash_sha256 == '0d5dbc6ed8b0a55414648b19727e217453c54d1527cef3a62784ae818c9777e7':
        bb.warn("!! CRITICAL SECURITY WARNING: You're using Phytec's Development Keyring for signatures. Please create your own keys!!")
    template_content = template_content.replace('{SRK_TABLE_PATH}', srk_table_path)
    template_content = template_content.replace('{INSTALL_CSFK_PATH}', d.getVar('BOOTLOADER_SIGN_CSF_PATH', True))
    template_content = template_content.replace('{INSTALL_KEY_PATH}', d.getVar('BOOTLOADER_SIGN_IMG_PATH', True))

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

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
    template_content = template_content.replace('{HAB_BLOCKS}', blocks)
    template_content = template_content.replace('{SRK_TABLE_PATH}', d.getVar('BOOTLOADER_SIGN_SRKFUSE_PATH', True))
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

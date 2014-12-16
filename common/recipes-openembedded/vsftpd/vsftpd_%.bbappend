python do_config_debug_tweaks() {
    import shutil
    if "debug-tweaks" in d.getVar("EXTRA_IMAGE_FEATURES", True):
        workdir = d.getVar("WORKDIR", True)
        conf = os.path.join(workdir, "vsftpd.conf")
        shutil.copyfile(conf,conf+".orig")
        with open(conf+".orig","r") as infile:
            with open(conf,"w") as outfile:
                for line in infile:
                    if "anonymous_enable" in line:
                        outfile.write("anonymous_enable=YES\n")
                    elif "anon_upload_enable" in line:
                        outfile.write("anon_upload_enable=YES\n")
                    elif "anon_mkdir_write_enable" in line:
                        outfile.write("anon_mkdir_write_enable=YES\n")
                    else:
                        outfile.write(line)
}
addtask config_debug_tweaks after do_patch before do_configure

# this is a workaround for missing upstream patch [kernel.bbclass: Fix
# so_shared_workdir task ordering
do_compile_kernelmodules_append(){
	do_shared_workdir
}

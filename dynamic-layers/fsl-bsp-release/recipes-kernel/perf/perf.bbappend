PACKAGECONFIG[bpf-skel] = ",BUILD_BPF_SKEL=0"

PERF_SRC:append = " include/uapi/asm-generic/Kbuild"

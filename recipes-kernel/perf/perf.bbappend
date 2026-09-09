# oe-core perf passes BUILD_BPF_SKEL=0 to disable BPF skeletons, but perf
# Makefile.config uses ifdef BUILD_BPF_SKEL, which treats =0 as defined and
# still runs the clang-bpf-co-re check, failing when clang is absent. Pass
# no disable-args so BUILD_BPF_SKEL stays undefined and the block is skipped.
PACKAGECONFIG[bpf-skel] = ","

ART = /home/dspfac/aosp-android-11/art
AOSP = /home/dspfac/aosp-android-11
STUBS = /home/dspfac/art-universal-build/stubs

CXX = g++
CXXFLAGS = -std=c++17 -O2 -w \
  -include $(STUBS)/art_gcc_compat.h \
  -Wno-attributes \
  -fpermissive \
  -I$(STUBS) \
  -I$(STUBS)/runtime \
  -I$(ART) \
  -I$(ART)/libdexfile \
  -I$(ART)/libartbase \
  -I$(ART)/libartpalette/include \
  -isystem $(ART)/runtime \
  -I$(ART)/compiler \
  -I$(ART)/disassembler \
  -I$(ART)/compiler/debug \
  -I$(ART)/libelffile \
  -I$(ART)/libprofile \
  -I$(ART)/cmdline \
  -I$(ART)/dex2oat \
  -I$(ART)/dex2oat/include \
  -I$(ART)/dexlayout \
  -I$(AOSP)/system/core/base/include \
  -I$(AOSP)/system/core/libziparchive/include \
  -I$(AOSP)/system/core/libutils/include \
  -I$(AOSP)/system/core/libcutils/include \
  -I$(AOSP)/libnativehelper/include_jni \
  -I$(AOSP)/system/logging/liblog/include \
  -I$(AOSP)/prebuilts/jdk/jdk11/linux-x86/include \
  -I$(AOSP)/prebuilts/jdk/jdk11/linux-x86/include/linux \
  -I$(AOSP)/external/zlib \
  -I$(AOSP)/external/lz4/lib \
  -I$(AOSP)/external/vixl/src \
  -DART_DEFAULT_GC_TYPE_IS_CMS \
  -DART_BASE_ADDRESS=0x70000000 \
  -DART_STACK_OVERFLOW_GAP_arm=8192 \
  -DART_STACK_OVERFLOW_GAP_arm64=8192 \
  -DART_STACK_OVERFLOW_GAP_x86=8192 \
  -DART_STACK_OVERFLOW_GAP_x86_64=8192

BUILDDIR = build

# ============ libdexfile ============
DEXFILE_SRCS = $(filter-out %_test.cc %_fuzzer.cc,$(wildcard $(ART)/libdexfile/dex/*.cc))
DEXFILE_OBJS = $(patsubst $(ART)/%.cc,$(BUILDDIR)/%.o,$(DEXFILE_SRCS))

# ============ libartbase ============
# Exclude platform-specific files (fuchsia, windows) and safe_copy (needs sys/ucontext)
ARTBASE_SRCS_ALL = $(filter-out %_test.cc %_fuzzer.cc,$(wildcard \
  $(ART)/libartbase/base/*.cc \
  $(ART)/libartbase/base/unix_file/*.cc \
  $(ART)/libartbase/arch/*.cc))
# Exclude platform-specific
ARTBASE_EXCLUDE = %mem_map_fuchsia.cc %mem_map_windows.cc %safe_copy.cc %socket_peer_is_trusted.cc
ARTBASE_SRCS = $(filter-out $(ARTBASE_EXCLUDE),$(ARTBASE_SRCS_ALL))
ARTBASE_OBJS = $(patsubst $(ART)/%.cc,$(BUILDDIR)/%.o,$(ARTBASE_SRCS))

# ============ compiler ============
COMPILER_SRCS_ALL = $(filter-out %_test.cc %_fuzzer.cc,$(wildcard \
  $(ART)/compiler/*.cc \
  $(ART)/compiler/debug/*.cc \
  $(ART)/compiler/debug/dwarf/*.cc \
  $(ART)/compiler/dex/*.cc \
  $(ART)/compiler/driver/*.cc \
  $(ART)/compiler/jit/*.cc \
  $(ART)/compiler/jni/*.cc \
  $(ART)/compiler/jni/quick/*.cc \
  $(ART)/compiler/jni/quick/arm/*.cc \
  $(ART)/compiler/jni/quick/arm64/*.cc \
  $(ART)/compiler/jni/quick/x86/*.cc \
  $(ART)/compiler/jni/quick/x86_64/*.cc \
  $(ART)/compiler/linker/*.cc \
  $(ART)/compiler/linker/x86/*.cc \
  $(ART)/compiler/linker/x86_64/*.cc \
  $(ART)/compiler/linker/arm/*.cc \
  $(ART)/compiler/linker/arm64/*.cc \
  $(ART)/compiler/optimizing/*.cc \
  $(ART)/compiler/trampolines/*.cc \
  $(ART)/compiler/utils/*.cc \
  $(ART)/compiler/utils/x86/*.cc \
  $(ART)/compiler/utils/x86_64/*.cc \
  $(ART)/compiler/utils/arm/*.cc \
  $(ART)/compiler/utils/arm64/*.cc))
COMPILER_SRCS = $(COMPILER_SRCS_ALL)
COMPILER_OBJS = $(patsubst $(ART)/%.cc,$(BUILDDIR)/%.o,$(COMPILER_SRCS))

# ============ dex2oat ============
DEX2OAT_SRCS = $(filter-out %_test.cc %_fuzzer.cc,$(wildcard \
  $(ART)/dex2oat/*.cc \
  $(ART)/dex2oat/dex/*.cc \
  $(ART)/dex2oat/driver/*.cc \
  $(ART)/dex2oat/linker/*.cc \
  $(ART)/dex2oat/linker/arm/*.cc \
  $(ART)/dex2oat/linker/arm64/*.cc \
  $(ART)/dex2oat/linker/x86/*.cc \
  $(ART)/dex2oat/linker/x86_64/*.cc))
DEX2OAT_OBJS = $(patsubst $(ART)/%.cc,$(BUILDDIR)/%.o,$(DEX2OAT_SRCS))

# ============ targets ============
.PHONY: all libdexfile libartbase libcompiler dex2oat clean status

all: libdexfile libartbase libcompiler dex2oat-build

libdexfile: $(DEXFILE_OBJS)
	@echo "=== libdexfile ==="
	@echo "Compiled: $$(ls $(BUILDDIR)/libdexfile/dex/*.o 2>/dev/null | wc -l) / $(words $(DEXFILE_OBJS))"

libartbase: $(ARTBASE_OBJS)
	@echo "=== libartbase ==="
	@echo "Compiled: $$(ls $(BUILDDIR)/libartbase/base/*.o $(BUILDDIR)/libartbase/base/unix_file/*.o $(BUILDDIR)/libartbase/arch/*.o 2>/dev/null | wc -l) / $(words $(ARTBASE_OBJS))"

libcompiler: $(COMPILER_OBJS)
	@echo "=== compiler ==="
	@compiled=$$(find $(BUILDDIR)/compiler -name '*.o' 2>/dev/null | wc -l); \
	echo "Compiled: $$compiled / $(words $(COMPILER_OBJS))"

dex2oat-build: $(DEX2OAT_OBJS)
	@echo "=== dex2oat ==="
	@compiled=$$(find $(BUILDDIR)/dex2oat -name '*.o' 2>/dev/null | wc -l); \
	echo "Compiled: $$compiled / $(words $(DEX2OAT_OBJS))"

status:
	@echo "=== Build Status ==="
	@dex=$$(ls $(BUILDDIR)/libdexfile/dex/*.o 2>/dev/null | wc -l); \
	dext=$(words $(DEXFILE_OBJS)); \
	echo "libdexfile: $$dex / $$dext"
	@art=$$(find $(BUILDDIR)/libartbase -name '*.o' 2>/dev/null | wc -l); \
	artt=$(words $(ARTBASE_OBJS)); \
	echo "libartbase: $$art / $$artt"
	@comp=$$(find $(BUILDDIR)/compiler -name '*.o' 2>/dev/null | wc -l); \
	compt=$(words $(COMPILER_OBJS)); \
	echo "compiler:   $$comp / $$compt"
	@d2o=$$(find $(BUILDDIR)/dex2oat -name '*.o' 2>/dev/null | wc -l); \
	d2ot=$(words $(DEX2OAT_OBJS)); \
	echo "dex2oat:    $$d2o / $$d2ot"

# Pattern rule: try to compile, continue on failure
$(BUILDDIR)/%.o: $(ART)/%.cc
	@mkdir -p $(dir $@)
	@$(CXX) $(CXXFLAGS) -c $< -o $@ 2>&1 && echo "OK: $(notdir $<)" || { echo "FAIL: $(notdir $<)"; rm -f $@; }

clean:
	rm -rf $(BUILDDIR)

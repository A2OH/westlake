ART = $HOME/aosp-android-11/art
AOSP = $HOME/aosp-android-11

CXX = g++
CXXFLAGS = -std=c++17 -O2 -w \
  -I$(ART) \
  -I$(ART)/libdexfile \
  -I$(ART)/libartbase \
  -I$(ART)/libartpalette/include \
  -I$(ART)/runtime \
  -I$(AOSP)/system/core/base/include \
  -I$(AOSP)/system/core/libziparchive/include \
  -I$(AOSP)/libnativehelper/include_jni \
  -I$(AOSP)/system/logging/liblog/include \
  -I$(AOSP)/prebuilts/jdk/jdk11/linux-x86/include \
  -I$(AOSP)/prebuilts/jdk/jdk11/linux-x86/include/linux \
  -I$(AOSP)/external/zlib \
  -DART_DEFAULT_GC_TYPE_IS_CMS \
  -DART_BASE_ADDRESS=0x70000000 \
  -DART_STACK_OVERFLOW_GAP_arm=8192 \
  -DART_STACK_OVERFLOW_GAP_arm64=8192 \
  -DART_STACK_OVERFLOW_GAP_x86=8192 \
  -DART_STACK_OVERFLOW_GAP_x86_64=8192

BUILDDIR = build

DEXFILE_SRCS = $(filter-out %_test.cc %_fuzzer.cc,$(wildcard $(ART)/libdexfile/dex/*.cc))
DEXFILE_OBJS = $(patsubst $(ART)/%.cc,$(BUILDDIR)/%.o,$(DEXFILE_SRCS))

all: libdexfile

libdexfile: $(DEXFILE_OBJS)
	@echo "=== libdexfile: $(words $(DEXFILE_OBJS)) objects ==="
	@echo "Compiled: $$(ls $(BUILDDIR)/libdexfile/dex/*.o 2>/dev/null | wc -l) / $(words $(DEXFILE_OBJS))"

$(BUILDDIR)/%.o: $(ART)/%.cc
	@mkdir -p $(dir $@)
	@$(CXX) $(CXXFLAGS) -c $< -o $@ 2>&1 && echo "OK: $(notdir $<)" || echo "FAIL: $(notdir $<)"

clean:
	rm -rf $(BUILDDIR)

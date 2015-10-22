LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_CFLAGS    := -DHUNSPELL_CHROME_CLIENT=1 \
                   -D_GLIBCXX_PERMIT_BACKWARD_HASH=1 \
                   
#LOCAL_CFLAGS    := -D_GLIBCXX_PERMIT_BACKWARD_HASH=1
                   
LOCAL_MODULE    := SpellChecker
LOCAL_SRC_FILES := SpellChecker.cpp
LOCAL_LDLIBS    := -lm -llog -landroid

include $(BUILD_SHARED_LIBRARY)

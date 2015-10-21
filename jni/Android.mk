LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := SpellChecker
LOCAL_SRC_FILES := SpellChecker.cpp
LOCAL_LDLIBS    := -lm -llog -ljnigraphics -landroid

include $(BUILD_SHARED_LIBRARY)

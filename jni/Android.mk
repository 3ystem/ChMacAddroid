LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := native_ioctller
LOCAL_SRC_FILES := native_ioctller.c
include $(BUILD_SHARED_LIBRARY)


include $(CLEAR_VARS)

LOCAL_MODULE    := native_ioctller_static
LOCAL_SRC_FILES := native_ioctller.c
include $(BUILD_STATIC_LIBRARY)


include $(CLEAR_VARS)

LOCAL_MODULE    := chmacaddr
LOCAL_SRC_FILES := chmacaddr.c
LOCAL_STATIC_LIBRARIES := native_ioctller_static
include $(BUILD_EXECUTABLE)

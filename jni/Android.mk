LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := my_native_module  # Replace with your module name

# Include Boost headers
LOCAL_C_INCLUDES += D:/Downloads/NewMap/osrm/boost_1_82_0/Boost-for-Android/boost_1_85_0/boost_1_85_0/boost

# Add your native source files
LOCAL_SRC_FILES := my_native_file.cpp  # Replace with your actual source files

# Link Boost libraries
LOCAL_LDLIBS += -LD:/Downloads/NewMap/osrm/boost_1_82_0/Boost-for-Android/boost_1_85_0/boost_1_85_0/stage/lib \
                -lboost_system -lboost_filesystem  # Add more Boost libraries if needed

include $(BUILD_SHARED_LIBRARY)

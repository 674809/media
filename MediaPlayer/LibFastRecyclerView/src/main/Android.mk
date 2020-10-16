#
# Copyright (C) 2008 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

#### Start ####
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_RESOURCE_DIR += frameworks/support/v7/recyclerview/res

#LOCAL_RESOURCE_DIR += prebuilts/sdk/current/support/v7/recyclerview/res

#### 指定编译所用SDK  ####
#TARGET_PLATFORM=android-25

## user: 指该模块只在user版本下才编译
## eng: 指该模块只在eng版本下才编译
## tests: 指该模块只在tests版本下才编译
## optional:指该模块在所有版本下都编译
LOCAL_MODULE_TAGS := optional

#### 编译名称  ####
#APK
#LOCAL_PACKAGE_NAME := EGAR_COMMON
#Jar
LOCAL_MODULE = EGAR_RECYCLERVIEW

### 声明 资源文件 ###
#LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res
### 声明 JAVA文件 & AIDL文件 ###
SRC_DIRS := java
LOCAL_SRC_FILES := $(call all-java-files-under, $(SRC_DIRS))

## current 编译时会忽略源码隐藏的API
## LOCAL_JAVA_LIBRARIES=true时，Android.mk中不能定义LOCAL_SDK_VERSION
#LOCAL_SDK_VERSION := current

#### 忽略编译 ####
#此变量可以使其他的模块不加入编译，如下表示不编译AlarmClock
#LOCAL_OVERRIDES_PACKAGES := AlarmClock

#### 编译目标位置  ####
#true , "system/priv-app"
#false , "system/app"
LOCAL_PRIVILEGED_MODULE := false

#### 签名 ####
## ^^ 使用场景 ^^
## (1)	系统中所有使用android.uid.system作为共享UID的APK，
##  	都会首先在manifest节点中增加android:sharedUserId="android.uid.system"，
##  	然后在Android.mk中增加LOCAL_CERTIFICATE := platform
## 		可以参见Settings等
## (2)	系统中所有使用android.uid.shared作为共享UID的APK，
## 		都会在manifest节点中增加android:sharedUserId="android.uid.shared"，
## 		然后在Android.mk中增加LOCAL_CERTIFICATE := shared。
## 		可以参见Launcher等
## (3)	系统中所有使用android.uid.shared作为共享UID的APK，
## 		都会在manifest节点中增加android:sharedUserId="android.uid.shared"，
## 		然后在Android.mk中增加LOCAL_CERTIFICATE := shared。
## 		可以参见Launcher等
## (4)	系统中所有使用android.media作为共享UID的APK，
## 		都会在manifest节点中增加android:sharedUserId="android.media"，
## 		然后在Android.mk中增加LOCAL_CERTIFICATE := media。
## 		可以参见Gallery等。
## ^^ 取值范围 ^^
## (1)	testkey：普通APK，默认情况下使用
## (2)	platform：该APK完成一些系统的核心功能。经过对系统中存在的文件夹的访问测试，这种方式编译出来的APK所在进程的UID为system。
## (3)	shared：该APK需要和home/contacts进程共享数据。
## (4)	media：该APK是media/download系统中的一环。
LOCAL_CERTIFICATE := platform

#### 混淆 ####
## 混淆是否可用
## ^^ 4.2 版本  ^^
## 		支持full， custom， optonly三种，
##		其中optonly的作用是增加 proguard_flags += -dontobfuscate，即不做混淆（但是其他优化和压缩都会做）
## ^^ 4.4 版本  ^^
##		支持full， custom， nosystem， obfuscation， optimization属性
##
## disabled : 不要使用代码混淆的工具进行代码混淆
## full : 默认值, 即将该工程代码全部混淆
## nosystem，是指不使用系统的proguard.flags
## obfuscation，是指做混淆，如果该值空，则会加上-dontobfuscate的标识
## optimization，优化，如果该值为空，则会加上-dontoptimize的标识
#LOCAL_PROGUARD_ENABLED := full obfuscation
#LOCAL_PROGUARD_ENABLED := disabled
## 混淆文件
#LOCAL_PROGUARD_FLAG_FILES := proguard.flags

#### Use LIBRARIES ####
## .so
#LOCAL_JNI_SHARED_LIBRARIES := SO_libqtplayer
#LOCAL_JNI_SHARED_LIBRARIES +=
## .jar
LOCAL_STATIC_JAVA_LIBRARIES := android-support-v4
#LOCAL_STATIC_JAVA_LIBRARIES += android-support-v7-appcompat
LOCAL_STATIC_JAVA_LIBRARIES += android-support-v7-recyclerview

#LOCAL_STATIC_JAVA_LIBRARIES += PINYIN_JAR
#LOCAL_STATIC_JAVA_LIBRARIES +=
## .aar
#LOCAL_STATIC_JAVA_AAR_LIBRARIES := AAR_vlc
#LOCAL_STATIC_JAVA_AAR_LIBRARIES +=

#### 指定编译生成类型 ####
## 指定编译生成APK
#include $(BUILD_PACKAGE)
## 指定编译生成一个静态的 Java 库
## 静态库不会复制到的APK包中，但是能够用于编译共享库
include $(BUILD_STATIC_JAVA_LIBRARY)
## 指向编译脚本，根据所有的在 LOCAL_XXX 变量把列出的源代码文件编译成一个共享库，将生成一个名为lib$(LOCAL_MODULE).so的文件
## 注意：你必须至少在包含这个文件之前定义LOCAL_MODULE和LOCAL_SRC_FILES
#include $(BUILD_SHARED_LIBRARY)

#### 预编译 ####
#include $(CLEAR_VARS)
## ^^ .so 预编译 ^^
#LOCAL_PREBUILT_LIBS := SO_libqtplayer:libs/armeabi-v7a/libqtplayer.so
#LOCAL_PREBUILT_LIBS +=
## ^^ .jar 预编译 ^^
#LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := PINYIN_JAR:libs/pinyin4j-2.5.0.jar
#LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES +=
## ^^ .aar 预编译 ^^
#LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += AAR_vlc:libs/libvlc-3.0.0.aar
#LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES +=
#include $(BUILD_MULTI_PREBUILT)

#use the folling include to make apk
include $(call all-makefiles-under,$(LOCAL_PATH))

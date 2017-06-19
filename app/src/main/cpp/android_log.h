//
// Created by vungoctrong@gmail.com on 6/19/2017.
//

#ifndef MOSQUITTOMQTT_ANDROID_LOG_H
#define MOSQUITTOMQTT_ANDROID_LOG_H

#include <android/log.h>

#define TP_STR_HELPER(x) #x
#define TP_STR(x) TP_STR_HELPER(x)
#define TAG "trongvu"
#define LOGV(fmt, ...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, "%s:%s " fmt "\n", __PRETTY_FUNCTION__, TP_STR(__LINE__), ##__VA_ARGS__)
#define LOGD(fmt, ...) __android_log_print(ANDROID_LOG_DEBUG, TAG, "%s:%s " fmt "\n", __PRETTY_FUNCTION__, TP_STR(__LINE__), ##__VA_ARGS__)
#define LOGI(fmt, ...) __android_log_print(ANDROID_LOG_INFO, TAG, "%s:%s " fmt "\n", __PRETTY_FUNCTION__, TP_STR(__LINE__), ##__VA_ARGS__)
#define LOGW(fmt, ...) __android_log_print(ANDROID_LOG_WARN, TAG, "%s:%s " fmt "\n", __PRETTY_FUNCTION__, TP_STR(__LINE__), ##__VA_ARGS__)
#define LOGE(fmt, ...) __android_log_print(ANDROID_LOG_ERROR, TAG, "%s:%s " fmt "\n", __PRETTY_FUNCTION__, TP_STR(__LINE__), ##__VA_ARGS__)
#endif //MOSQUITTOMQTT_ANDROID_LOG_H

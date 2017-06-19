
#include <jni.h>
#include "mosquitto_callback_wrapper.h"
#include "android_log.h"

mosquitto_callback_wrapper::mosquitto_callback_wrapper(const char *id) : mosquittopp(id) {}

mosquitto_callback_wrapper::~mosquitto_callback_wrapper() {}

void mosquitto_callback_wrapper::on_connect(int rc) {
    callVoidMethod("on_connect", "(I)V", (jint) rc);
}

void mosquitto_callback_wrapper::on_message(const struct mosquitto_message *message) {
    callVoidMethodOnMessage("on_message", "(ILjava/lang/String;ILjava/lang/String;IZ)V", message);
}

void mosquitto_callback_wrapper::on_subscribe(int mid, int qos_count, const int *granted_qos) {
    callVoidMethod("on_subscribe", "(III)V", (jint) mid, (jint) qos_count, (jint) *granted_qos);
}

void mosquitto_callback_wrapper::on_disconnect(int rc) {
    callVoidMethod("on_disconnect", "(I)V", (jint) rc);
}

void mosquitto_callback_wrapper::on_publish(int mid) {
    callVoidMethod("on_publish", "(I)V", (jint) mid);
}

void mosquitto_callback_wrapper::on_unsubscribe(int mid) {
    callVoidMethod("on_unsubscribe", "(I)V", (jint) mid);
}

void mosquitto_callback_wrapper::on_log(int level, const char *str) {
}

void mosquitto_callback_wrapper::on_error() {
}


void mosquitto_callback_wrapper::callVoidMethod(const char *func, const char *strSignature, ...) {
    LOGI("callVoidMethod = %s", func);
    bool attached = false;
    int getEnvStat = g_vm->GetEnv((void **) &g_env, JNI_VERSION_1_6);
    if (getEnvStat == JNI_EDETACHED) {
        if (g_vm->AttachCurrentThread(&g_env, NULL) != 0) {
            LOGE("Failed to attach");
        } else {
            attached = true;
        }
    } else if (getEnvStat == JNI_OK) {
        LOGE("getEnvStat failed");
    } else if (getEnvStat != JNI_EVERSION) {
        LOGE("GetEnv: version not supported");
    }
    g_method = g_env->GetMethodID(g_class, func, strSignature);
    if (g_method == NULL) {
        LOGE("Could not find method %s", func);
    }
    va_list args;
    va_start(args, strSignature);
    g_env->CallVoidMethod(g_obj, g_method, args);

    if (g_env->ExceptionCheck()) {
        g_env->ExceptionDescribe();
    }
    if (attached)
        g_vm->DetachCurrentThread();
}

void mosquitto_callback_wrapper::callVoidMethodOnMessage(const char *func, const char *strSignature,
                                                         const struct mosquitto_message *message) {
    LOGI("callVoidMethod = %s", func);
    bool attached = false;
    int getEnvStat = g_vm->GetEnv((void **) &g_env, JNI_VERSION_1_6);
    if (getEnvStat == JNI_EDETACHED) {
        if (g_vm->AttachCurrentThread(&g_env, NULL) != 0) {
            LOGE("Failed to attach");
        } else {
            attached = true;
        }
    } else if (getEnvStat == JNI_OK) {
        LOGE("getEnvStat failed");
    } else if (getEnvStat != JNI_EVERSION) {
        LOGE("GetEnv: version not supported");
    }
    g_method = g_env->GetMethodID(g_class, func, strSignature);
    if (g_method == NULL) {
        LOGE("Could not find method %s", func);
    }
    jstring topic = g_env->NewStringUTF(message->topic);
    jstring msg = g_env->NewStringUTF((char *) message->payload);
    g_env->CallVoidMethod(g_obj, g_method, (jint) message->mid, topic, (jint) message->payloadlen,
                         msg, (jint) message->qos, (jboolean) message->retain);
    g_env->DeleteLocalRef(topic);
    g_env->DeleteLocalRef(msg);

    if (g_env->ExceptionCheck()) {
        g_env->ExceptionDescribe();
    }
    if (attached)
        g_vm->DetachCurrentThread();
}
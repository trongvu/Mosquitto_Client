//
// Created by vungoctrong@gmail.com on 6/16/2017.
//

#include <jni.h>
#include "com_android_trongvu_mosquittomqtt_wrapper_MosquittoWrapper.h"
#include "android_log.h"
/*
 * Class:     com_android_trongvu_mosquittomqtt_wrapper_MosquittoWrapper
 * Method:    connect
 * Signature: (Ljava/lang/String;II)I
 */
JNIEXPORT jint JNICALL Java_com_android_trongvu_mosquittomqtt_wrapper_MosquittoWrapper_connect
        (JNIEnv *env, jobject obj, jstring host, jint port, jint keepalive) {
    const char *_host = env->GetStringUTFChars(host, 0);
    LOGI("connect, host = %s, port = %d, keepalive = %d", _host, port, keepalive);
    jni_mosquitto->connect(_host, port, keepalive);
    int rc = jni_mosquitto->connect(_host, (int) port, (int) keepalive);
    LOGI("connect, rc = %d", rc);
    env->ReleaseStringUTFChars(host, _host);
    return rc;
}

/*
 * Class:     com_android_trongvu_mosquittomqtt_wrapper_MosquittoWrapper
 * Method:    reconnect
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_android_trongvu_mosquittomqtt_wrapper_MosquittoWrapper_reconnect
        (JNIEnv *env, jobject obj) {
    if (jni_mosquitto == NULL)
        return -1;
    return jni_mosquitto->reconnect();
}

/*
 * Class:     com_android_trongvu_mosquittomqtt_wrapper_MosquittoWrapper
 * Method:    disconnect
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_android_trongvu_mosquittomqtt_wrapper_MosquittoWrapper_disconnect
        (JNIEnv *env, jobject obj) {
    if (jni_mosquitto == NULL)
        return -1;
    return jni_mosquitto->disconnect();
}

/*
 * Class:     com_android_trongvu_mosquittomqtt_wrapper_MosquittoWrapper
 * Method:    publish
 * Signature: (ILjava/lang/String;I[BIZ)I
 */
JNIEXPORT jint JNICALL Java_com_android_trongvu_mosquittomqtt_wrapper_MosquittoWrapper_publish
        (JNIEnv *env, jobject obj, jint mid, jstring topic, jstring payload, jint qos,
         jboolean retain) {
    if (jni_mosquitto == NULL)
        return -1;
    const char *_topic = env->GetStringUTFChars(topic, 0);
    const char *_payload = env->GetStringUTFChars(payload, 0);
    int rc = jni_mosquitto->publish(NULL, _topic, strlen(_payload), _payload, qos, retain);
    env->ReleaseStringUTFChars(topic, _topic);
    env->ReleaseStringUTFChars(payload, _payload);
    return rc;
}

/*
 * Class:     com_android_trongvu_mosquittomqtt_wrapper_MosquittoWrapper
 * Method:    subscribe
 * Signature: (ILjava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_com_android_trongvu_mosquittomqtt_wrapper_MosquittoWrapper_subscribe
        (JNIEnv *env, jobject obj, jint mid, jstring topic, jint qos) {
    if (jni_mosquitto == NULL)
        return -1;
    const char *_topic = env->GetStringUTFChars(topic, 0);
    int rc = jni_mosquitto->subscribe(&mid, _topic, qos);
    env->ReleaseStringUTFChars(topic, _topic);
    return rc;
}

/*
 * Class:     com_android_trongvu_mosquittomqtt_wrapper_MosquittoWrapper
 * Method:    unsubscribe
 * Signature: (ILjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_android_trongvu_mosquittomqtt_wrapper_MosquittoWrapper_unsubscribe
        (JNIEnv *env, jobject obj, jint mid, jstring topic) {
    if (jni_mosquitto == NULL)
        return -1;
    const char *_topic = env->GetStringUTFChars(topic, 0);
    int rc = jni_mosquitto->unsubscribe(&mid, _topic);
    env->ReleaseStringUTFChars(topic, _topic);
    return rc;
}

/*
 * Class:     com_android_trongvu_mosquittomqtt_wrapper_MosquittoWrapper
 * Method:    loop
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_android_trongvu_mosquittomqtt_wrapper_MosquittoWrapper_loop
        (JNIEnv *env, jobject obj, jint timeout, jint maxpackets) {
    if (jni_mosquitto == NULL)
        return -1;
    LOGI("loop called");
    return jni_mosquitto->loop(timeout, maxpackets);
}

JNIEXPORT void JNICALL Java_com_android_trongvu_mosquittomqtt_wrapper_MosquittoWrapper_release
        (JNIEnv *env, jobject obj) {
    if (jni_mosquitto != NULL) {
        delete jni_mosquitto;
        jni_mosquitto = NULL;
    }
    mosqpp::lib_cleanup();
}

JNIEXPORT jboolean JNICALL Java_com_android_trongvu_mosquittomqtt_wrapper_MosquittoWrapper_init
        (JNIEnv *env, jobject obj, jstring id) {
    if (jni_mosquitto != NULL)
        delete jni_mosquitto;
    const char *_id = env->GetStringUTFChars(id, 0);
    mosqpp::lib_init();
    jni_mosquitto = new mosquitto_callback_wrapper(_id);
    LOGI("register client ID = %s", _id);
    ///
    bool returnValue = true;
    // convert local to global reference
    // (local will die after this method call)
    env->GetJavaVM(&jni_mosquitto->g_vm);
    jclass cls = env->GetObjectClass(obj);
    jni_mosquitto->g_class = (jclass) env->NewGlobalRef(cls);
    jni_mosquitto->g_obj = env->NewGlobalRef(obj);

    // save refs for callback
    jclass g_clazz = env->GetObjectClass(obj);
    if (g_clazz == NULL) {
        LOGE("g_clazz == null");
    }
    return (jboolean) returnValue;
    ///
    return true;
}
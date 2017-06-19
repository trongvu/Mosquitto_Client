#ifndef TEMPERATURE_CONVERSION_H
#define TEMPERATURE_CONVERSION_H

#include <mosquittopp.h>

class mosquitto_callback_wrapper : public mosqpp::mosquittopp {

    void callVoidMethod(const char *func, const char *strSignature, ...);
    void callVoidMethodOnMessage(const char *func, const char *strSignature, const struct mosquitto_message* msg);
public:
    JavaVM *g_vm;
    JNIEnv *g_env;
    jclass g_class;
    jobject g_obj;
    jmethodID g_method;

    mosquitto_callback_wrapper(const char *id);

    ~mosquitto_callback_wrapper();

    void on_connect(int rc);

    void on_message(const struct mosquitto_message *message);

    void on_subscribe(int mid, int qos_count, const int *granted_qos);

    void on_disconnect(int rc);

    void on_publish(int mid);

    void on_unsubscribe(int mid);

    void on_log(int level, const char *str);

    void on_error();
};

#endif

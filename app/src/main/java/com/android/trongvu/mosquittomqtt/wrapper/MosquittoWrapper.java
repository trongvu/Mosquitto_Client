package com.android.trongvu.mosquittomqtt.wrapper;

import android.util.Log;

import com.android.trongvu.mosquittomqtt.message.EventMessage;

import org.greenrobot.eventbus.EventBus;


public class MosquittoWrapper {
    static {
        System.loadLibrary("mosquitto_wrapper");
    }

    public void on_connect(int rc) {
        Log.i("trongvu", "on_connect = " + rc);
        EventBus.getDefault().post(new EventMessage.CONNECT(rc));
    }

    public void on_disconnect(int rc) {
        Log.i("trongvu", "on_disconnect = " + rc);
        EventBus.getDefault().post(new EventMessage.DISCONNECT(rc));
    }

    public void on_publish(int mid) {
        Log.i("trongvu", "on_publish = " + mid);
        EventBus.getDefault().post(new EventMessage.PUBLISH(mid));
    }

    public void on_message(int mid, String topic, int len, String payload, int qos, boolean retain) {
        Log.i("trongvu", "on_message = " + topic);
        EventBus.getDefault().post(new EventMessage.MESSAGE(mid, topic, len, payload, qos, retain));
    }

    public void on_subscribe(int mid, int qos_count, int granted_qos) {
        Log.i("trongvu", "on_subscribe");
        EventBus.getDefault().post(new EventMessage.SUBSCRIBE(mid, qos_count, granted_qos));
    }


    public void on_unsubscribe(int mid) {
        Log.i("trongvu", "on_unsubscribe");
        EventBus.getDefault().post(new EventMessage.UNSUBSCRIBE(mid));
    }

    public void on_log(int level, String msg) {
        EventBus.getDefault().post(new EventMessage.LOG(level, msg));
    }

    public void on_error() {
        EventBus.getDefault().post(new EventMessage.ERROR());
    }

    public native int connect(String host, int port, int keepalive);

    public native int reconnect();

    public native int disconnect();

    public native int publish(int mid, String topic, String payload, int qos, boolean retain);

    public native int subscribe(int mid, String topic, int qos);

    public native int unsubscribe(int mid, String sub);

    public native int loop(int timeout, int max_packets);

    public native void release();

    public native void init(String id);
}

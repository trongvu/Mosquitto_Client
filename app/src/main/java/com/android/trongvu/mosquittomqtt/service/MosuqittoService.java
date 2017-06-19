package com.android.trongvu.mosquittomqtt.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.android.trongvu.mosquittomqtt.wrapper.MosquittoWrapper;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MosuqittoService extends IntentService {
    public static String pkgName = MosuqittoService.class.getPackage().toString() + ".";
    public static String ACTION_LOOP = pkgName + "ACTION_LOOP";
    public static String ACTION_CONNECT = pkgName + "ACTION_CONNECT";
    public static String ACTION_SUBSCRIBE = pkgName + "ACTION_SUBSCRIBE";
    public static String ACTION_UNSUBSCRIBE = pkgName + "ACTION_UNSUBSCRIBE";
    public static String ACTION_PUBLISH = pkgName + "ACTION_PUBLISH";
    public static String ACTION_DISCONNECT = pkgName + "ACTION_DISCONNECT";
    private static MosquittoWrapper mMosquittoWrapper = null;
    Set<String> mSubscribedTopics = new HashSet<String>();
    private AlarmManager alarmMgr;
    private PendingIntent loopIntent;

    public MosuqittoService() {
        super("MosuqittoService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        Log.i("trongvu", "onHandleIntent");
        if (ACTION_LOOP.equals(action)) {
            Log.i("trongvu", "onHandleIntent - ACTION_LOOP");
            requestLoop(10);
        } else if (ACTION_CONNECT.equals(action)) {
            Log.i("trongvu", "onHandleIntent - ACTION_CONNECT");
            String host = intent.getStringExtra("host");
            int port = intent.getIntExtra("port", 1883);
            int keepalive = intent.getIntExtra("keepalive", 60);
            connect(host, port, keepalive);
        } else if (ACTION_DISCONNECT.equals(action)) {
            Log.i("trongvu", "onHandleIntent - ACTION_DISCONNECT");
            disconnect();
        } else if (ACTION_SUBSCRIBE.equals(action)) {
            Log.i("trongvu", "onHandleIntent - ACTION_SUBSCRIBE");
            String topic = intent.getStringExtra("topic");
            int mid = intent.getIntExtra("mid", 0);
            int qos = intent.getIntExtra("qos", 0);
            subscribe(mid, topic, qos);
        } else if (ACTION_PUBLISH.equals(action)) {
            Log.i("trongvu", "onHandleIntent - ACTION_PUBLISH");
            String topic = intent.getStringExtra("topic");
            String message = intent.getStringExtra("message");
            boolean retain = intent.getBooleanExtra("retain", false);
            int mid = intent.getIntExtra("mid", 0);
            int qos = intent.getIntExtra("qos", 0);
            publish(mid, topic, message, qos, retain);
            Log.i("trongvu", "topic - " + topic);
            Log.i("trongvu", "message - " + message);
        } else if (ACTION_UNSUBSCRIBE.equals(action)) {
            Log.i("trongvu", "onHandleIntent - ACTION_UNSUBSCRIBE");
            String topic = intent.getStringExtra("topic");
            int mid = intent.getIntExtra("mid", 0);
            unsubscribe(mid, topic);
        }
    }

    private void requestLoop(int maxTime) {
        if (mMosquittoWrapper == null) {
            Log.i("trongvu", "mMosquittoWrapper = null");
            return;
        }

        if (mMosquittoWrapper.loop(maxTime, 1) != 0) {
            Log.i("trongvu", "loop failed");
            mMosquittoWrapper.reconnect();
        }
    }

    private void subscribe(int mid, String topic, int qos) {
        if (topic != null && topic.length() > 0 && mSubscribedTopics.add(topic) && mMosquittoWrapper != null)
            mMosquittoWrapper.subscribe(mid, topic, qos);
    }

    private void unsubscribe(int mid, String topic) {
        if (topic != null && topic.length() > 0 && mSubscribedTopics.remove(topic) && mMosquittoWrapper != null) {
            mMosquittoWrapper.unsubscribe(mid, topic);
        }
    }

    private void publish(int mid, String topic, String payload, int qos, boolean retain) {
        if (mMosquittoWrapper == null)
            return;
        mMosquittoWrapper.publish(mid, topic, payload, qos, retain);
    }

    private void connect(String host, int port, int keepalive) {
        if (mMosquittoWrapper == null)
            mMosquittoWrapper = new MosquittoWrapper();
        mMosquittoWrapper.init(UUID.randomUUID().toString());
        mMosquittoWrapper.connect(host, port, keepalive);
    }

    private void disconnect() {
        //prevent loop request
        if (alarmMgr != null) {
            alarmMgr.cancel(loopIntent);
            alarmMgr = null;
        }
        // disconnect mqtt
        mMosquittoWrapper.disconnect();
        mMosquittoWrapper = null;
    }
}

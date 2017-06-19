package com.android.trongvu.mosquittomqtt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.trongvu.mosquittomqtt.message.EventMessage;
import com.android.trongvu.mosquittomqtt.service.MosuqittoService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends Activity {
    EditText host, port, topic, message, pub_topic;
    Button connect, subscribe, publish;
    TextView rev_message, status;
    StringBuilder receviedMsg = new StringBuilder();
    boolean isPublishing, isSubscribing, isConnecting;
    ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        host = (EditText) findViewById(R.id.host);
        port = (EditText) findViewById(R.id.port);
        topic = (EditText) findViewById(R.id.topic);
        message = (EditText) findViewById(R.id.message);
        pub_topic = (EditText) findViewById(R.id.pub_topic);

        connect = (Button) findViewById(R.id.btn_connect);
        subscribe = (Button) findViewById(R.id.btn_subscribe);
        publish = (Button) findViewById(R.id.btn_publish);

        rev_message = (TextView) findViewById(R.id.receviedMsg);
        status = (TextView) findViewById(R.id.status);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    public void onClickPublish(View v) {
        isPublishing = true;
        Intent msgIntent = new Intent(MosuqittoService.ACTION_PUBLISH, null, this, MosuqittoService.class);
        msgIntent.putExtra("topic", pub_topic.getText().toString());
        msgIntent.putExtra("message", message.getText().toString());
        startService(msgIntent);
    }

    public void onClickConnect(View v) {
        Log.i("trongvu", "onClickConnect");
        isConnecting = true;
        Intent msgIntent = new Intent(MosuqittoService.ACTION_CONNECT, null, this, MosuqittoService.class);
        msgIntent.putExtra("host", host.getText().toString());
        startService(msgIntent);

        //Schedule a task to run every 5 seconds (or however long you want)
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // Do stuff here!
                Intent msgIntent = new Intent(MosuqittoService.ACTION_LOOP, null, MainActivity.this, MosuqittoService.class);
                startService(msgIntent);
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

    public void onClickSubscribe(View v) {
        isSubscribing = true;
        Intent msgIntent = new Intent(MosuqittoService.ACTION_SUBSCRIBE, null, this, MosuqittoService.class);
        msgIntent.putExtra("topic", topic.getText().toString());
        startService(msgIntent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage.CONNECT event) {
        //connect.setText("Disconnect");
        connect.setEnabled(false);
        host.setEnabled(false);
        port.setEnabled(false);
        subscribe.setEnabled(true);
        publish.setEnabled(true);
        status.setText("Connected");

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage.DISCONNECT event) {
        connect.setText("Connect");
        connect.setEnabled(true);
        host.setEnabled(true);
        port.setEnabled(true);
        subscribe.setEnabled(false);
        publish.setEnabled(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage.MESSAGE event) {
        String msg = "NULL";
        if (event.len > 0) {
            msg = new String(event.payload);
        }
        receviedMsg.append(event.topic + "|" + msg + "\n");
        rev_message.setText(receviedMsg.toString());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage.PUBLISH event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage.SUBSCRIBE event) {
        subscribe.setEnabled(false);
        topic.setEnabled(false);
    }
}

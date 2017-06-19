package com.android.trongvu.mosquittomqtt.message;

/**
 * Created by vungoctrong@gmail.com on 6/16/2017.
 */

public class EventMessage {
    public static enum Status {
        CONNECT,
        DISCONNECT,
        PUBLISH,
        MESSAGE,
        SUBSCRIBE,
        UNSUBSCRIBE,
        LOG,
        ERROR,
    }

    public static class CONNECT {
        public final int rc;

        public CONNECT(int rc) {
            this.rc = rc;
        }
    }

    public static class DISCONNECT {
        public final int rc;

        public DISCONNECT(int rc) {
            this.rc = rc;
        }
    }

    public static class MESSAGE {
        public final int mid;
        public final String topic;
        public final int len;
        public final String payload;
        public final int qos;
        public final boolean retain;

        public MESSAGE(int mid, String topic, int len, String payload, int qos, boolean retain) {
            this.mid = mid;
            this.topic = topic;
            this.len = len;
            this.payload = payload;
            this.qos = qos;
            this.retain = retain;
        }
    }

    public static class SUBSCRIBE {
        public final int mid;
        public final int qos_count;
        public final int granted_qos;

        public SUBSCRIBE(int mid, int qos_count, int granted_qos) {
            this.mid = mid;
            this.qos_count = qos_count;
            this.granted_qos = granted_qos;
        }
    }

    public static class UNSUBSCRIBE {
        public final int mid;

        public UNSUBSCRIBE(int mid) {
            this.mid = mid;
        }
    }

    public static class PUBLISH {
        public final int mid;

        public PUBLISH(int mid) {
            this.mid = mid;
        }
    }

    public static class LOG {
        public final int level;
        public final String message;

        public LOG(int level, String message) {
            this.level = level;
            this.message = message;
        }
    }

    public static class ERROR {
        public ERROR() {
        }
    }
}

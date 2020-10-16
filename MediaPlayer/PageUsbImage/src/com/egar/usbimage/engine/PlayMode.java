package com.egar.usbimage.engine;

public class PlayMode {
    public static final int NONE = -1;
    public static final int LOOP = 1;
    public static final int RANDOM = 2;

    public static String desc(int playMode) {
        switch (playMode) {
            case RANDOM:
                return "RANDOM";
            case LOOP:
            default:
                return "LOOP";
        }
    }
}

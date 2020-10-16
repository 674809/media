package com.egar.usbimage.engine;

public class PlayState {
    public static final int PLAY = 1;
    public static final int PAUSE = 2;

    public static String desc(int state) {
        switch (state) {
            case PAUSE:
                return "PAUSE";
            case PLAY:
            default:
                return "PLAY";
        }
    }
}

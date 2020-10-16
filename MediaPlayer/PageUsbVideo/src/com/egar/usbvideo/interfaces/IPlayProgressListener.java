package com.egar.usbvideo.interfaces;

/**
 * Play progress Listener
 *
 */
public interface IPlayProgressListener {
    /**
     * Progress change callback
     */
    void onProgressChanged(String mediaPath, int progress, int duration);
}

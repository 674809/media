package com.egar.btmusic.fragment;

import com.egar.btproxy.common.BTDevice;
import com.egar.btproxy.common.BTLog;
import com.egar.btproxy.common.IBTProxy;
import com.egar.btproxy.music.BTMusicClient;
import com.egar.btproxy.music.BTMusicClientCallback;
import com.egar.btproxy.music.BTMusicSongInfo;
import com.egar.mediaui.Icallback.IAppApplication;
import com.egar.mediaui.App;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/14 13:50
 * @see {@link }
 */
public class BTMusicApp implements IAppApplication {

    public static BTMusicClient sMusicClient;

    @Override
    public void appOnCreate() {

        BTMusicClient.create(App.getContext(), new IBTProxy.ServiceListener() {
            @Override
            public void onServiceConnected(int i, IBTProxy ibtProxy) {
                if (i == IBTProxy.BTMUSIC) {
                    sMusicClient = (BTMusicClient) ibtProxy;
                    sMusicClient.registerApp(new BTMusicClientCallback() {

                        @Override
                        public void onRemoteDeviceConnectionStateChanged(BTDevice device, int
                                state) {
                            super.onRemoteDeviceConnectionStateChanged(device, state);
                            BTLog.i("onConnectionStateChanged device" + device + " state " + state);
                            if (musicObservers != null) {
                                musicObservers.onConnectionStateChanged(device, state);
                            } else {
                                BTLog.i("musicObservers is null");
                            }
                        }

                        @Override
                        public void onTrackInfoChanged(BTMusicSongInfo songInfo) {
                            super.onTrackInfoChanged(songInfo);
                            BTLog.i("onMetadataChanged  songInfo " + songInfo);
                            if (musicObservers != null) {
                                musicObservers.onMetadataChanged(songInfo);
                            } else {
                                BTLog.i("musicObservers is null");
                            }

                            if (musicObservers != null) {
                                musicObservers.onPlaybackPositionChanged(songInfo.getPosition());
                                BTLog.i("onPlaybackPositionChanged  position " + songInfo.getPosition());
                            } else {
                                BTLog.i("musicObservers is null");
                            }
                        }

                        @Override
                        public void onPlayStateChanged(int state) {
                            super.onPlayStateChanged(state);
                            BTLog.i("onPlayStateChanged " + state);
                            if (musicObservers != null) {
                                musicObservers.onPlayStateChanged(state);
                            } else {
                                BTLog.i("musicObservers is null");
                            }
                        }
                    });
                }
            }

            @Override
            public void onServiceDisconnected(int i) {
                sMusicClient = null;
            }
        });
    }

    @Override
    public void appOnTerminate() {

        sMusicClient = null;
    }

    public static boolean isAvailable() {

        return sMusicClient != null;

    }

    private static OnMusicObserver musicObservers;

    public static void registerMusicObserver(OnMusicObserver observer) {

        musicObservers = observer;
    }

    public interface OnMusicObserver {

        void onConnectionStateChanged(BTDevice device, int state);

        void onPlayStateChanged(int state);

        void onMetadataChanged(BTMusicSongInfo songInfo);

        void onPlaybackPositionChanged(long position);

    }


}

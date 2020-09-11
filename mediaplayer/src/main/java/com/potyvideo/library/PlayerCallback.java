package com.potyvideo.library;

public interface PlayerCallback {
    void onPlayerStateChange(int state);

    void onProgressUpdate(int value);

    void onError();

    void onMediaFinished();
}

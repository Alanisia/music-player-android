// IPlayerAidlInterface.aidl
package com.thundersoft.android.musicplayer.server;

// Declare any non-default types here with import statements

interface IPlayerAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void play();
    boolean control();
    void previous();
    void next(boolean over);
    void seekTo(int second);
    void reset();
}
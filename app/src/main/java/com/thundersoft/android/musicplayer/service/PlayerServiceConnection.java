package com.thundersoft.android.musicplayer.service;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.thundersoft.android.musicplayer.server.IPlayerAidlInterface;

public class PlayerServiceConnection implements ServiceConnection {
    @SuppressLint("StaticFieldLeak")
    private static final PlayerServiceConnection instance = new PlayerServiceConnection();
    private IPlayerAidlInterface iPlayerAidlInterface;
    private PlayerService.PlayerBinder binder;
    private Context context;

    private PlayerServiceConnection() {}

    public static PlayerServiceConnection getInstance() {
        return instance;
    }

    public PlayerService.PlayerBinder getBinder() {
        return binder;
    }

    public IPlayerAidlInterface getIPlayerAidlInterface() {
        return iPlayerAidlInterface;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        binder = (PlayerService.PlayerBinder) service;
        iPlayerAidlInterface = IPlayerAidlInterface.Stub.asInterface(service);
        binder.setContext(context);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }
}

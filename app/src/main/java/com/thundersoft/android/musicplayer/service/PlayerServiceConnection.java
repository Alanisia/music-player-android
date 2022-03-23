package com.thundersoft.android.musicplayer.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

public class PlayerServiceConnection implements ServiceConnection {
    private static final PlayerServiceConnection instance = new PlayerServiceConnection();
    private PlayerService.PlayerBinder binder;
    private Context context;

    private PlayerServiceConnection() {}

    public static PlayerServiceConnection getInstance() {
        return instance;
    }

    public PlayerService.PlayerBinder getBinder() {
        return binder;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        binder = (PlayerService.PlayerBinder) service;
        binder.setContext(context);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }
}

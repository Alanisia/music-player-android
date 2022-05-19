package com.thundersoft.android.musicplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.thundersoft.android.musicplayer.R;
import com.thundersoft.android.musicplayer.player.PlayMode;
import com.thundersoft.android.musicplayer.player.Player;
import com.thundersoft.android.musicplayer.util.Constants;

import java.util.Objects;

public class PlayerNotification {
    private static final String TAG = PlayerNotification.class.getSimpleName();
    private static final String CHANNEL_ID = "music_player";
    private final Player player = Player.getInstance();
    private final PlayerServiceConnection playerServiceConnection = PlayerServiceConnection.getInstance();
    private final Context context;
    private NotificationBroadcastReceiver notificationReceiver;
    private RemoteViews notificationLayout;
    private Notification notification;
    private NotificationManager notificationManager;

    public PlayerNotification(Context context) {
        this.context = context;
    }

    public void initRemoteView() {
        notificationLayout = new RemoteViews(context.getPackageName(), R.layout.notification_player);
        setOnClickPendingIntent(Constants.NOTIFICATION_PREV, R.id.notification_prev);
        setOnClickPendingIntent(Constants.NOTIFICATION_PLAY, R.id.notification_control);
        setOnClickPendingIntent(Constants.NOTIFICATION_PAUSE, R.id.notification_control);
        setOnClickPendingIntent(Constants.NOTIFICATION_NEXT, R.id.notification_next);
    }

    public void initNotification() {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.album)
                .setCustomContentView(notificationLayout)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(false)
                .build();
    }

    public void showNotification() {
        notificationManager.notify(Constants.NOTIFICATION_ID, notification);
    }

    public void registerBroadcast() {
        notificationReceiver = new NotificationBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.NOTIFICATION_PREV);
        intentFilter.addAction(Constants.NOTIFICATION_NEXT);
        intentFilter.addAction(Constants.NOTIFICATION_PLAY);
        intentFilter.addAction(Constants.NOTIFICATION_PAUSE);
        context.registerReceiver(notificationReceiver, intentFilter);
    }

    public void unregisterBroadcast() {
        if (notificationReceiver != null) {
            context.unregisterReceiver(notificationReceiver);
        }
    }

    private void setOnClickPendingIntent(String action, int viewId) {
        Intent intent = new Intent(action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        notificationLayout.setOnClickPendingIntent(viewId, pendingIntent);
    }

    private class NotificationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // take place of AIDL after refactoring
            PlayerService.PlayerBinder binder = playerServiceConnection.getBinder();
            switch (intent.getAction()) {
                case Constants.NOTIFICATION_PREV:
                    binder.previous();
                    notificationLayout.setTextViewText(R.id.notification_current_title, player.current().getTitle());
                    notificationLayout.setTextViewText(R.id.notification_current_artists, player.current().getArtist());
                    break;
                case Constants.NOTIFICATION_PLAY:
                case Constants.NOTIFICATION_PAUSE:
                    notificationLayout.setImageViewResource(R.id.notification_control, binder.control() ?
                            R.drawable.ic_baseline_pause_circle_outline_24 :
                            R.drawable.ic_baseline_play_circle_outline_24);
                    break;
                case Constants.NOTIFICATION_NEXT:
                    binder.next(false);
                    notificationLayout.setTextViewText(R.id.notification_current_title, player.current().getTitle());
                    notificationLayout.setTextViewText(R.id.notification_current_artists, player.current().getArtist());
                    break;
                default:
                    break;
            }
        }
    }

}

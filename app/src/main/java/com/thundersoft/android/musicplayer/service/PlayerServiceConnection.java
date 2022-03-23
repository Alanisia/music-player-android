package com.thundersoft.android.musicplayer.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Future;
//import java.util.concurrent.FutureTask;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeoutException;
//import java.util.concurrent.locks.Condition;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;

public class PlayerServiceConnection implements ServiceConnection {
    private static final String TAG = PlayerServiceConnection.class.getSimpleName();
    private volatile PlayerService.PlayerBinder binder;
    private Context context;
//    private static PlayerServiceConnection instance;
//
//    private PlayerServiceConnection() {
//        instance = new PlayerServiceConnection().setContext(context);
//    }
//
//    public static void setInstance(PlayerServiceConnection instance,
//                                   Context context) {
//        PlayerServiceConnection.instance = instance;
//
//    }

//    public static PlayerServiceConnection getInstance() {
//        return instance;
//    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "onServiceConnected: " + name);
        Log.d(TAG, "onServiceConnected: " + service);
        binder = (PlayerService.PlayerBinder) service;
        binder.setContext(context);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }

    public PlayerService.PlayerBinder getBinder() {
        Log.d(TAG, "getBinder");
//        GetBinderFuture task = new GetBinderFuture();
//        new Thread(() -> {
//            for (;;)
//                if (binder != null) {
//                    task.setBinder(binder);
//                    break;
//                }
//        }).start();
//        binder = task.get();
//        Log.d(TAG, "getBinder" + (binder != null));
        return binder;
    }

    public PlayerServiceConnection setContext(Context context) {
        this.context = context;
        return this;
    }

//    private static class GetBinderFuture implements Future<PlayerService.PlayerBinder> {
//        private final Lock lock = new ReentrantLock();
//        private final Condition condition = lock.newCondition();
//        private PlayerService.PlayerBinder binder;
//        private boolean done;
//
//        @Override
//        public boolean cancel(boolean mayInterruptIfRunning) {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public boolean isCancelled() {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public boolean isDone() {
//            return done;
//        }
//
//        @Override
//        public PlayerService.PlayerBinder get() {
//            try {
//                return get(5, TimeUnit.SECONDS);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        @Override
//        public PlayerService.PlayerBinder get(long timeout, TimeUnit unit) throws InterruptedException {
//            lock.lock();
//            while (!done) {
////                if (binder == null)
//                    condition.await();
//            }
//            lock.unlock();
//            return binder;
//        }
//
//        public void setBinder(PlayerService.PlayerBinder binder) {
//            lock.lock();
//            this.binder = binder;
//            condition.signal();
//            done = true;
//            lock.unlock();
//        }
//    }

}

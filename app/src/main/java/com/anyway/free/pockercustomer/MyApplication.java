package com.anyway.free.pockercustomer;

import android.app.Application;
import android.content.Context;

import com.anyway.free.pockercustomer.socket.ServerSocketHelper;

import de.kp.rtspcamera.poker.utils.SoundUtils;

/**
 * Created by Administrator on 2016/12/31.
 */
public class MyApplication extends Application {

    private static Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = getApplicationContext();

        SoundUtils.getInstance().initSound(this);
      //  SocketHelper.getInstance().sendIpBroadcast();
     //   ServerSocketHelper.getInstance().startListening();
    }

    public static Context getAppContext(){
        return mAppContext;
    }
}

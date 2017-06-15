package com.anyway.free.pockercustomer.socket;


import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.anyway.free.pockercustomer.Activity.LoginActivity;
import com.anyway.free.pockercustomer.Activity.RtspViewerActivity;
import com.anyway.free.pockercustomer.Activity.ServerActivity;
import com.anyway.free.pockercustomer.MyApplication;
import com.anyway.free.pockercustomer.Utils.Constants;

public class PlayerActivityController {

    private static String TAG = "PlayerActivityController";
    private static PlayerActivityController mInstance = null;

    private boolean mIsPlaying = false;
    private boolean mIsTry = false;
    private Activity playerActivity;

    private PlayerActivityController (){}

    public static PlayerActivityController getInstance () {

        if (mInstance == null) {
            mInstance = new PlayerActivityController();
        }
        return mInstance;
    }

    public synchronized void registPlayActivity (Activity player, boolean isTry) {

        this.playerActivity = player;
        mIsPlaying = true;
        mIsTry = isTry;
    }

    public synchronized void unregistPlayActivity () {
        this.playerActivity = null;
        this.mIsPlaying = false;
    }

    public synchronized void startPlayerActivity () {

        Log.d(TAG,"startPlayerActivity --- mIsTry :"+mIsTry +"mIsPlaying = "+mIsPlaying+" LoginActivity.isOnBackGroud = "+LoginActivity.isOnBackGroud);
//        if (!mIsPlaying /*&& !LoginActivity.isOnBackGroud*/) {
//            Toast.makeText(MyApplication.getAppContext(), "网络重新连接，继续播放",Toast.LENGTH_LONG).show();
//            Log.d(TAG,"startPlayerActivity --- mIsTry :"+mIsTry);
//            Intent intent = new Intent();
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setAction("com.anyway.free.palyerActivity");
//            intent.putExtra(Constants.INTENT_PARAM_TRY, mIsTry);
//            MyApplication.getAppContext().startActivity(intent);
//        }

        if ( playerActivity != null) {

            if (playerActivity instanceof ServerActivity) {

                Toast.makeText(MyApplication.getAppContext(), "网络重新连接，继续播放",Toast.LENGTH_LONG).show();
                ((ServerActivity) playerActivity).startRtsp();
            }

            if (playerActivity instanceof RtspViewerActivity) {

                Toast.makeText(MyApplication.getAppContext(), "网络重新连接，继续播放",Toast.LENGTH_LONG).show();
                ((RtspViewerActivity) playerActivity).startRtsp();
            }
        }

    }

    public synchronized void stopPlayerActivity () {

//        if (mIsPlaying) {
//            if (playerActivity != null) {
//                Toast.makeText(MyApplication.getAppContext(), "网络断开连接，停止播放",Toast.LENGTH_LONG).show();
//                Log.d(TAG,"stopPlayerActivity --- mIsTry :"+mIsTry);
//                playerActivity.finish();
//            }
//        }
        if ( playerActivity != null) {

            if (playerActivity instanceof ServerActivity) {

                Toast.makeText(MyApplication.getAppContext(), "网络断开连接，停止播放",Toast.LENGTH_LONG).show();
                ((ServerActivity) playerActivity).stopRtsp();
            }

            if (playerActivity instanceof RtspViewerActivity) {

                Toast.makeText(MyApplication.getAppContext(), "网络断开连接，停止播放",Toast.LENGTH_LONG).show();
                ((RtspViewerActivity) playerActivity).stopRtsp();
            }
        }
    }

}

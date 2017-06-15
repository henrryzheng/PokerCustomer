package com.anyway.free.pockercustomer.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.net.wifi.WifiManager;
import android.widget.Button;
import android.widget.Toast;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.anyway.free.pockercustomer.R;
import com.anyway.free.pockercustomer.Utils.Constants;
import com.anyway.free.pockercustomer.Utils.DisplayUtil;
import com.anyway.free.pockercustomer.Utils.SettingUtils;
import com.anyway.free.pockercustomer.socket.PlayerActivityController;
import com.orangelabs.core.ims.protocol.rtp.media.MediaSample;
import com.orangelabs.platform.AndroidFactory;
import com.orangelabs.provider.settings.RcsSettings;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import de.kp.net.rtp.viewer.OnConnectListener;
import de.kp.net.rtp.viewer.RtpRenderer;
import de.kp.net.rtsp.client.SetUpParams;
import de.kp.rtspcamera.poker.data.PokerCustomerSetting;
import de.kp.rtspcamera.poker.utils.SoundUtils;

public class ServerActivity extends Activity implements OnConnectListener, SurfaceHolder.Callback {


    private Thread decodeThread;
    private Thread startRenderThread;
    private TextView log;
    private TextView loading;
    private SurfaceView surfaceView;
    private Button backBtn;
    private MediaCodec decoder;
    private final static int TIME_INTERNAL = 10;
 //   private List<byte[]> h264data = new LinkedList<>();
    private List<MediaSample> h264data = new LinkedList<MediaSample>();
    private byte[] sps;
    private byte[] pps;

    private RtpRenderer renderer = null;
    private String rtspConnect = "rtsp://192.168.43.1:8080/video";
    private SetUpParams params = new SetUpParams();
    private Bitmap rgbFrame;
    private boolean mIsLoading = false;
	private static final int RENDER_START = 0x10;
    private static final int RENDER_STOP = 0x11;
    private static final int STOP_LOADING = 0x12;
    private static final int START_AUTH = 0x13;
    private boolean isTry = false;
    private final int TRY_INTERVAL = 5*60*1000; //����5����

    private WifiManager wifiManager;
    private Button testCloseWifiBtn;
    private Button testOpenWifiBtn;

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case STOP_LOADING:

                    hideLoading();
                    break;
                case RENDER_START:

                    renderer.open();
                    renderer.start();

                    break;
                case RENDER_STOP:
                    Toast.makeText(ServerActivity.this, "服务端已退出" , Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case START_AUTH:
                    startAuthActivity();
                    break;
            }

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidFactory.setApplicationContext(getApplicationContext());
        // Instantiate the settings manager
        RcsSettings.createInstance(getApplicationContext());
        setContentView(R.layout.activity_server);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        parseIntent();
//        log = (TextView) findViewById(R.id.server_text);
//        log.setMovementMethod(ScrollingMovementMethod.getInstance());
        loading = (TextView) findViewById(R.id.server_text);
        surfaceView = (SurfaceView) findViewById(R.id.surface);
        backBtn = (Button) findViewById(R.id.server_btn_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initViewParams();
        surfaceView.getHolder().addCallback(this);
        showLoading();
 //       SoundUtils.getInstance().initSound(this);
        Log.d("xxx", "initViewParams params : GameType = " + params.getGameType() + " peopleNum = " + params.getPeopleNum()
                + "broType = " + params.getBroadcastType() + " playType = " + params.getPlayType());


    }

    private void parseIntent(){
        Intent intent = getIntent();
        isTry = intent.getBooleanExtra(Constants.INTENT_PARAM_TRY, false);
        PlayerActivityController.getInstance().registPlayActivity(this,isTry);
        if (isTry){
            startTryTimer();
        }
    }

    @Override
    protected void onDestroy() {
        PlayerActivityController.getInstance().unregistPlayActivity();
        super.onDestroy();
    }

    private void startTryTimer(){
        mHandler.sendEmptyMessageDelayed(START_AUTH, TRY_INTERVAL);
    }

    private void startAuthActivity(){
        Intent intent = new Intent();
        intent.setClass(ServerActivity.this, AuthActivity.class);
        intent.putExtra(Constants.INTENT_PARAM_TRY, isTry);
        startActivity(intent);
        finish();
    }

    private void initViewParams(){

        params.setGameType(SettingUtils.getGameType(this));
        params.setPeopleNum(SettingUtils.getPeopleNum(this));
        params.setBroadcastType(SettingUtils.getBroadcatType(this));
        params.setPlayType(SettingUtils.getPlayType(this));
        params.setEV(SettingUtils.getEV(this));
        Log.d("xxx", "initViewParams params : GameType = " + params.getGameType() + " peopleNum = " + params.getPeopleNum()
                + "broType = " + params.getBroadcastType() + " playType = " + params.getPlayType()+ " ev = " + params.getEV());

        ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
        Point p = DisplayUtil.getScreenMetrics(this);
        params.width = p.x ;
        params.height = (int)(params.width / 1.33);
        surfaceView.setLayoutParams(params);

        testOpenWifiBtn = (Button) findViewById(R.id.server_btn1);
        testCloseWifiBtn = (Button) findViewById(R.id.server_btn2);

        testOpenWifiBtn.setOnClickListener(buttonClickListener);
        testCloseWifiBtn.setOnClickListener(buttonClickListener);

    }

    private synchronized void showLoading() {
        mIsLoading = true;
        loading.setVisibility(View.VISIBLE);

//        ImageView progress = (ImageView) this.findViewById(R.id.progress_bar);
//        progress.setVisibility(View.VISIBLE);
//        Animation animation =  DisplayUtil.getProgressBarAnimation(this);
//        if (animation != null) {
//            progress.clearAnimation();
//            progress.setAnimation(animation);
//            animation.startNow();
//        }
    }

    private synchronized void hideLoading() {

        if (mIsLoading) {

            loading.setVisibility(View.INVISIBLE);
            mIsLoading = false;
        }

 //       ImageView progress = (ImageView) this.findViewById(R.id.progress_bar);
 //       ImageView progress = (ImageView) this.findViewById(R.id.progress_bar);
//        Animation animation = DisplayUtil.getProgressBarAnimation(this);
//        if (animation != null) {
//            progress.clearAnimation();
//            progress.setAnimation(animation);
//            animation.startNow();
//        }
 //       progress.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onPause() {
        Log.i("xxx", "onPause");

        SoundUtils.getInstance().stopAllSound();
        mHandler.removeMessages(START_AUTH);
        try {
            if (renderer != null){
                renderer.close();
            }

//            if (decoder != null) {
//                decoder.stop();
//            }
//            decoder = null;
        }catch (Exception e) {
            Log.i("xxx", "onPause err : "+e);
        }
        if (decodeThread != null) {
            if (decodeThread.isAlive()) {
                decodeThread.interrupt();
            }
            decodeThread = null;
        }

        if (startRenderThread != null) {
            if (startRenderThread.isAlive()) {
                startRenderThread.interrupt();

            }
            startRenderThread = null;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.i("xxx", "onResume");
        super.onResume();
        if (renderer != null) {
            renderer.stop();
            renderer.close();
            renderer = null;
        }

        if (startRenderThread == null) {
            startRenderThread = new Thread(rendRun);
            startRenderThread.start();
        }
        Log.i("xxx", "onResume renderer started");

    }

    public void stopRtsp () {

        Log.i("xxx", "stopRtsp");
        try {
            if (renderer != null){
                renderer.close();
            }
        }catch (Exception e) {
            Log.i("xxx", "onPause err : "+e);
        }

        if (startRenderThread != null) {
            if (startRenderThread.isAlive()) {
                startRenderThread.interrupt();

            }
            startRenderThread = null;
        }

    }

    public void startRtsp () {

        if (renderer != null) {
            renderer.stop();
            renderer.close();
            renderer = null;
        }
        if (startRenderThread == null) {
            startRenderThread = new Thread(rendRun);
            startRenderThread.start();
        }
        Log.i("xxx", "startRtsp");
    }


    private Runnable rendRun = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                renderer = new RtpRenderer(rtspConnect, ServerActivity.this,params);
                Log.i("xxx", "onCreate 6");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d("xxx", "  onCreate error : "+ e);
            }
//			incomingRenderer.setVideoSurface(incomingVideoView);
//			incomingRenderer.open();
//			incomingRenderer.start();
        }
    };

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        initMediaDecode();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        Log.d("xxx", " surfaceChanged format : " + format + " width = " + width + " height = " + height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("xxx"," surfaceDestroyed  ");

    }

    private void initMediaDecode() {
        Log.e("xxx", "initMediaDecode");
//        byte[] sps = { 0, 0, 0, 1, 103, 100, 0, 40, -84, 52, -59, 1, -32, 17, 31, 120, 11, 80, 16, 16, 31, 0, 0, 3, 3, -23, 0, 0, -22, 96, -108 };
//        byte[] pps = { 0, 0, 0, 1, 104, -18, 60, -128 };
        byte[] sps = { 0, 0, 0, 1, 103, 66, 0, 41, -101, 64, 80, 30};
        byte[] pps = { 0, 0, 0, 1, 104, -78, 49};
//        byte[] sps = { 103, 66, 0, 41, -101, 64, 80, 30};
//        byte[] pps = { 104, -78, 49};

        MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", 640, 480);
        mediaFormat.setByteBuffer("csd-0"  , ByteBuffer.wrap(sps));
        mediaFormat.setByteBuffer("csd-1", ByteBuffer.wrap(pps));
        mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 640*480);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 500000);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
//        format.setByteBuffer("csd-0"  , ByteBuffer.wrap(sps));
  //      format.setByteBuffer("csd-1", ByteBuffer.wrap(pps));
        try {
            decoder = MediaCodec.createDecoderByType("video/avc");
            decoder.configure(mediaFormat, surfaceView.getHolder().getSurface(), null, 0);
            decoder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (decodeThread == null) {
            decodeThread = new DecodeThread();
        }

        decodeThread.start();
    }



    @Override
    public void onConnect() {
        // TODO Auto-generated method stub
        Log.i("xxx", "onConnect renderbegin");
        mHandler.sendEmptyMessageDelayed(RENDER_START, 100);
    }

    @Override
    public void onPlayerStop() {

        Log.i("xxx", "onStop");
        if (renderer != null){
            renderer.stop();
            renderer.close();
        }

        mHandler.sendEmptyMessage(RENDER_STOP);

    }

    @Override
    public void onReceiveResult(int[] var1) {
 //       Log.i("xxx", "onReceiveResult");
//		for(int i = 0; i< var1.length; i++) {
//			Log.i(TAG, "onReceiveResult result var1[" + i + "] = " + var1[i]);
//		}
        if (var1 != null){
            int length = var1.length;
            if(var1[0] == PokerCustomerSetting.BroadcastType.DANPAI_HUASE_ZIFU){
                if(length>1){
                    SoundUtils.getInstance().playSoundColorAndNum(var1[1]);
                }
            }
            else if(var1[0] == PokerCustomerSetting.BroadcastType.DANPAI_ZIFU){
                if(length>1){
                    SoundUtils.getInstance().playSoundNum(var1[1]);
                }
            }
            else if(var1[0] == PokerCustomerSetting.BroadcastType.DAXIAO_ZUIDA){
                if(length>1){
                    SoundUtils.getInstance().playSingleSoundEx(SoundUtils.KEY_POKER_SORT_BASE + var1[1]);
                }
            }
            else if(var1[0] == PokerCustomerSetting.BroadcastType.DAXIAO_ZUIDA_TYPE){
                if(length>1){
                    SoundUtils.getInstance().playSingleSoundEx(SoundUtils.KEY_POKER_SORT_BASE + var1[1]);
                }
                // TODO: 2017/1/13
            }
            else if(var1[0] == PokerCustomerSetting.BroadcastType.DAXIAO_FIRST_SECOND){
                if(length > 2){
                    int[] playInts = new int[2];
                    for(int i = 0; i < 2; i++){
                        playInts[i] = SoundUtils.KEY_POKER_SORT_BASE + var1[i+1];
                    }
                    SoundUtils.getInstance().playMultiSoundsEx(playInts,true);

                }
                else if (length == 2){
                    SoundUtils.getInstance().playSingleSoundEx(SoundUtils.KEY_POKER_SORT_BASE + var1[1]);
                }
            }
            else if(var1[0] == PokerCustomerSetting.BroadcastType.DAXIAO_EVERYONE){
                if(length > 1){
                    int[] playInts = new int[var1.length - 1];
                    for(int i = 0; i < var1.length - 1; i++){
                        playInts[i] = SoundUtils.KEY_POKER_SORT_BASE + var1[i + 1];
                    }
                    SoundUtils.getInstance().playMultiSoundsEx(playInts,true);
                }
            }
        }
    }

    @Override
    public void onFrameData(MediaSample sample) {
    //    Log.i("xxx", "onFrameData : "+sample.getData().length + " l = "+sample.getTimeStamp());
        if (mIsLoading) {
            mHandler.sendEmptyMessageDelayed(STOP_LOADING,1000);
        }
        h264data.add(sample);
    }


    private class DecodeThread extends Thread {

        MediaCodec.BufferInfo mBufferInfo;
        int mCount = 0;

        public DecodeThread() {
            Log.e("TAG", "DecodeThread");
            mBufferInfo = new MediaCodec.BufferInfo();
        }

        @Override
        public void run() {
            while (true) {
//                try {
//                    if (h264data.size() > 0) {
//                        byte[] data = h264data.get(0);
//                        h264data.remove(0);
////                        Log.e("Media", "save to file data size:" + data.length);
////                        Util.save(data, 0, data.length, path, true);
//
//                        ByteBuffer[] inputBuffers = decoder.getInputBuffers();
//                        int inputBufferIndex = decoder.dequeueInputBuffer(100);
//                        if (inputBufferIndex >= 0) {
//                            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
//                            inputBuffer.clear();
//                            inputBuffer.put(data);
//                            decoder.queueInputBuffer(inputBufferIndex, 0, data.length, mCount * TIME_INTERNAL, 0);
//                        }
//
//                        // Get output buffer index
//                        int outputBufferIndex = decoder.dequeueOutputBuffer(mBufferInfo, 100);
//                        while (outputBufferIndex >= 0) {
//                            Log.e("TAG", "onFrame index:" + outputBufferIndex);
//                            decoder.releaseOutputBuffer(outputBufferIndex, true);
//                            outputBufferIndex = decoder.dequeueOutputBuffer(mBufferInfo, 0);
//                        }
//                    } else {
//                        sleep(TIME_INTERNAL);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Log.d("TAG"," DecodeThread error : "+ e);
//                }
          try {
                if (h264data.size() > 0 && h264data.get(0).getData().length > 3 ) {

                        MediaSample sample =  h264data.remove(0);
                        byte[] data = sample.getData();
//                        ByteBuffer[] inputBuffers = decoder.getInputBuffers();
//
                        int length = data.length;
                        int offset = 0;
 //                       Log.d("xxx"," data length = " + length + " offset = "+offset + " data[0] = "+data[0] +
 //                        "data[1] = "+data[1] + "data[2] = "+data[2]);
//
//                        if (data.length < 3) {
//                            continue;
//                        }
//
//                        if (data[0] == 0 && data[1] == 0 && data[2] == 1) {
//
//
//                        } else {
//
//                            offset = 3;
//                            length = length - 3;
//                        }
//
//                        int inputBufferIndex = decoder.dequeueInputBuffer(-1);
//                        if (inputBufferIndex >= 0) {
//                            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
//                            inputBuffer.clear();
//                            inputBuffer.put(data, offset, length);
//                            decoder.queueInputBuffer(inputBufferIndex, 0, length, mCount * 1000000 / 15, 0);
//                            mCount++;
//                            Log.d("xxx","mCount = "+mCount);
//                        }
//
//                        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
//                        int outputBufferIndex = decoder.dequeueOutputBuffer(bufferInfo,0);
//                        while (outputBufferIndex >= 0) {
//                            decoder.releaseOutputBuffer(outputBufferIndex, true);
//                            outputBufferIndex = decoder.dequeueOutputBuffer(bufferInfo, 0);
//                    }

                    ByteBuffer[] inputBuffers = decoder.getInputBuffers();
                    int inputBufferIndex = decoder.dequeueInputBuffer(-1);
                    if (inputBufferIndex >= 0) {
                        ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                        long timestamp = mCount++ * 1000000 / 15;
             //           Log.d("xxx","offerDecoder timestamp: " +timestamp+" inputSize: "+length + " bytes");
                        inputBuffer.clear();
                        inputBuffer.put(data,0,length);
                        decoder.queueInputBuffer(inputBufferIndex, 0, length, timestamp, 0);
                    }

                    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                    int outputBufferIndex = decoder.dequeueOutputBuffer(bufferInfo,0);
                    while (outputBufferIndex >= 0) {
                  //      Log.d("xxx","offerDecoder OutputBufSize:"+bufferInfo.size+ " bytes written");

                        //If a valid surface was specified when configuring the codec,
                        //passing true renders this output buffer to the surface.
                        decoder.releaseOutputBuffer(outputBufferIndex, true);
                        outputBufferIndex = decoder.dequeueOutputBuffer(bufferInfo, 0);
                    }
                }
            } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("xxx"," DecodeThread error : "+ e);
                }

            }
        }
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == testOpenWifiBtn){
                Log.d("NetworkReceiver", "==testOpenWifiBtn");
                wifiManager.setWifiEnabled(true);
            }

            if (v == testCloseWifiBtn){
                Log.d("NetworkReceiver", "==testCloseWifiBtn");
                wifiManager.setWifiEnabled(false);
            }
        }
    };
}

package com.anyway.free.pockercustomer.Activity;

/**
 * This is the most minimal viewer for RtspCamera app
 * 
 * @author Peter Arwanitis (arwanitis@dr-kruscheundpartner.de)
 *
 */
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.anyway.free.pockercustomer.R;
import com.anyway.free.pockercustomer.Utils.Constants;
import com.anyway.free.pockercustomer.Utils.DisplayUtil;
import com.anyway.free.pockercustomer.Utils.SettingUtils;
import com.anyway.free.pockercustomer.socket.PlayerActivityController;
import com.orangelabs.core.ims.protocol.rtp.media.MediaSample;
import com.orangelabs.platform.AndroidFactory;
import com.orangelabs.provider.settings.RcsSettings;
import com.orangelabs.service.api.client.media.video.VideoSurfaceView;

import de.kp.net.rtp.viewer.RtpRenderer;
import de.kp.net.rtp.viewer.RtpVideoRenderer;
import de.kp.net.rtp.viewer.OnConnectListener;
import de.kp.net.rtsp.client.SetUpParams;
import de.kp.rtspcamera.poker.data.PokerCustomerSetting;
import de.kp.rtspcamera.poker.utils.SoundUtils;

public class RtspViewerActivity extends Activity implements OnConnectListener{

	/**
	 * Video renderer
	 */
	private RtpVideoRenderer incomingRenderer = null;

	/**
	 * Video preview
	 */
	private VideoSurfaceView incomingVideoView = null;
	private Button backBtn;
	/**
	 * hardcoded rtsp server path
	 */
	private String rtspConnect = "rtsp://192.168.43.1:8080/video";
	// private String rtsp =
	// "rtsp://184.72.239.149/vod/mp4:BigBuckBunny_175k.mov";

	private int videoHeight ;

	private int videoWidth ;
	private Thread startRenderThread;

	private String TAG = "RtspViewer";
	private SetUpParams params = new SetUpParams();

	private static final int MESSAGE_START_RENDER = 1;

	private boolean isTry = false;
	private final int TRY_INTERVAL = 5*60*1000; //����5����
	private static final int RENDER_START = 0x10;
	private static final int RENDER_STOP = 0x11;
	private static final int STOP_LOADING = 0x12;
	private static final int START_AUTH = 0x13;

	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

				case RENDER_START:
					incomingRenderer.setVideoSurface(incomingVideoView);
					incomingRenderer.open();
					incomingRenderer.start();

					break;
				case RENDER_STOP:
					Toast.makeText(RtspViewerActivity.this, "服务端已退出", Toast.LENGTH_LONG).show();
					finish();
					break;
				case START_AUTH:
					startAuthActivity();
					break;
			}

		}

	};

	@Override
	public void onCreate(Bundle icicle) {

		Log.i(TAG, "onCreate");
		super.onCreate(icicle);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// Set application context ... skipping FileFactory
		AndroidFactory.setApplicationContext(getApplicationContext());
		// Instantiate the settings manager
		RcsSettings.createInstance(getApplicationContext());
		setContentView(R.layout.videoview);
		incomingVideoView = (VideoSurfaceView) findViewById(R.id.incoming_video_view);
		backBtn = (Button) findViewById(R.id.videoview_btn_back);
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		parseIntent();

		initViewParams();
		SoundUtils.getInstance().initSound(this);

	}

	private void parseIntent(){
		Intent intent = getIntent();
		isTry = intent.getBooleanExtra(Constants.INTENT_PARAM_TRY, false);
		PlayerActivityController.getInstance().registPlayActivity(this,isTry);
		if (isTry){
			startTryTimer();
		}

	}

	public void stopRtsp () {

		Log.i("xxx", "stopRtsp");
		try {
			if (incomingRenderer != null){
				incomingRenderer.close();
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

		if (incomingRenderer != null) {
			incomingRenderer.stop();
			incomingRenderer.close();
			incomingRenderer = null;
		}
		if (startRenderThread == null) {
			startRenderThread = new Thread(rendRun);
			startRenderThread.start();
		}
		Log.i("xxx", "startRtsp");
	}

	@Override
	public void onPlayerStop() {
		Log.i("xxx", "onStop");
		if (incomingRenderer != null){
			incomingRenderer.stop();
			incomingRenderer.close();
		}

		mHandler.sendEmptyMessage(RENDER_STOP);
	}

	private void startTryTimer(){
		mHandler.sendEmptyMessageDelayed(START_AUTH, TRY_INTERVAL);
	}

	private void startAuthActivity(){
		Intent intent = new Intent();
		intent.setClass(RtspViewerActivity.this, AuthActivity.class);
		intent.putExtra(Constants.INTENT_PARAM_TRY, isTry);
		startActivity(intent);
		finish();
	}

	private void initViewParams(){

		params.setGameType(SettingUtils.getGameType(this));
		params.setPeopleNum(SettingUtils.getPeopleNum(this));
		params.setBroadcastType(SettingUtils.getBroadcatType(this));
		params.setPlayType(SettingUtils.getPlayType(this));
		Log.d("xxx","initViewParams params : GameType = "+ params.getGameType() + " peopleNum = "+params.getPeopleNum()
				+ "broType = "+ params.getBroadcastType() + " playType = "+params.getPlayType());

		ViewGroup.LayoutParams params = incomingVideoView.getLayoutParams();
		Point p = DisplayUtil.getScreenMetrics(this);
		params.width = p.x * 3 / 2;
		params.height = (int)(params.width / 1.38);
		incomingVideoView.setLayoutParams(params);
		incomingVideoView.setAspectRatio(videoWidth, videoHeight);
	}


	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		SoundUtils.getInstance().stopAllSound();
		mHandler.removeMessages(START_AUTH);
		try {
			if (incomingRenderer != null){
				incomingRenderer.stop();
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
		super.onPause();

	}
	
	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		super.onResume();
		if (incomingRenderer != null) {
			incomingRenderer.stop();
			incomingRenderer.close();
			incomingRenderer = null;
		}

		if (startRenderThread == null) {
			startRenderThread = new Thread(rendRun);
			startRenderThread.start();
		}
		Log.i("xxx", "onResume renderer started");
		
	}
	
	private Runnable rendRun = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				incomingRenderer = new RtpVideoRenderer(rtspConnect, RtspViewerActivity.this,params);
				Log.i(TAG, "onCreate 6");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d(TAG, "  onCreate error : "+ e);
			}
		}
	};

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		PlayerActivityController.getInstance().unregistPlayActivity();
		super.onDestroy();
	}

	@Override
	public void onConnect() {
		// TODO Auto-generated method stub
		Log.i(TAG, "onConnect renderbegin");

		mHandler.sendEmptyMessageDelayed(RENDER_START, 100);
	}

	@Override
	public void onReceiveResult(int[] var1) {
		Log.i(TAG, "onReceiveResult");
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
	public void onFrameData(MediaSample mediaSample) {

	}
}

package com.anyway.free.pockercustomer.Utils;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

/**
 * CameraActivity.java
 * <p/>
 * Created by Administrator on 2016/12/30.
 * <p/>
 * Copyright (c) 2016
 */

public class DisplayUtil {
	private static final String TAG = "peer/DisplayUtil";
	/**
	 * dipתpx
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue){            
		final float scale = context.getResources().getDisplayMetrics().density;                 
		return (int)(dipValue * scale + 0.5f);         
	}     
	
	/**
	 * pxתdip
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue){                
		final float scale = context.getResources().getDisplayMetrics().density;                 
		return (int)(pxValue / scale + 0.5f);         
	} 
	
	/**
	 * ��ȡ��Ļ��Ⱥ͸߶ȣ���λΪpx
	 * @param context
	 * @return
	 */
	public static Point getScreenMetrics(Context context){
		DisplayMetrics dm =context.getResources().getDisplayMetrics();
		int w_screen = dm.widthPixels;
		int h_screen = dm.heightPixels;
		Log.i(TAG, "Screen---Width = " + w_screen + " Height = " + h_screen + " densityDpi = " + dm.densityDpi);
		return new Point(w_screen, h_screen);
		
	}
	
	/**
	 * ��ȡ��Ļ�����
	 * @param context
	 * @return
	 */
//	public static float getScreenRate(Context context){
//		Point P = getScreenMetrics(context);
//		float H = P.y;
//		float W = P.x;
//		return (H/W);
//	}
//
//	public static Animation getProgressBarAnimation(Context context) {
//		Animation animation = AnimationUtils.loadAnimation(context,
//				R.anim.peer_anima_load_item);
//		LinearInterpolator lin = new LinearInterpolator();
//		animation.setInterpolator(lin);
//		return animation;
//	}
}

package com.anyway.free.pockercustomer.Utils;

import android.content.Context;

import com.anyway.free.pockercustomer.preference.SettingPreference;

/**
 * Created by Administrator on 2016/12/20.
 */
public class SettingUtils {
    public static void saveGameType(int position, Context context) {
        SettingPreference preference = new SettingPreference(context, Constants.SETTING_INFO_FILE);
        preference.save(Constants.SETTING_GAME_TYPE, position);
    }

    public static void savePeopleNum(int position, Context context) {
        SettingPreference preference = new SettingPreference(context, Constants.SETTING_INFO_FILE);
        preference.save(Constants.SETTING_PEOPLE_NUM, position);
    }

    public static void saveBroadcatType(int position, Context context) {
        SettingPreference preference = new SettingPreference(context, Constants.SETTING_INFO_FILE);
        preference.save(Constants.SETTING_BROADCAST_TYPE, position);
    }

    public static void savePlayType(int position, Context context) {
        SettingPreference preference = new SettingPreference(context, Constants.SETTING_INFO_FILE);
        preference.save(Constants.SETTING_PLAY_TYPE, position);
    }

    public static void saveEV(int position, Context context){
        SettingPreference preference = new SettingPreference(context, Constants.SETTING_INFO_FILE);
        preference.save(Constants.SETTING_EV, position);
    }

    public static void saveTryCount(int tryCount, Context context) {
        SettingPreference preference = new SettingPreference(context, Constants.SETTING_INFO_FILE);
        preference.save(Constants.TRY_COUNT, tryCount);
    }

    public static void saveAuthSuccess(boolean authSuccess, Context context){
        SettingPreference preference = new SettingPreference(context, Constants.SETTING_INFO_FILE);
        preference.save(Constants.AUTH_SUCCESS, authSuccess);
    }

    public static void savePassword(String password, Context context){
        SettingPreference preference = new SettingPreference(context, Constants.SETTING_INFO_FILE);
        preference.save(Constants.LOGIN_PASSWORD, password);
    }

    public static void savePasswordIsSave(boolean isSave, Context context){
        SettingPreference preference = new SettingPreference(context, Constants.SETTING_INFO_FILE);
        preference.save(Constants.ISSAVE_PASSWORD, isSave);
    }


    public static int getGameType(Context context) {
        SettingPreference preference = new SettingPreference(context, Constants.SETTING_INFO_FILE);
        return preference.getInt(Constants.SETTING_GAME_TYPE, Constants.NO_SETTING_ID);
    }

    public static int getPeopleNum(Context context) {
        SettingPreference preference = new SettingPreference(context, Constants.SETTING_INFO_FILE);
        return preference.getInt(Constants.SETTING_PEOPLE_NUM, Constants.NO_SETTING_ID);
    }

    public static int getBroadcatType(Context context) {
        SettingPreference preference = new SettingPreference(context, Constants.SETTING_INFO_FILE);
        return preference.getInt(Constants.SETTING_BROADCAST_TYPE, Constants.NO_SETTING_ID);
    }

    public static int getPlayType(Context context) {
        SettingPreference preference = new SettingPreference(context, Constants.SETTING_INFO_FILE);
        return preference.getInt(Constants.SETTING_PLAY_TYPE, Constants.NO_SETTING_ID);
    }

    public static int getEV(Context context) {
        SettingPreference preference = new SettingPreference(context, Constants.SETTING_INFO_FILE);
        return preference.getInt(Constants.SETTING_EV, Constants.DEFAULT_EV_SETTING_ID);
    }

    public static int getTryCount(Context context) {
        SettingPreference preference = new SettingPreference(context, Constants.SETTING_INFO_FILE);
        return preference.getInt(Constants.TRY_COUNT, Constants.NO_SETTING_ID);
    }

    public static boolean getAuthSuccess(Context context){
        SettingPreference preference = new SettingPreference(context, Constants.SETTING_INFO_FILE);
        return preference.getBoolean(Constants.AUTH_SUCCESS, Constants.DEFAULT_AUTH_SUCCESS);
    }

    public static String getPassword(Context context){
        SettingPreference preference = new SettingPreference(context, Constants.SETTING_INFO_FILE);
        return preference.get(Constants.LOGIN_PASSWORD, Constants.DEFAULT_LOGIN_PASSWORD);
    }

    public static boolean getIsSavePassword(Context context){
        SettingPreference preference = new SettingPreference(context, Constants.SETTING_INFO_FILE);
        return preference.getBoolean(Constants.ISSAVE_PASSWORD, Constants.DEFAULT_ISSAVE_PASSWORD);
    }

}

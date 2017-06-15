package com.anyway.free.pockercustomer.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Administrator on 2016/12/20.
 */
public class SettingPreference {
    private static final String LOG_TAG = "SettingPreference";
    private SharedPreferences mSharedPref;

    public static SettingPreference get(Context ctx, String name) {
        return new SettingPreference(ctx, name);
    }

    public SettingPreference(Context ctx, String name) {
        if (ctx != null) {
            mSharedPref = ctx.getSharedPreferences(name, Context.MODE_MULTI_PROCESS + Context.MODE_WORLD_READABLE);
        } else {
            Log.e(LOG_TAG, "EXCEPTION ---- SettingPreference(Context ctx, String name) --- Context IS NULL");
        }
    }

    public void save(String key, String value) {
        if (mSharedPref != null) {
            mSharedPref.edit().putString(key, value).apply();
        }
    }
    public void save(String key, long value) {
        if (mSharedPref != null) {
            mSharedPref.edit().putLong(key, value).apply();
        }
    }
    public void save(String key, int value) {
        if (mSharedPref != null) {
            mSharedPref.edit().putInt(key, value).apply();
        }
    }

    public void save(String key, boolean value) {
        if (mSharedPref != null) {
            mSharedPref.edit().putBoolean(key, value).apply();
        }
    }

    public String get(String key) {
        return get(key, "");
    }

    public String get(String key, String defaultValue) {
        if (mSharedPref == null) {
            return defaultValue;
        }
        return mSharedPref.getString(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        if (mSharedPref == null) {
            return defaultValue;
        }
        return mSharedPref.getInt(key, defaultValue);
    }
    public long getLong(String key, long defaultValue) {
        if (mSharedPref == null) {
            return defaultValue;
        }
        return mSharedPref.getLong(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if (mSharedPref == null) {
            return defaultValue;
        }
        return mSharedPref.getBoolean(key, defaultValue);
    }

    /**
     * 保存
     * @param ctx
     * @param name
     * @param key
     * @param value
     */
    public static void save(Context ctx, String name, String key, String value) {
        SettingPreference preference = get(ctx, name);
        if (preference != null) {
            preference.save(key, value);
        }
    }

    /**
     * 读取
     * @param ctx
     * @param name
     * @param key
     */
    public static String get(Context ctx, String name, String key) {
        return get(ctx, name, key, "");
    }

    /**
     * 读取
     * @param ctx
     * @param name
     * @param key
     */
    public static String get(Context ctx, String name, String key, String defaultValue) {
        SettingPreference preference = get(ctx, name);
        if(preference == null){
            return defaultValue;
        }
        return preference.get(key, defaultValue);
    }

    public void clear() {
        if (mSharedPref != null) {
            mSharedPref.edit().clear().apply();
        }
    }
}

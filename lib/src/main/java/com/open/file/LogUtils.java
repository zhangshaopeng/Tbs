package com.open.file;

import android.text.TextUtils;
import android.util.Log;

/**
 * p>@Describe:log
 * p>@Author:zhangshaop
 * p>@Data:2021/1/18.
 */
public final class LogUtils {
    private static final String LOG_TAG = "log_demo";
    private static boolean DEBUG = false;

    public static void init(boolean isOpenLog) {
        DEBUG = isOpenLog;
    }

    private LogUtils() {
    }

    public static void e(String log) {
        if (DEBUG && !TextUtils.isEmpty(log)) {
            Log.e(LOG_TAG, "" + log);
        }
    }

    public static void i(String log) {
        if (DEBUG && !TextUtils.isEmpty(log)) {
            Log.i(LOG_TAG, log);
        }
    }

    public static void i(String tag, String log) {
        if (DEBUG && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(log)) {
            Log.i(tag, log);
        }
    }

    public static void d(String tag, String log) {
        if (DEBUG && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(log)) {
            Log.d(tag, log);
        }
    }

    public static void d(String log) {
        if (DEBUG && !TextUtils.isEmpty(log)) {
            Log.d(LOG_TAG, log);
        }
    }

    public static void e(String tag, String log) {
        if (DEBUG && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(log)) {
            Log.e(tag, log);
        }
    }


}
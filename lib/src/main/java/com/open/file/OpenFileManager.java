package com.open.file;

import android.content.Context;
import android.graphics.Color;

import com.tencent.smtt.sdk.QbSdk;

/**
 * p>@Describe:初始化管理者
 * p>@Author:zhangshaop
 * p>@Data:2021/1/19.
 */
public class OpenFileManager {
    public static int backgroundColor = -1;

    /**
     * @param context
     * @param isOpenLog 日志开关
     */
    public static void initSDK(Context context, boolean isOpenLog) {
        QbSdk.setDownloadWithoutWifi(true);
        QbSdk.initX5Environment(context, null);
        LogUtils.init(isOpenLog);
    }

    /**
     * @param context
     * @param isOpenLog       日志开关
     * @param BackgroundColor 北京颜色
     */
    public static void initSDK(Context context, boolean isOpenLog, int BackgroundColor) {
        QbSdk.setDownloadWithoutWifi(true);
        QbSdk.initX5Environment(context, null);
        LogUtils.init(isOpenLog);
        backgroundColor = BackgroundColor;
    }
}

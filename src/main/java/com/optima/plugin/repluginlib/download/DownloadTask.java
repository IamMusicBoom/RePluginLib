package com.optima.plugin.repluginlib.download;

import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.optima.plugin.repluginlib.Logger;
import com.optima.plugin.repluginlib.pluginUtils.P_Context;
import com.optima.plugin.repluginlib.utils.NotificationUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.HashMap;

/**
 * create by wma
 * on 2020/9/1 0001
 * 下载任务
 */
public class DownloadTask {
    private final String TAG = DownloadTask.class.getSimpleName();
    private final String PLUGIN_FILE_LOCAL_PATH;
    public static final String FOLDER_NAME = "plugin";
    private Context mContext;
    /**
     * 通知栏
     */
    private NotificationUtils notificationUtils;
    private NotificationCompat.Builder notificationBuilder;

    private Callback.Cancelable downloadCancelable;

    /**
     * 任务执行map
     */
    private HashMap<String, Callback.Cancelable> cancelableHashMap = new HashMap<>();
    /**
     * 下载文件名字，插件名字
     */
    private String fileName;
    /**
     * 下载地址
     */
    private String downloadUrl;
    /**
     * 下载回调
     */
    private CallbackListener callback;
    /**
     * 下载进度条ID
     */
    private int notificationId;

    public DownloadTask(String fileName, String downloadUrl, int notificationId) {
        mContext = P_Context.getContext();
        PLUGIN_FILE_LOCAL_PATH = mContext.getExternalFilesDir(FOLDER_NAME).getAbsolutePath();
        notificationUtils = new NotificationUtils();
        notificationBuilder = notificationUtils.createDownloadBuilder(100, 0);
        this.fileName = fileName;
        this.downloadUrl = downloadUrl;
        this.notificationId = notificationId;
    }

    public DownloadTask(String fileName, String downloadUrl) {
        this(fileName, downloadUrl, -1);
    }


    public void excuse(CallbackListener callback) {
        this.callback = callback;
        new DownloadThread().start();
    }


    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }


    public void cancelDownLoadTasks() {
        for (String pluginName : cancelableHashMap.keySet()) {
            cancelDownloadTask(pluginName);
        }
    }

    public void cancelDownloadTask(String pluginName) {
        if (cancelableHashMap.containsKey(pluginName)) {
            Callback.Cancelable cancelable = cancelableHashMap.get(pluginName);
            if (cancelable != null) {
                cancelable.cancel();
                cancelable = null;
                cancelableHashMap.put(pluginName, cancelable);
            }
        }
    }

    class DownloadThread extends Thread {


        @Override
        public void run() {
            super.run();
            downloadCancelable = download();
            cancelableHashMap.put(fileName, downloadCancelable);
        }

        private Callback.Cancelable download() {
            RequestParams params = new RequestParams(downloadUrl);
            params.setSaveFilePath(PLUGIN_FILE_LOCAL_PATH + File.separator + fileName);
            Callback.Cancelable cancelable = x.http().get(params, new Callback.ProgressCallback<File>() {
                @Override
                public void onSuccess(File result) {
                    if (callback != null) {
                        callback.onSuccess(result);
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Logger.e(TAG, "onError: ex = " + ex);
                    if (callback != null) {
                        callback.onError(ex, isOnCallback);
                    }
                }

                @Override
                public void onCancelled(CancelledException cex) {
                    Logger.d(TAG, "onCancelled: ");
                    if (callback != null) {
                        callback.onCancelled(cex);
                    }
                }

                @Override
                public void onFinished() {
                    Logger.d(TAG, "onFinished: ");
                    notificationUtils.cancelNotification(notificationId);
                    if (callback != null) {
                        callback.onFinished();
                    }
                }

                @Override
                public void onWaiting() {
                    Logger.d(TAG, "onWaiting: ");
                    if (callback != null) {
                        callback.onWaiting();
                    }
                }

                @Override
                public void onStarted() {
                    Logger.d(TAG, "onStarted: ");
                    notificationUtils.showNotification(notificationId, notificationBuilder.build());
                    if (callback != null) {
                        callback.onStarted();
                    }
                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    Logger.d(TAG, "onLoading: ");
                    notificationUtils.updateDownloadNotification(notificationId, 100, (int) ((current * 100) / total), notificationBuilder);
                    if (callback != null) {
                        callback.onLoading(total, current, isDownloading);
                    }
                }
            });
            return cancelable;
        }
    }
}

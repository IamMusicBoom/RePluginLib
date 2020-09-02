package com.optima.plugin.repluginlib;

import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.optima.plugin.repluginlib.pluginUtils.P_Manager;
import com.optima.plugin.repluginlib.utils.NotificationUtils;
import com.qihoo360.replugin.model.PluginInfo;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.HashMap;

/**
 * create by wma
 * on 2020/9/1 0001
 */
public class DownloadTask {
    private final String TAG = DownloadTask.class.getSimpleName();
    private final String PLUGIN_FILE_LOCAL_PATH;
    public static final String FOLDER_NAME = "plugin";
    private Context mContext;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationUtils notificationUtils;
    private Callback.Cancelable downloadCancelable;
    private HashMap<String,Callback.Cancelable> cancelableHashMap = new HashMap<>();

    public DownloadTask(Context context,int notificationId) {
        mContext = context;
        PLUGIN_FILE_LOCAL_PATH = mContext.getExternalFilesDir(FOLDER_NAME).getAbsolutePath();
        notificationUtils = new NotificationUtils();
        notificationBuilder = notificationUtils.createDownloadBuilder(100, 0);
        this.notificationId = notificationId;
    }


    /**
     * 下载至本地后是否自动安装
     */
    private boolean isAutoInstall = true;

    /**
     * 安装完成后是否直接释放dex文件，释放dex文件为耗时操作，需要放置在子线程中
     */
    private boolean isAutoPreload = true;

    /**
     * 下载进度条ID
     */
    private int notificationId;


    public void excuse(String pluginName, String url) {
        new DownloadThread(pluginName, url).start();
    }

    public boolean isAutoInstall() {
        return isAutoInstall;
    }

    public void setAutoInstall(boolean autoInstall) {
        isAutoInstall = autoInstall;
    }

    public boolean isAutoPreload() {
        return isAutoPreload;
    }

    public void setAutoPreload(boolean autoPreload) {
        isAutoPreload = autoPreload;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }


    public void cancelDownLoadTasks(){
        for (String pluginName : cancelableHashMap.keySet()) {
            cancelDownloadTask(pluginName);
        }
    }

    public void cancelDownloadTask(String pluginName){
        Callback.Cancelable cancelable = cancelableHashMap.get(pluginName);
        if(cancelable != null){
            cancelable.cancel();
            cancelable = null;
        }
    }

    class DownloadThread extends Thread {
        /**
         * 插件名字
         */
        private String fileName;
        /**
         * 插件下载地址
         */
        private String downloadUrl;


        public DownloadThread(String fileName, String downloadUrl) {
            this.fileName = fileName;
            this.downloadUrl = downloadUrl;
        }

        @Override
        public void run() {
            super.run();
            downloadCancelable = download();
            cancelableHashMap.put(fileName,downloadCancelable);
        }

        private Callback.Cancelable download() {
            RequestParams params = new RequestParams(downloadUrl);
            params.setSaveFilePath(PLUGIN_FILE_LOCAL_PATH + File.separator + fileName);
            Callback.Cancelable cancelable = x.http().get(params, new Callback.ProgressCallback<File>() {
                @Override
                public void onSuccess(File result) {
                    if (isAutoInstall) {
                        PluginInfo pluginInfo = P_Manager.install(result.getAbsolutePath());
                        if (pluginInfo != null && isAutoPreload) {
                            P_Manager.preload(pluginInfo.getName());
                        } else {
                            Logger.d(TAG, "onSuccess: pluginInfo = " + pluginInfo + " isAutoPreload = " + isAutoPreload);
                        }
                    }else{
                        Logger.d(TAG, "onSuccess: isAutoInstall = " + isAutoInstall + " file = " + result.getAbsolutePath());
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Logger.e(TAG, "onError: ex = " + ex);
                }

                @Override
                public void onCancelled(CancelledException cex) {
                    Logger.d(TAG, "onCancelled: ");
                }

                @Override
                public void onFinished() {
                    Logger.d(TAG, "onFinished: ");
                    notificationUtils.cancelNotification(notificationId);
                }

                @Override
                public void onWaiting() {
                    Logger.d(TAG, "onWaiting: ");
                }

                @Override
                public void onStarted() {
                    Logger.d(TAG, "onStarted: ");
                    notificationUtils.showNotification(notificationId, notificationBuilder.build());
                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    Logger.d(TAG, "onLoading: ");
                    notificationUtils.updateDownloadNotification(notificationId, 100, (int) ((current * 100) / total), notificationBuilder);
                }
            });
            return cancelable;
        }
    }


}

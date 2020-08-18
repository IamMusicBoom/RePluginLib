package com.optima.plugin.repluginlib.PluginUtils;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import com.optima.plugin.repluginlib.base.BaseActivity;
import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.component.service.PluginServiceClient;

import java.security.PublicKey;

/**
 * create by wma
 * on 2020/8/14 0014
 * <p>
 * Context 管理类
 */
public class P_Context {

    private static Context context;

    /**
     * 获取当前Context,需要在当前应用Application中注册
     *
     * @return
     */
    public static Context getContext() {
        if (context == null) {
            throw new RuntimeException("context = null，请在你 Application 的 onCreate 方法中调用 P_Context.setContext(this)");
        }
        return context;
    }

    public static void setContext(Context context) {
        P_Context.context = context;
    }

    /**
     * 获取宿主Context
     *
     * @return
     */
    public static Context getHostContext() {
        try {
            return RePlugin.getHostContext();
        } catch (NoSuchMethodError error) {
            return getContext();
        }
    }


    /**
     * 获取某个插件的Context;
     *
     * @param pluginName
     * @return
     */
    public static Context getPluginContext(String pluginName) {
        Context context = RePlugin.fetchContext(pluginName);
        return context;
    }


    /**
     * 跳转插件Acitivyt
     *
     * @param context 跳转Activity的当前Context
     * @param intent  可以使用{@link P_Context#createIntent(String, String)}创建，也可以自己创建
     */
    public static void startPluginActivity(Context context, Intent intent) {
        RePlugin.startActivity(context, intent);
    }


    /**
     * @param alias        插件别名，或者插件包名
     * @param allClassName 跳转组件的全路径 包名加类名
     * @return
     */
    public static Intent createIntent(String alias, String allClassName) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(alias, allClassName));
        return intent;
    }

    /**
     * 跳转插件Activity并且得到返回值
     *
     * @param activity
     * @param intent
     * @param requestCode
     */
    public static void startPluginActivityForResult(Activity activity, Intent intent, int requestCode) {
        RePlugin.startActivityForResult(activity, intent, requestCode);
    }

    /**
     * 开启插件服务
     *
     * @param context
     * @param intent
     */
    public static ComponentName startPluginService(Context context, Intent intent) {
        ComponentName componentName = PluginServiceClient.startService(context, intent);
        return componentName;
    }

    /**
     * 停止插件服务
     *
     * @param context
     * @param intent
     */
    public static boolean stopPluginService(Context context, Intent intent) {
        boolean b = PluginServiceClient.stopService(context, intent);
        return b;
    }

    /**
     * 绑定插件服务
     * @param context
     * @param intent
     * @param connection
     */
    public static boolean bindPluginService(Context context, Intent intent, ServiceConnection connection) {
        boolean b = PluginServiceClient.bindService(context, intent, connection, Context.BIND_AUTO_CREATE);
        return b;
    }

    /**
     * 解绑插件服务
     * @param context
     * @param connection
     */
    public static void unBindPluginService(Context context, ServiceConnection connection) {
        PluginServiceClient.unbindService(context, connection);
    }
}

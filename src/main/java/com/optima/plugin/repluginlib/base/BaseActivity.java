package com.optima.plugin.repluginlib.base;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.optima.plugin.repluginlib.BuildConfig;
import com.optima.plugin.repluginlib.Logger;
import com.optima.plugin.repluginlib.PluginUtils.P_Constants;
import com.optima.plugin.repluginlib.PluginUtils.P_Context;

/**
 * create by wma
 * on 2020/8/17 0017
 */
public class BaseActivity extends AppCompatActivity {
    public String TAG;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        setTitle(this.getClass().getSimpleName());
        Intent intent = getIntent();
        if (intent != null) {
            String stringExtra = intent.getStringExtra(P_Constants.INTENT_KEY);
            Logger.d(TAG, "onCreate: stringExtra = " + stringExtra);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.d(TAG, "onActivityResult: requestCode = " + requestCode);
        if (data != null) {
            Logger.d(TAG, "onActivityResult: data = " + data.getStringExtra(P_Constants.INTENT_KEY));
        }
    }

    /**
     * 适用于：
     * （1）宿主跳转插件
     * （2）插件之间跳转插件
     * （3）插件跳转宿主
     * @param intent
     * @param isOuterClass 是否为其他插件或宿主Activity
     *
     */
    public void startActivity(Intent intent, boolean isOuterClass) {
        ComponentName component = intent.getComponent();
        String packageName = component.getPackageName();
        Logger.d(TAG, "startActivity: packageName = " + packageName);
        intent.putExtra(P_Constants.INTENT_KEY, this.getClass().getSimpleName());
        if (isOuterClass) {
            if (P_Constants.HOST_PACKAGE_NAME.equals(packageName)) {// 跳转宿主
                super.startActivity(intent);
            } else {
                P_Context.startPluginActivity(BaseActivity.this, intent);// 跳转插件
            }
        } else {
            super.startActivity(intent);// 插件内部跳转
        }
    }

    /**
     * @param intent
     * @param requestCode
     * @param isOuterClass 是否为其他插件或宿主Activity
     */
    public void startActivityForResult(Intent intent, int requestCode, boolean isOuterClass) {
        ComponentName component = intent.getComponent();
        String packageName = component.getPackageName();
        Logger.d(TAG, "startActivityForResult: packageName = " + packageName);
        intent.putExtra(P_Constants.INTENT_KEY, this.getClass().getSimpleName());
        if (isOuterClass) {
            if (P_Constants.HOST_PACKAGE_NAME.equals(packageName)) {// 跳转宿主
                super.startActivityForResult(intent, requestCode);
            } else {
                P_Context.startPluginActivityForResult(BaseActivity.this, intent, requestCode);// 跳转插件
            }
        } else {
            super.startActivityForResult(intent, requestCode);// 插件内部跳转
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra(P_Constants.INTENT_KEY, "WMA-OK");
        setResult(RESULT_OK, data);
        super.onBackPressed();
    }
}

package com.huanju.chajianhuatest;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;

import com.huanju.chajianhuatest.ams.AMSHookHelper;

import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * @author 刘镓旗
 * @date 17/2/21
 */
public class MyApplication extends Application {
    private static Context sContext;

    private AssetManager assetManager;
    private Resources newResource;
    private Resources.Theme mTheme;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sContext = base;

        loadChaJianAPK();
        initResource();
    }

    private void loadChaJianAPK(){
        try {
            new Thread(){
                @Override
                public void run() {
                    //创建一个属于我们自己插件的ClassLoader，我们分析过只能使用DexClassLoader
                    String cachePath = getCacheDir().getAbsolutePath();
                    String apkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/chajian_demo.apk";
                    DexClassLoader mClassLoader = new DexClassLoader(apkPath, cachePath,cachePath, getClassLoader());
                    MyHookHelper.inject(mClassLoader);
                    try {
                        AMSHookHelper.hookActivityManagerNative();
                        AMSHookHelper.hookActivityThreadHandler();
                    } catch (Exception e) {
                        Log.e("Main","加载异常了 = " + e.getMessage());
                        e.printStackTrace();
                    }
                    Log.e("Main","加载完成" );
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initResource(){
        try {
            //创建我们自己的Resource
            String apkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/chajian_demo.apk";

            assetManager = AssetManager.class.newInstance();

            Method addAssetPathMethod = assetManager.getClass().getDeclaredMethod("addAssetPath", String.class);
            addAssetPathMethod.setAccessible(true);
            addAssetPathMethod.invoke(assetManager, apkPath);

            Method ensureStringBlocks = AssetManager.class.getDeclaredMethod("ensureStringBlocks");
            ensureStringBlocks.setAccessible(true);
            ensureStringBlocks.invoke(assetManager);

            Resources supResource = getResources();
            newResource = new Resources(assetManager, supResource.getDisplayMetrics(), supResource.getConfiguration());
            mTheme = newResource.newTheme();
            mTheme.setTo(super.getTheme());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public AssetManager getAssets() {
        return assetManager == null ? super.getAssets() : assetManager;
    }

    @Override
    public Resources getResources() {
        return newResource == null ? super.getResources() : newResource;
    }

    @Override
    public Resources.Theme getTheme() {
        return mTheme == null ? super.getTheme() : mTheme;
    }

    public static Context getContext() {
        return sContext;
    }
}

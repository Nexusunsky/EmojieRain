package com.nexusunsky.liuhao.raindeom;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

/**
 * application类（分享需要用到FrontiaApplication，所以继承此类）
 */
public class AppContext extends Application {

    public static long startMills;
    public static AppContext appContext;


    public static AppContext getInstance() {
        return appContext;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        //        MultiDex.install(this);
        //        Nuwa.init(this);
        // attachBaseContext时期初始化处理
        // 由于补丁配置信息存储在AppConfigProvider,所以需要先初始化applicationContext后才不影响
        // AppConfigProvider的初始化,也就是调用AppManagerSetup
        //        SetupCenterBusiness.setUpInApplicationAttachBaseContextMainThread(this);
        // 加载补丁
        //        File patchFile = PatchUtil.getPatchFile();
        //        if (patchFile != null) {
        //            String patchPath = patchFile.getPath();
        //            XLog.w(TAG, "patchPath: " + patchPath);
        //            Nuwa.loadPatch(this, patchPath);
        //        }
        //        this.logTime.log("MultiDex install", XLog.DEBUG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        LeakCanary.install(this);
        //        SetupCenterBusiness.setUpInApplicationMainThread(this);
        //        if (SystemUtils.isCurrentAppProcess(this)) {
        //            SetupCenterBusiness.setUpSingelProcess(this);
        //        }
        //        logTime.log("AppConext.onCreate()", XLog.DEBUG);
        //        FreelineCore.init(this);
        //        Elog.init(this);
        //        Elog.open();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}

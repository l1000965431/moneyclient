package com.dragoneye.wjjt.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.config.BroadcastConfig;
import com.dragoneye.wjjt.config.PreferencesConfig;
import com.dragoneye.wjjt.dao.MyDaoMaster;
import com.dragoneye.wjjt.http.HttpClient;
import com.dragoneye.wjjt.tool.PreferencesHelper;
import com.dragoneye.wjjt.tool.ToolMaster;
import com.dragoneye.wjjt.user.UserBase;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.smssdk.SMSSDK;

/**
 * Created by happysky on 15-6-30.
 */
public class MyApplication extends Application {
    public ArrayList<Integer> images = new ArrayList<>();
    private PushAgent mPushAgent;

    @Override
    public void onCreate() {
        super.onCreate();
        MyDaoMaster.init(this);
        HttpClient.initHttpClient(this);
//        createTestData();
        initImageLoader();
        AnalyticsConfig.enableEncrypt(true);
        MobclickAgent.updateOnlineConfig(this);
        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.enable();
        PushAgent.getInstance(this).onAppStart();
        /*SMSSDK.initSDK(this, PreferencesConfig.SHARESDKAPPKEY, PreferencesConfig.SHARESDKAPPSECRET);*/

        //Log.d("UMENG TEST", getDeviceInfo(this));
        CreatePushMessageHandle();

        images.add(R.mipmap.icon_albums);
    }

    /**
     * 友盟测试函数
     * 获取设备信息
     *
     * @param context
     * @return
     */
    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String device_id = tm.getDeviceId();

            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);

            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }

            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            }

            json.put("device_id", device_id);

            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static void createTestData(){
//        ProjectDao dao = MyDaoMaster.getDaoSession().getProjectDao();
//        ProjectImageDao projectImageDao = MyDaoMaster.getDaoSession().getProjectImageDao();
//
//        long id = dao.insert(new Project(null, ProjectStatusConfig.IN_PROGRESS, 0, "1", "test"));
//        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display001_1).toString()));
//        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display001_2).toString()));
//        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display001_3).toString()));
//
//        id = dao.insert(new Project(null, ProjectStatusConfig.IN_PROGRESS, 0, "2", "test"));
//        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display002_1).toString()));
//
//        id = dao.insert(new Project(null, ProjectStatusConfig.IN_PROGRESS, 0, "3", "test"));
//        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display003_1).toString()));
//        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display003_2).toString()));
//        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display003_3).toString()));
//        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display003_4).toString()));
//        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display003_5).toString()));
//        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display003_6).toString()));
//        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display003_7).toString()));
//
//        id = dao.insert(new Project(null, ProjectStatusConfig.IN_PROGRESS, 0, "4", "test"));
//        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display004_1).toString()));
//        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display004_2).toString()));
//        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display004_3).toString()));
//        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display004_4).toString()));
//
//        id = dao.insert(new Project(null, ProjectStatusConfig.IN_PROGRESS, 0, "5", "test"));
//        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display005_1).toString()));
//        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display005_2).toString()));
//        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display005_3).toString()));
//        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display005_4).toString()));
//
//        id = dao.insert(new Project(null, ProjectStatusConfig.IN_PROGRESS, 0, "6", "test"));
//        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display006_1).toString()));
//        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display006_2).toString()));
//
//        id = dao.insert(new Project(null, ProjectStatusConfig.IN_PROGRESS, 0, "7", "test"));
//        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display007_1).toString()));
//
//    }

    private void initImageLoader() {
        File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "imageLoader/Cache");
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .showImageOnLoading(R.mipmap.icon_albums)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheExtraOptions(480, 800)
                .diskCacheExtraOptions(480, 800, null)
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCacheFileCount(100)
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000))
                .writeDebugLogs()
                .defaultDisplayImageOptions(options)
                .build();
        ImageLoader.getInstance().init(configuration);
    }


    //运用list来保存们每一个activity是关键
    private static List<Activity> mList = new LinkedList<Activity>();

    // add Activity
    public static void addActivity(Activity activity) {
        mList.add(activity);
    }

    public static void removeActivity(Activity a) {
        mList.remove(a);
    }

    //关闭每一个list内的activity
    public static void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
            mList.clear();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    public UserBase getCurrentUser(Context context) {
        if (mUser == null) {
            String json = PreferenceManager.getDefaultSharedPreferences(context).getString(
                    PreferencesConfig.LAST_LOGIN_USER_DATA, "");
            try {
                mUser = ToolMaster.gsonInstance().fromJson(json, UserBase.class);
            } catch (Exception e) {
                mUser = null;
            }
        }
        return mUser;
    }

    public void setCurrentUser(Context context, UserBase user) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PreferencesConfig.LAST_LOGIN_USER_DATA,
                ToolMaster.gsonInstance().toJson(mUser)).apply();
        mUser = user;
    }

    private UserBase mUser;
    private String mToken;

    public String getToken(Context context) {
        return mToken;
    }

    public void setToken(Context context, String token) {
        mToken = token;
    }

    public void setUserLoginSuccess(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(
                PreferencesConfig.IS_USER_LOGIN_DATA_OUT_OF_DATE, false
        ).apply();
    }

    public boolean isUserOutOfDate(Context context) {
        if (getCurrentUser(context) == null || PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                PreferencesConfig.IS_USER_LOGIN_DATA_OUT_OF_DATE, true)) {
            return true;
        }

        return false;
    }

    public void setUserOutOfDate(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(
                PreferencesConfig.IS_USER_LOGIN_DATA_OUT_OF_DATE, true
        ).apply();
    }

    private void CreatePushMessageHandle() {
        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                new Handler(getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        //显示收益的红点
                        if (msg.custom.equals("redpoint")) {
                            Intent intent = new Intent(BroadcastConfig.NEW_EARNING_MESSAGE);
                            sendBroadcast(intent);

                            PreferencesHelper.setIsHaveEarningMessage(MyApplication.this, true);
                        }
                    }
                });
            }
        };
        mPushAgent.setMessageHandler(messageHandler);
    }
}

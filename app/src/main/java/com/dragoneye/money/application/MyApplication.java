package com.dragoneye.money.application;

import android.app.Activity;
import android.app.Application;
import android.net.Uri;

import com.dragoneye.money.R;
import com.dragoneye.money.config.ProjectStatusConfig;
import com.dragoneye.money.dao.MyDaoMaster;
import com.dragoneye.money.dao.Project;
import com.dragoneye.money.dao.ProjectDao;
import com.dragoneye.money.dao.ProjectImage;
import com.dragoneye.money.dao.ProjectImageDao;
import com.dragoneye.money.http.HttpClient;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by happysky on 15-6-30.
 */
public class MyApplication extends Application {
    public ArrayList<Integer> images = new ArrayList<>();

    @Override
    public void onCreate(){
        super.onCreate();
        MyDaoMaster.init(this);
        HttpClient.initHttpClient(this);
        createTestData();
        initImageLoader();

//        UserEntrepreneur entrepreneur = new UserEntrepreneur();
//        entrepreneur.setUserId("test");
//        CurrentUser.setCurrentUser(entrepreneur);

        // 阿里云存储初始化
        initOssService();

        images.add(R.mipmap.projects_display001_0);
        images.add(R.mipmap.projects_display002_0);
        images.add(R.mipmap.projects_display003_0);
        images.add(R.mipmap.projects_display004_0);
        images.add(R.mipmap.projects_display005_0);
        images.add(R.mipmap.projects_display006_0);
        images.add(R.mipmap.projects_display007_0);
    }

    private void initOssService(){

    }

    public static void createTestData(){
        ProjectDao dao = MyDaoMaster.getDaoSession().getProjectDao();
        ProjectImageDao projectImageDao = MyDaoMaster.getDaoSession().getProjectImageDao();

        long id = dao.insert(new Project(null, ProjectStatusConfig.IN_PROGRESS, 0, "1", "test"));
        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display001_1).toString()));
        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display001_2).toString()));
        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display001_3).toString()));

        id = dao.insert(new Project(null, ProjectStatusConfig.IN_PROGRESS, 0, "2", "test"));
        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display002_1).toString()));

        id = dao.insert(new Project(null, ProjectStatusConfig.IN_PROGRESS, 0, "3", "test"));
        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display003_1).toString()));
        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display003_2).toString()));
        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display003_3).toString()));
        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display003_4).toString()));
        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display003_5).toString()));
        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display003_6).toString()));
        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display003_7).toString()));

        id = dao.insert(new Project(null, ProjectStatusConfig.IN_PROGRESS, 0, "4", "test"));
        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display004_1).toString()));
        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display004_2).toString()));
        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display004_3).toString()));
        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display004_4).toString()));

        id = dao.insert(new Project(null, ProjectStatusConfig.IN_PROGRESS, 0, "5", "test"));
        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display005_1).toString()));
        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display005_2).toString()));
        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display005_3).toString()));
        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display005_4).toString()));

        id = dao.insert(new Project(null, ProjectStatusConfig.IN_PROGRESS, 0, "6", "test"));
        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display006_1).toString()));
        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display006_2).toString()));

        id = dao.insert(new Project(null, ProjectStatusConfig.IN_PROGRESS, 0, "7", "test"));
        projectImageDao.insert(new ProjectImage(null, id, Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display007_1).toString()));

    }

    private void initImageLoader(){
        File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "imageLoader/Cache");
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
                .build();
        ImageLoader.getInstance().init(configuration);
    }


    //运用list来保存们每一个activity是关键
    private static List<Activity> mList = new LinkedList<Activity>();

    // add Activity
    public static void addActivity(Activity activity) {
        mList.add(activity);
    }

    public static void removeActivity(Activity a){
        mList.remove(a);
    }

    //关闭每一个list内的activity
    public static void exit() {
        try {
            for (Activity activity:mList) {
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
}

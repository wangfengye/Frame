package com.example.skin;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.lang.reflect.Method;

/**
 * Created by maple on 2019/7/26 14:53
 */
public class SkinManager {
    public static final SkinManager INSTANCE = new SkinManager();
    Resources mResources;
    Context context;
    private String mSkinPackageName;

    public static SkinManager getInstance() {
        return INSTANCE;
    }

    /**
     * 实际项目需要将切换的皮肤包路径持久化,启动时获取并加载该皮肤包
     * 建议在application onCreate中初始化
     */
    public static void init(){
        // todo
    }
    public boolean loadSkin(Context context, String path) {
        boolean isSuccess= false;
        this.context = context;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
            mSkinPackageName = packageInfo.packageName;
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPathMethod = AssetManager.class.getMethod("addAssetPath", String.class);
            addAssetPathMethod.invoke(assetManager, path);
            mResources = new Resources(assetManager, context.getResources().getDisplayMetrics(),
                    context.getResources().getConfiguration());
            isSuccess=true;
        } catch (Exception e) {

        }
        return isSuccess;
    }

    /**
     *  参照该方法可以扩展Drawable,String 等资源的获取方法.
     * @param id 主包中的资源id,
     * @return 从皮肤包中获取到的资源
     */
    public int getColor(int id) {
        if (mResources == null) return id;
        String typeName = context.getResources().getResourceTypeName(id);
        // 值字符串(R.id.sa)
        String valueName = context.getResources().getResourceEntryName(id);
        int skinId = mResources.getIdentifier(typeName, valueName, mSkinPackageName);
        if (skinId == 0) skinId = id;
        return mResources.getColor(skinId);
    }
}

package com.example.skin;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by maple on 2019/7/26 13:10
 */
public class BaseActivity extends Activity {
    public static final String TAG = "BaseActivity";
    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.webkit.",
            "android.app."
    };
    static final Class<?>[] mConstructorSignature = new Class[]{Context.class, AttributeSet.class};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 参考文档是使用该方法替换控件的构造过程
        // LayoutInflaterCompat.setFactory2(getLayoutInflater(), null);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        Log.i(TAG, "onCreateView: " + name);
        View view = null;
        if (name.contains(".")) {//自定义View会传入全类名
            view = onCreateView(name, context, attrs);

        } else {
            for (String prefix : sClassPrefixList) {
                view = onCreateView(prefix + name, context, attrs);
                if (view != null) {
                    break;
                }
            }
        }
        cacheNeedUpdateView(view, attrs);

        return view;
    }

    /**
     * 缓存需要换肤的控件及属性
     */
    private List<SkinView> mSkinViews = new ArrayList<>();

    private void cacheNeedUpdateView(View view, AttributeSet attrs) {
        List<SkinAttr> attrList = new ArrayList<>();
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            //属性名
            String attributeName = attrs.getAttributeName(i);
            // 属性id; @2130968614
            String attrValue = attrs.getAttributeValue(i);
            Log.i(TAG, "cacheNeedUpdateView: " + attributeName + "  " + attrValue);
            if (attributeName.contains("background")) {//可以增加需要换肤的属性
                //属性id
                int resId = Integer.parseInt(attrValue.substring(1));
                // 值类型(@color/ @drawable)
                String typeName = view.getResources().getResourceTypeName(resId);
                // 值字符串(R.id.sa)
                String valueName = view.getResources().getResourceEntryName(resId);
                attrList.add(new SkinAttr(attributeName, resId, typeName, valueName));
            }
        }
        mSkinViews.add(new SkinView(view, attrList));
    }

    public void updateSkin() {
        for (SkinView skinView : mSkinViews) {
            for (SkinAttr attr : skinView.getAttrs()) {// 根据Name判断设置函数,资源类型,分别设置资源(繁琐的地方:有方法数* 资料类型数个分支)
                if (attr.getAttrName().equals("background")) {
                    if (attr.getTypeName().equals("color")) {
                        skinView.getView().setBackgroundColor(SkinManager.getInstance().getColor(attr.getResId()));
                    }
                }
            }
        }
    }

    /**
     * 参考LayoutInflater.createView()源码,
     * 简化代码:去除了构造方法的缓存 filter过滤
     *
     * @param name
     * @param context
     * @param attrs
     * @return
     */
    private static final HashMap<String, Constructor<? extends View>> sConstructorMap =
            new HashMap<>();

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = null;
        try {
            Class<? extends View> claxx = context.getClassLoader().loadClass(name).asSubclass(View.class);
            Constructor<? extends View> constructor = claxx.getConstructor(mConstructorSignature);
            constructor.setAccessible(true);
            view = constructor.newInstance(context, attrs);

        } catch (Exception e) {
            // e.printStackTrace();
        }
        return view;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //加载颜色.
        updateSkin();
    }

}

package com.example.gifload;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import java.util.HashMap;


public class GifLoader {
    static {
        System.loadLibrary("gif-load");
    }

    public static HashMap<Integer, GifLoader> mMap = new HashMap<>();
    private volatile long gifInfo;//对应C中gif结构的地址.
    private int maxIndex;
    private Bitmap mBitmap;
    private ImageView mView;
    private boolean canLoop;
    private int speed = 100;//默认100为正常速度.;

    public static GifLoader with(String path, ImageView view) {
        if (mMap.containsKey(view.hashCode())) {
            GifLoader tmp = mMap.get(view.hashCode());
            if (tmp == null) {
                throw new RuntimeException("GifLoader  NULL Object");
            }
            tmp.canLoop = false;
            tmp.speed = 100;
            //todo: 判断并更新资源文件
            return tmp;
        } else {
            mMap.put(view.hashCode(), new GifLoader(path, view));
            return mMap.get(view.hashCode());
        }
    }

    private GifLoader(String path, ImageView view) {
        gifInfo = openFile(path);
        int width = getWidth(gifInfo);
        int height = getHeight(gifInfo);
        maxIndex = getLength(gifInfo);
        mView = view;
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        view.setImageBitmap(mBitmap);
    }

    private Handler handler = new Handler(Looper.getMainLooper());

    public GifLoader canLoop() {
        this.canLoop = true;
        return this;
    }

    public GifLoader speed(int speed) {
        this.speed = speed;
        return this;
    }

    public void start() {
        threadId++;
        final int currentId = threadId;
        new Thread(new Runnable() {
            @Override
            public void run() {
                render(currentId);
            }
        }).start();
    }

    public void descStart() {//由于后帧是基于前一帧的修改,倒序导致部分画面缺失,不能实现
        threadId++;
        final int currentId = threadId;
        new Thread(new Runnable() {
            @Override
            public void run() {
                unRender(currentId);
            }
        }).start();
    }

    private void unRender(int currentId) {
        for (int i = maxIndex - 1; i >= 0; i--) {
            if (threadId != currentId) return;
            long time = renderFrameN(gifInfo, mBitmap, i);
            // mainThread;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mView.setImageBitmap(mBitmap);
                }
            });
            try {
                Thread.sleep(time * 100 / speed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (canLoop && i == 0) {
                i = maxIndex;
            }
        }
    }

    private volatile int threadId;

    private void render(int currentId) {

        for (int i = 0; i < maxIndex; i++) {
            if (threadId != currentId) return;
            long time = renderFrameN(gifInfo, mBitmap, i);
            // mainThread;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mView.setImageBitmap(mBitmap);
                }
            });
            try {
                Thread.sleep(time * 100 / speed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (canLoop && i == maxIndex - 1) {
                i = -1;
            }
        }
    }

    public void destroy() {
        threadId = -1;
        mMap.remove(mView.hashCode());
    }

    public native long renderFrameN(long gifInfo, Bitmap bitmap, int index);

    // 获取C中gif对象地址
    private native long openFile(String path);

    private native int getWidth(long gifInfo);

    private native int getHeight(long gifInfo);

    //图片帧数
    private native int getLength(long gifInfo);
}

package com.maple.livedatabus.livedata;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;

;


/**
 * @author maple on 2019/7/8 17:07.
 * @version v1.0
 * @see 1040441325@qq.com
 */
@SuppressLint("ValidFragment")
public class HolderFragment extends Fragment {
    private int mCode;
    private LifecycleListener mListener;

    public HolderFragment() {
    }

    public void setLifecycleListener(LifecycleListener listener) {
        this.mListener = listener;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCode = activity.hashCode();
        if (mListener!=null)mListener.onCreate(mCode);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mListener!=null)mListener.onStart(mCode);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mListener!=null)mListener.onPause(mCode);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mListener!=null)mListener.onDetach(mCode);

    }
}

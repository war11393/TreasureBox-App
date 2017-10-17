package com.treasurebox.titwdj.treasurebox.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by 11393 on 2017/8/14.
 */

public class AppManager {
    private static final String TAG = "AppManager";

    private static AppManager mInstance;
    private static Stack<Activity> mActivityStack;
    public static AppManager getInstance() {
        if (null == mInstance) {
            mInstance = new AppManager();
        }
        return mInstance;
    }

    // 入栈
    public void addActivity(Activity activity) {
        if (activity.equals("class com.treasurebox.titwdj.treasurebox.Activity.FragmentActivitys")) {
            mActivityStack.push(activity);
            LogUtil.d(TAG, "添加活动：" + activity.getClass());
        } else {
            if (!checkActivity(activity.getClass())) {
                mActivityStack.push(activity);
                LogUtil.d(TAG, "添加活动：" + activity.getClass());
            }
        }
    }
    // 出栈
    public void removeActivity(Activity activity) {
        mActivityStack.remove(activity);
        LogUtil.d(TAG, "Destroy，移除：" + activity.getClass());
    }
    //  彻底退出
    public void finishAllActivity() {
        Activity activity;
        while (!mActivityStack.empty()) {
            activity = mActivityStack.pop();
            if (activity != null) {
                activity.finish();
            }
        }
        LogUtil.d(TAG, "结束所有活动");
    }
    // 结束指定类名的Activity
    public void finishActivity(Class<?> cls) {
        for (Activity activity : mActivityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }
    // 查找栈中是否存在指定的activity
    public boolean checkActivity(Class<?> cls) {
        for (Activity activity : mActivityStack) {
            if (activity.getClass().equals(cls)) {
                return true;
            }
        }
        return false;
    }
    // 结束指定的Activity
    public void finishActivity(Activity activity) {
        LogUtil.d(TAG, "结束活动：" + activity.getClass());
        if (activity != null) {
            mActivityStack.remove(activity);
            activity.finish();
        }
    }
    // finish指定的activity之上所有的activity
    public boolean finishToActivity(Class<? extends Activity> actCls, boolean isIncludeSelf) {
        List<Activity> buf = new ArrayList<Activity>();
        int size = mActivityStack.size();
        Activity activity = null;
        for (int i = size - 1; i >= 0; i--) {
            activity = mActivityStack.get(i);
            if (activity.getClass().isAssignableFrom(actCls)) {
                for (Activity a : buf) {
                    a.finish();
                }
                return true;
            } else if (i == size - 1 && isIncludeSelf) {
                buf.add(activity);
            } else if (i != size - 1) {
                buf.add(activity);
            }
        }
        return false;
    }
    //返回当前任务栈的大小
    public int getStackSize() {
        return mActivityStack.size();
    }
    //返回当前任务栈的大小
    public Activity getTopActivity() {
        return mActivityStack.get(mActivityStack.size() - 1);
    }

    private AppManager() {
        mActivityStack = new Stack<Activity>();
    }
}

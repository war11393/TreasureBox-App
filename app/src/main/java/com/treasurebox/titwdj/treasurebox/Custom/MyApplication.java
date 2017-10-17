package com.treasurebox.titwdj.treasurebox.Custom;

import android.app.Application;
import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.mob.MobSDK;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.treasurebox.titwdj.treasurebox.Model.nother.ComAbility;
import com.treasurebox.titwdj.treasurebox.Utils.AppManager;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Utils.HttpUtil;
import com.treasurebox.titwdj.treasurebox.Utils.Util;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.FriendList;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 11393 on 2017/8/2.
 * 为自己定制的Application类
 * 需在Manifest中为application标签添加name值：
 * android:name=".Utils.MyApplication"
 */

public class MyApplication extends Application {
    private static Context context;
    private static Application application;

    //全局变量---有丢失的可能
    public static Map<String, String> locate;//地理位置--键：经、纬、国、省、市、区、街、定位方式(GPS、网络)
    public static User user = null;//此次登陆的用户
    public static List<FriendList> userFriendLists = null;
    public static ComAbility userAbility = null;

    @Override
    public void onCreate() {
        AppManager.getInstance();
        super.onCreate();
        context = getApplicationContext();//用来全局获取context
        application = this;
        MobSDK.init(this, this.a(), this.b());//短信sdk得初始化
        //LitePal.initialize(context);//使用了LitePal，就在这里为LitePal获取上下文环境，或者直接继承LitePalApplication
        FlowManager.init(this);//DbFlow数据库框架初始化
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(this);
    }
    protected String a() {
        return null;
    }
    protected String b() {
        return null;
    }

    //提供全局获取context得方法
    public static Context getContext() {
        return context;
    }

    //提供获取应用程序的方法
    public static Application getApplication() {
        return application;
    }

    //加载好友列表
    private static List<FriendList> loadFriendList() {
        userFriendLists = new ArrayList<>();
        RequestBody body = new FormBody.Builder()
                .add("uid", MyApplication.user.getUid() + "").build();
        HttpUtil.sendPostOkHttpRequest(HttpPathUtil.selectAllFriends(), body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {e.printStackTrace();}
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                if (resp.equals("您还没有好友哦，快去添加几个好友吧！")) {
                    userFriendLists.clear();
                } else if (Util.JsonUtils.isGoodJson(resp)) {
                    userFriendLists = JSON.parseArray(resp, FriendList.class);
                    for(FriendList friend:userFriendLists) {
                        friend.save();
                    }
                }
            }
        });
        return userFriendLists;
    }
    //检查好友数据是否存在
    public static List<FriendList> checkFriendList() {
        if (userFriendLists == null) {
            return loadFriendList();
        } else {
            return userFriendLists;
        }
    }
}

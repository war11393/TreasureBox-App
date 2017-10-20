package com.treasurebox.titwdj.treasurebox.Activity.PremainActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.treasurebox.titwdj.treasurebox.Activity.BaseActivity;
import com.treasurebox.titwdj.treasurebox.Activity.MainActivity;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.FriendList;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.User;
import com.treasurebox.titwdj.treasurebox.Model.nother.ComAbility;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.AppManager;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Utils.HttpUtil;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Utils.Util;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.client;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.maxLoadTimes;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.serversLoadTimes;

public class SplashActivity extends BaseActivity {
    private static final String TAG = "SplashActivity";

    private boolean isRemember;
    private String acc, pwd;

    private TextView textView;

    private static int LOGIN_OK = 0;
    private static int LOGIN_False = 1;
    private static int GET_FRIEND_OK = 2;
    private static int GET_FRIEND_ERROR = 3;
    private static int GET_ABILITY_OK = 4;
    private static int GET_ABILITY_ERROR = 5;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://登陆成功，获取好友列表
                    textView.setText("正在获取好友信息...");
                    loadFriendList();
                    break;
                case 1://登陆失败，去登陆
                    LogUtil.d(TAG, "登陆失败");
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    AppManager.getInstance().finishActivity(SplashActivity.this);
                    break;
                case 2://好友获取完成，获取用户能力值
                    textView.setText("初始化用户信息...");
                    getValue();
                    break;
                case 3://好友获取失败，提示用户相关信息，0.5秒后进入弹出选择
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            final SweetAlertDialog dialog = new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("WARN").setContentText("没有获取到好友信息，是否忽略？")
                                    .setConfirmText("忽略").setCancelText("退出应用");
                            dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    dialog.dismiss();
                                    getValue();
                                }
                            });
                            dialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    dialog.dismiss();
                                    AppManager.getInstance().finishAllActivity();
                                }
                            });
                            dialog.show();
                        }
                    }, 500);
                    break;
                case 4://获取能力值成功，0.1秒后进入主界面
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            AppManager.getInstance().finishActivity(SplashActivity.this);
                        }
                    }, 100);
                    break;
                case 5://获取能力值失败，手动添加一个不报错的数据，0.1秒后进入主界面
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyApplication.userAbility = new ComAbility(0,0,0,0,0,0,0,0,0,0);
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            AppManager.getInstance().finishActivity(SplashActivity.this);
                        }
                    }, 100);
                    break;
                default:break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        textView = (TextView) findViewById(R.id.textView2);

        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo", MODE_PRIVATE);
        isRemember = sharedPreferences.getBoolean("remember_password", false);
        acc = sharedPreferences.getString("account", "");
        pwd = sharedPreferences.getString("password", "");

        textView.setText("正在监测登陆...");
        login();
    }

    //登陆
    private void login() {
        final Message msg = new Message();
        if (isRemember) {//执行登陆
            RequestBody body = new FormBody.Builder()
                    .add("number", acc)
                    .add("password", pwd)
                    .build();
            HttpUtil.sendPostOkHttpRequest(HttpPathUtil.login(), body, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtil.d(TAG, e.toString() + "   正重新尝试链接...");
                    if(e.getClass().equals(SocketTimeoutException.class) && serversLoadTimes < maxLoadTimes)//如果超时并未超过指定次数，则重新连接
                    {
                        serversLoadTimes++;
                        client.newCall(call.request()).enqueue(this);
                    } else {
                        serversLoadTimes = 0;
                        e.printStackTrace();
                        HttpUtil.showError();
                    }
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    serversLoadTimes = 0;
                    final String resp = response.body().string();
                    LogUtil.d(TAG, resp);
                    if ("您输入的账号不存在！".equals(resp)||"您输入的密码不正确！".equals(resp)||"您输入的手机号不存在！".equals(resp)) {//登陆成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final SweetAlertDialog dialog = new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("小贴士")
                                        .setContentText("小盒监测到主人的登陆信息有变，要重新登陆咯～").setConfirmText("确认");
                                dialog.setCancelable(false);
                                dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        dialog.dismiss();
                                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        AppManager.getInstance().finishActivity(SplashActivity.this);
                                    }
                                });
                                dialog.show();
                            }
                        });
                        msg.what = LOGIN_False;
                        handler.sendMessage(msg);
                    } else {
                        if (Util.JsonUtils.isGoodJson(resp)) {
                            MyApplication.user = new Gson().fromJson(resp, User.class);
                            MyApplication.user.save();
                            msg.what = LOGIN_OK;
                            handler.sendMessage(msg);
                        } else {
                            msg.what = LOGIN_False;
                            handler.sendMessage(msg);
                        }
                    }
                }
            });
        } else {
            msg.what = LOGIN_False;
            handler.sendMessage(msg);
        }
    }

    //加载好友列表
    private void loadFriendList() {
        final Message msg = new Message();
        MyApplication.userFriendLists = new ArrayList<>();
        RequestBody body = new FormBody.Builder()
                .add("uid", MyApplication.user.getUid() + "").build();
        HttpUtil.sendPostOkHttpRequest(HttpPathUtil.selectAllFriends(), body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.d(TAG, e.toString() + "   正重新尝试链接...");
                if(e.getClass().equals(SocketTimeoutException.class) && serversLoadTimes < maxLoadTimes)//如果超时并未超过指定次数，则重新连接
                {
                    serversLoadTimes++;
                    client.newCall(call.request()).enqueue(this);
                } else {
                    serversLoadTimes = 0;
                    e.printStackTrace();
                    HttpUtil.showError();
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                serversLoadTimes = 0;
                String resp = response.body().string();
                if (resp.equals("您还没有好友哦，快去添加几个好友吧！")) {
                    MyApplication.userFriendLists = new ArrayList<FriendList>();
                    MyApplication.userFriendLists.clear();
                    msg.what = GET_FRIEND_OK;
                    handler.sendMessage(msg);
                } else if (Util.JsonUtils.isGoodJson(resp)) {
                    MyApplication.userFriendLists = JSON.parseArray(resp, FriendList.class);
                    for(FriendList friend:MyApplication.userFriendLists) {
                        friend.save();
                    }
                    msg.what = GET_FRIEND_OK;
                    handler.sendMessage(msg);
                } else {
                    msg.what = GET_FRIEND_ERROR;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    //获取用户能力值
    private void getValue() {
        final Message msg = new Message();
        HttpUtil.sendOkHttpRequest(HttpPathUtil.selectValue() + "?number=" + MyApplication.user.getNumber(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.d(TAG, e.toString() + "   正重新尝试链接...");
                if(e.getClass().equals(SocketTimeoutException.class) && serversLoadTimes < maxLoadTimes)//如果超时并未超过指定次数，则重新连接
                {
                    serversLoadTimes++;
                    client.newCall(call.request()).enqueue(this);
                } else {
                    serversLoadTimes = 0;
                    e.printStackTrace();
                    HttpUtil.showError();
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                serversLoadTimes = 0;
                String resp = response.body().string();
                LogUtil.d(TAG, resp);
                if (Util.JsonUtils.isGoodJson(resp)) {
                    MyApplication.userAbility = new Gson().fromJson(resp, ComAbility.class);
                    msg.what = GET_ABILITY_OK;
                    handler.sendMessage(msg);
                } else {
                    msg.what = GET_ABILITY_ERROR;
                    handler.sendMessage(msg);
                }
            }
        });
    }
}

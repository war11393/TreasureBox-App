package com.treasurebox.titwdj.treasurebox.Activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.treasurebox.titwdj.treasurebox.Activity.PremainActivity.LoginActivity;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.FriendList;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.User_Table;
import com.treasurebox.titwdj.treasurebox.Utils.AppManager;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Utils.HttpUtil;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Utils.Util;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.User;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.client;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.maxLoadTimes;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.serversLoadTimes;

/**
 * Created by 11393 on 2017/8/14.
 * 自定义Activity父类并实现Activity统一堆栈管理
 * 日志还有问题。。。
 */

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    //读取登录信息
    private static SharedPreferences sharedPreferences;
    private static boolean isRemember;
    private static String acc, pwd;

    @Override//完整生存期开始
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//设置透明状态栏
        super.onCreate(savedInstanceState, persistentState);
        sharedPreferences = getSharedPreferences("loginInfo", MODE_PRIVATE);
        isRemember = sharedPreferences.getBoolean("remember_password", false);
        acc = sharedPreferences.getString("account", "");
        pwd = sharedPreferences.getString("password", "");

        MyApplication.user = SQLite.select().from(User.class)
                .where(User_Table.number.eq(acc)).or(User_Table.phone.eq(acc))
                .and(User_Table.password.eq(pwd)).querySingle();
    }
    @Override//可见生存期开始
    protected void onStart() {
        super.onStart();
        LogUtil.d(TAG, getRunningActivityName());
        if (getRunningActivityName().equals("com.treasurebox.titwdj.treasurebox.Activity.PremainActivity.LoginActivity")){
            LogUtil.d(TAG, "登陆界面");
            return;
        } else if (getRunningActivityName().equals("com.treasurebox.titwdj.treasurebox.Activity.PremainActivity.RegistActivity")) {
            return;
        } else if (getRunningActivityName().equals("com.treasurebox.titwdj.treasurebox.Activity.PremainActivity.CompInfoActivity")) {
            return;
        } else if (getRunningActivityName().equals("com.treasurebox.titwdj.treasurebox.Activity.PremainActivity.SplashActivity")) {
            return;
        } else {
            checkUserInfo();
            if (MyApplication.user != null) {
                MyApplication.checkFriendList();
            }
        }
        AppManager.getInstance().addActivity(this);
    }
    @Override//前台生存期开始
    protected void onResume() {
        super.onResume();
    }
    @Override//前台生存期结束
    protected void onPause() {
        super.onPause();
    }
    @Override//可见生存期结束------有三个结果，要么等待重启，要么执行销毁，要么被优先级更高的程序需要内存时杀掉（重启时将从onCreate开始）
    protected void onStop() {
        super.onStop();
    }
    @Override//完整生存期结束
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override//在onStop和onStart之间，活动重启时调用
    protected void onRestart() {
        super.onRestart();
    }

    /**
     * 自写的方法
     * @return
     */
    //获取栈顶activity名字
    private String getRunningActivityName(){
        ActivityManager activityManager=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivity=activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        return runningActivity;
    }

    //检查登陆信息
    public void checkUserInfo() {
        if (isRemember && MyApplication.user == null) {
            //此处执行登陆命令
            RequestBody body = new FormBody.Builder()
                    .add("number", acc)
                    .add("password", pwd)
                    .build();
            HttpUtil.sendPostOkHttpRequest(HttpPathUtil.login(), body, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    if(e.getClass().equals(ConnectException.class)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final SweetAlertDialog dialog = new SweetAlertDialog(BaseActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("小贴士")
                                        .setContentText("系统正在维护更新，请耐心等待我们完成后在访问，谢谢合作！").setConfirmText("确认");
                                dialog.setCancelable(false);
                                dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        dialog.dismiss();
                                        AppManager.getInstance().finishAllActivity();
                                    }
                                });
                                dialog.show();
                            }
                        });
                    } else if(e.getClass().equals(SocketTimeoutException.class) && serversLoadTimes < maxLoadTimes)//如果超时并未超过指定次数，则重新连接
                    {
                        serversLoadTimes++;
                        client.newCall(call.request()).enqueue(this);
                    } else {
                        serversLoadTimes = 0;
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
                                final SweetAlertDialog dialog = new SweetAlertDialog(BaseActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("小贴士")
                                        .setContentText("小盒监测到主人的登陆信息有变，要重新登陆咯～").setConfirmText("确认");
                                dialog.setCancelable(false);
                                dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        dialog.dismiss();
                                        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        AppManager.getInstance().finishActivity(BaseActivity.this);
                                    }
                                });
                                dialog.show();
                            }
                        });
                    } else {
                        if (Util.JsonUtils.isGoodJson(resp)) {
                            MyApplication.user = new Gson().fromJson(resp, User.class);
                            MyApplication.user.save();
                        }
                    }
                }
            });
        } else if (!isRemember && MyApplication.user == null) {
            LogUtil.d(TAG, "去登陆");
            Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
            this.startActivity(intent);
            AppManager.getInstance().finishActivity(BaseActivity.this);
        }
    }
}

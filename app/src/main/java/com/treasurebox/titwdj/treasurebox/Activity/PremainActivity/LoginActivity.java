package com.treasurebox.titwdj.treasurebox.Activity.PremainActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.treasurebox.titwdj.treasurebox.Activity.MainActivity;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.AppManager;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Utils.HttpUtil;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Utils.ProjectUtil;
import com.treasurebox.titwdj.treasurebox.Activity.BaseActivity;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.User;
import com.treasurebox.titwdj.treasurebox.Utils.Util;

import java.io.IOException;
import java.net.SocketTimeoutException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.client;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.dialog;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.maxLoadTimes;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.serversLoadTimes;

public class LoginActivity extends BaseActivity {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private EditText accountEdit;
    private EditText passwordEdit;
    private Button login, regist;
    private CheckBox rememberPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ProjectUtil.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = getSharedPreferences("loginInfo", MODE_PRIVATE);
        accountEdit = (EditText) findViewById(R.id.login_account);
        passwordEdit = (EditText) findViewById(R.id.login_password);
        login = (Button) findViewById(R.id.login_button);
        regist = (Button) findViewById(R.id.login_regist_button);
        rememberPass = (CheckBox) findViewById(R.id.login_remember_pass);

        ProjectUtil.setToolBar(this, "用户登录", R.drawable.part_trans, null);

        //初始化这些内容
        boolean isRemember = pref.getBoolean("remember_password", false);
        String acc = pref.getString("account", "");
        String pwd = pref.getString("password", "");
        accountEdit.setText(acc);
        passwordEdit.setText(pwd);
        rememberPass.setChecked(isRemember);

        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转至注册页
                Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String acc = accountEdit.getText().toString();
                final String pwd = passwordEdit.getText().toString();

                //执行登陆
                RequestBody body = new FormBody.Builder()
                        .add("number", acc)
                        .add("password", pwd)
                        .build();
                if ("".equals(acc) || "".equals(pwd)) {
                    Toast.makeText(LoginActivity.this, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    HttpUtil.sendPostOkHttpRequest(HttpPathUtil.login(), body, true, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            LogUtil.d("LoginActivity", e.toString() + "   正重新尝试链接...");
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
                            serversLoadTimes = 0;dialog.dismiss();
                            String resp = response.body().string();
                            if ("您输入的账号不存在！".equals(resp)) {//登陆成功
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                .setContentText("您输入的账号不存在哦，请检查一下下")
                                                .setTitleText("发生错误")
                                                .show();
                                    }
                                });
                            } else if ("您输入的密码不正确！".equals(resp)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                .setContentText("密码不正确>_<")
                                                .setTitleText("发生错误")
                                                .show();
                                    }
                                });
                            } else if ("您输入的手机号不存在！".equals(resp)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final SweetAlertDialog dialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                .setContentText("您输入的号码不存在,请先去注册哦")
                                                .setTitleText("发生错误").setConfirmText("去注册").setCancelText("取消");
                                        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
                                                startActivity(intent);
                                                dialog.dismiss();
                                            }
                                        });
                                        dialog.show();
                                    }
                                });
                            } else {//登陆成功
                                if (Util.JsonUtils.isGoodJson(resp)) {
                                    MyApplication.user = new Gson().fromJson(resp, User.class);
                                    MyApplication.user.save();
                                    LogUtil.d("LoginActivity", MyApplication.user.getNumber());
                                    editor = pref.edit();
                                    //检查复选框勾选情况并做出相应操作
                                    if (rememberPass.isChecked()) {
                                        editor.putBoolean("remember_password", true);
                                        editor.putString("account", acc);
                                        editor.putString("password", pwd);
                                    } else {
                                        editor.clear();
                                    }
                                    editor.apply();

                                    Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                                    startActivity(intent);
                                    AppManager.getInstance().finishActivity(LoginActivity.this);
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            final SweetAlertDialog dialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                    .setTitleText("WARN").setContentText("系统正忙，请稍后再试").setConfirmText("知道了");
                                            dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            dialog.show();
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        });
    }
}

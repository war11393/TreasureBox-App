package com.treasurebox.titwdj.treasurebox.Activity.PremainActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.google.gson.Gson;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Service.MyReceiver;
import com.treasurebox.titwdj.treasurebox.Utils.AppManager;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Utils.HttpUtil;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Utils.ProjectUtil;
import com.treasurebox.titwdj.treasurebox.Activity.BaseActivity;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.client;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.dialog;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.maxLoadTimes;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.serversLoadTimes;
import static java.lang.Thread.sleep;

public class RegistActivity extends BaseActivity {
    private static final String TAG = "RegistActivity";

    private EditText phone, checknum, pass, repass;
    private Button send, regist;
    private EventHandler eventHandler;

    private int time = -1;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0://更新按钮时间
                    if (time >= 0) {
                        send.setText(time + "s");
                    } else {
                        send.setText("重新发送");
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ProjectUtil.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        initView();//初始化视图

        //开启推送业务，为设备获取通信id
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, "luCn0f4d0zrGRxoCtX9fD6qRE3s4rl7u");

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(RegistActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RegistActivity.this, new String[]{Manifest.permission.READ_SMS}, 1);
                } else {
                    sendSMS();
                }
            }
        });
        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regist.setEnabled(false);
                if (TextUtils.isEmpty(pass.getText().toString()) || TextUtils.isEmpty(repass.getText().toString())
                        || TextUtils.isEmpty(phone.getText().toString()) || TextUtils.isEmpty(checknum.getText().toString())){
                    new SweetAlertDialog(RegistActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("ERROR")
                            .setContentText("主人要把信息填完～")
                            .show();
                    regist.setEnabled(true);
                } else if (pass.getText().toString().equals(repass.getText().toString())){
                    if (pass.getText().toString().length() < 8 && pass.getText().toString().length() > 12){
                        new SweetAlertDialog(RegistActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("ERROR")
                                .setContentText("主人的密码不宜过长或过短哦～")
                                .show();
                        regist.setEnabled(true);
                    } else {
                        //提交验证信息
                        SMSSDK.submitVerificationCode("86", phone.getText().toString().trim(), checknum.getText().toString().trim());
                    }
                } else if (!pass.getText().toString().equals(repass.getText().toString())) {
                    new SweetAlertDialog(RegistActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("ERROR")
                            .setContentText("主人的两次密码不一致哦！")
                            .show();
                    regist.setEnabled(true);
                }
            }
        });
    }

    //发短信
    private void sendSMS(){
        String num = phone.getText().toString();
        if (!judgephone(num)){
            SweetAlertDialog dialog = new SweetAlertDialog(RegistActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("ERROR")
                    .setContentText("主人的手机号好像输错了0.0");
            dialog.show();
        } else if (time < 0){
            //给这个手机号发短信
            SMSSDK.getVerificationCode("86", phone.getText().toString().trim());
            initSMS();//开启短信服务
            timeOve();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    sendSMS();
                else
                    Toast.makeText(this, "权限禁止>_<", Toast.LENGTH_SHORT).show();
                break;
            default:break;
        }
    }

    //定时器，每次都减一秒
    private void timeOve() {
         new Thread(new Runnable() {
            @Override
            public void run() {
                for (time = 30; time>=0; time--){
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(0);
                }
            }
        }).start();
    }

    //初始化短信服务并接收回调
    private void initSMS() {
        eventHandler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        LogUtil.d(TAG, "提交注册");
                        //提交验证码成功,在这里执行注册
                        String username = phone.getText().toString().trim();
                        String password = pass.getText().toString().trim();
                        String repassword = repass.getText().toString().trim();
                        RequestBody body = new FormBody.Builder()
                                .add("phone", username)
                                .add("password", password)
                                .add("repassword", repassword)
                                .add("channelId", MyReceiver.getChannelId())
                                .build();
                        HttpUtil.sendPostOkHttpRequest(HttpPathUtil.addUser(), body, true, new Callback() {
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
                                serversLoadTimes = 0;dialog.dismiss();
                                final String resp = response.body().string();
                                LogUtil.d(TAG, resp);
                                if ("此手机号已经被注册！".equals(resp)){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new SweetAlertDialog(RegistActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                    .setTitleText("发生错误")
                                                    .setContentText("这个手机号已经被注册过啦")
                                                    .show();
                                        }
                                    });
                                } else {
                                    Gson gson = new Gson();
                                    final User userInfoSample = gson.fromJson(resp, User.class);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            final SweetAlertDialog dialog = new SweetAlertDialog(RegistActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                                    .setTitleText("注册完成,账号:" + userInfoSample.getNumber())
                                                    .setContentText("恭喜主人注册成功，是否选择完善信息呢？（有完整的信息才能让我更好地为主人服务哦～）")
                                                    .setConfirmText("去完善").setCancelText("去登陆");
                                            dialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    AppManager.getInstance().finishActivity(RegistActivity.this);
                                                    dialog.dismiss();
                                                }
                                            });
                                            dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    Intent intent = new Intent(RegistActivity.this, CompInfoActivity.class);
                                                    intent.putExtra("uid", userInfoSample.getUid());
                                                    startActivity(intent);
                                                    AppManager.getInstance().finishActivity(RegistActivity.this);
                                                    dialog.dismiss();
                                                }
                                            });
                                            userInfoSample.save();
                                            dialog.show();
                                            regist.setEnabled(true);
                                        }
                                    });
                                }
                            }
                        });
                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        LogUtil.d(TAG, "afterEvent: 获取验证码成功");
                    }
                }else{
                    ((Throwable) data).printStackTrace();
                    String err = ((Throwable) data).getMessage();
                    try {
                        JSONObject obj = new JSONObject(err);
                        if (((int)obj.get("status")) == 477){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new SweetAlertDialog(RegistActivity.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("ERROR")
                                            .setContentText("同一个号码一天只能接收5条信息哦，主人明天再试吧...")
                                            .show();
                                }
                            });
                            regist.setEnabled(true);
                        } else if (((int)obj.get("status")) == 468){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new SweetAlertDialog(RegistActivity.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("ERROR")
                                            .setContentText("主人的验证码可能输错了或者过期了<0_0>")
                                            .show();
                                }
                            });
                            regist.setEnabled(true);
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new SweetAlertDialog(RegistActivity.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("ERROR")
                                            .setContentText("发生了未知错误0x0")
                                            .show();
                                }
                            });
                            regist.setEnabled(true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        SMSSDK.registerEventHandler(eventHandler);
    }

    //监测手机号合法性
    public boolean judgephone(String phone) {
        String str = "";
        str = phone;
        String pattern = "(13\\d|14[57]|15[^4,\\D]|17[678]|18\\d)\\d{8}|170[059]\\d{7}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
        return m.matches();
    }

    //初始化视图
    private void initView() {
        phone = (EditText) findViewById(R.id.regist_number);
        checknum = (EditText) findViewById(R.id.regist_checknum);
        pass = (EditText) findViewById(R.id.regist_password);
        repass = (EditText) findViewById(R.id.regist_repassword);
        regist = (Button) findViewById(R.id.regist_regist_button);
        send = (Button) findViewById(R.id.regist_send_button);
        //设置标题栏
        ProjectUtil.setToolBar(this, "用户注册", R.drawable.part_back, backClick);
    }
    //处理点击
    View.OnClickListener backClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.toolbar_image) {
                AppManager.getInstance().finishActivity(RegistActivity.this);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);//注销SDK
    }
}

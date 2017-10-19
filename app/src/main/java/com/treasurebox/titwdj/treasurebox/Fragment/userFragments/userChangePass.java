package com.treasurebox.titwdj.treasurebox.Fragment.userFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.AppManager;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Utils.HttpUtil;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Utils.Util;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.User;

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


/**
 * Created by 11393 on 2017/8/14.
 */

public class userChangePass extends Fragment implements View.OnClickListener{
    private static final String TAG = "userChangePass";

    EditText oldpass, newpass, renewpass;
    Button commit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_changepass, container, false);

        initView(view);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_changepass_commit:
                LogUtil.d(TAG, oldpass.getText().toString());
                LogUtil.d(TAG, newpass.getText().toString());
                LogUtil.d(TAG, renewpass.getText().toString());
                if (!TextUtils.isEmpty(oldpass.getText())) {//旧密码不空
                    if ((!TextUtils.isEmpty(newpass.getText()))&&newpass.getText().toString().equals(renewpass.getText().toString())) {//新密码不空
                        String oldpad = oldpass.getText().toString();//旧密码
                        String newpad = newpass.getText().toString().trim();//新密码
                        if (oldpad.equals(MyApplication.user.getPassword())) {
                            RequestBody body = new FormBody.Builder()
                                    .add("uid", MyApplication.user.getUid() + "")
                                    .add("password", newpad)
                                    .build();
                            HttpUtil.sendPostOkHttpRequest(HttpPathUtil.updatePasswprd(), body, true, new Callback() {
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
                                    serversLoadTimes = 0;HttpUtil.closeDialog();
                                    String resp = response.body().string();
                                    LogUtil.d(TAG, resp);
                                    if (Util.JsonUtils.isGoodJson(resp)) {
                                        User userInfo = new Gson().fromJson(resp, User.class);
                                        userInfo.save();
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                                                        .setTitleText("修改成功")
                                                        .setConfirmText("关闭")
                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                            @Override
                                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                AppManager.getInstance().finishActivity((AppCompatActivity) getActivity());
                                                            }
                                                        }).show();
                                            }
                                        });
                                    } else {
                                        if (getActivity() != null) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    final SweetAlertDialog dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
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
                    } else {
                        //提醒用户检查新密码是否输入正确
                        new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("两次密码不一致")
                                .setContentText("主人请重新输入下下>_<")
                                .show();
                    }
                } else {
                    //弹出旧密码不能为空
                    new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("旧密码有毛病0.0")
                            .show();
                }
                break;
            default:break;
        }
    }

    //初始化视图
    private void initView(View view) {
        commit = (Button) view.findViewById(R.id.user_changepass_commit);
        renewpass = (EditText) view.findViewById(R.id.user_changepass_renewpass);
        newpass = (EditText) view.findViewById(R.id.user_changepass_newpass);
        oldpass = (EditText) view.findViewById(R.id.user_changepass_oldpass);

        commit.setOnClickListener(this);
    }
}

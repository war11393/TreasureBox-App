package com.treasurebox.titwdj.treasurebox.Fragment.userFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.AppManager;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Utils.HttpUtil;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;

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

public class userCallUs extends Fragment {
    private static final String TAG = "userCallUs";

    EditText userCallusText;
    Button userCallusConfirm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_callus, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        userCallusConfirm = (Button) view.findViewById(R.id.user_callus_confirm);
        userCallusText = (EditText) view.findViewById(R.id.user_callus_text);

        userCallusConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!userCallusText.getText().toString().trim().equals("")) {
                    RequestBody body = new FormBody.Builder()
                            .add("uid", MyApplication.user.getUid() + "")
                            .add("content", userCallusText.getText().toString().trim()).build();
                    HttpUtil.sendPostOkHttpRequest(HttpPathUtil.addFeedBack(), body, true, new Callback() {
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
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final SweetAlertDialog dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                                                .setTitleText("提交成功").setContentText("感谢您向我们提出建议，我们将努力改进^_^")
                                                .setConfirmText("返回");
                                        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                if (getActivity() != null) {
                                                    dialog.dismiss();
                                                    AppManager.getInstance().finishActivity(getActivity());
                                                }
                                            }
                                        });
                                        dialog.show();
                                    }
                                });
                            }
                        }
                    });
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final SweetAlertDialog dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("WARN").setContentText("请您输入有效信息后再提交！")
                                        .setConfirmText("知道了");
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

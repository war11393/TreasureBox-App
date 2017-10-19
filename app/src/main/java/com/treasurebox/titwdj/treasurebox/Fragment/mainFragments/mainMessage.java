package com.treasurebox.titwdj.treasurebox.Fragment.mainFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.Message_Table;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Utils.HttpUtil;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Utils.Util;
import com.treasurebox.titwdj.treasurebox.Adapter.main_message;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.Message;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

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
 * 主页-消息页
 */

public class mainMessage extends Fragment {
    private static final String TAG = "mainMessage";

    RecyclerView mainMessageRecycler;
    List<Message> messages, messages1;
    main_message adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_message, container, false);
        getData();
        initView(view);
        return view;
    }

    private void getData() {
        RequestBody body = new FormBody.Builder()
                .add("uid", MyApplication.user.getUid() + "").build();
        HttpUtil.sendPostOkHttpRequest(HttpPathUtil.selWarnByPre(), body, true, new Callback() {
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
                    messages1 = JSON.parseArray(resp, Message.class);
                    for (Message message:messages1) {
                        messages.add(message);
                        message.save();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new main_message(messages, getActivity());
                            mainMessageRecycler.setAdapter(adapter);
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

    private void initView(View view) {
        mainMessageRecycler = (RecyclerView) view.findViewById(R.id.main_message_recycler);

        messages = SQLite.select().from(Message.class).where(Message_Table.wfrom.eq(MyApplication.user.getUid())).queryList();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mainMessageRecycler.setLayoutManager(layoutManager);
        adapter = new main_message(messages, getActivity());
        mainMessageRecycler.setAdapter(adapter);
    }
}

package com.treasurebox.titwdj.treasurebox.Fragment.mainFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.treasurebox.titwdj.treasurebox.Adapter.HotList;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;
import com.treasurebox.titwdj.treasurebox.Model.nother.HotContent;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Utils.HttpUtil;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Utils.Util;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.client;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.dialog;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.maxLoadTimes;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.serversLoadTimes;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.showError;

/**
 * Created by 11393 on 2017/8/14.
 */
public class hotDiscuss extends Fragment {
    private static final String TAG = "hotDiscuss";
    private static final String title1 = "改变人生轨迹的一次经历";
    private static final String title2 = "增加生活幸福感的n种方式";

    RecyclerView hotRecycler;
    EditText hotText;
    Button hotCommit;

    private static int flag;
    private HotList adapter;

    List<HotContent> contentList = new ArrayList<HotContent>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hot, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            getTopic();
        }
    }

    private void getTopic() {
        RequestBody body = new FormBody.Builder()
                .add("title", flag==1?title1:title2).build();
        HttpUtil.sendPostOkHttpRequest(HttpPathUtil.showtopic(), body, true, new Callback() {
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
                    contentList = JSON.parseArray(resp, HotContent.class);
                    adapter = new HotList(getActivity(), contentList, flag);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hotRecycler.setAdapter(adapter);
                            }
                        });
                    }
                } else {
                    showError();
                }
            }
        });
    }

    private void commitContent() {
        RequestBody body = new FormBody.Builder()
                .add("uid", MyApplication.user.getUid() + "")
                .add("topicContent", hotText.getText().toString().trim())
                .add("title", (flag==1?title1:title2)).build();
        HttpUtil.sendPostOkHttpRequest(HttpPathUtil.addTopic(), body, true, new Callback() {
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
                    HotContent hot = new Gson().fromJson(resp, HotContent.class);
                    Collections.reverse(contentList);
                    contentList.add(hot);
                    Collections.reverse(contentList);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hotText.setText("");
                                hotCommit.setEnabled(false);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                } else {
                    HttpUtil.showError();
                }
            }
        });
    }

    private void initView(View view) {
        flag = getArguments().getInt("flag", 1);
        hotCommit = (Button) view.findViewById(R.id.hot_commit);
        hotText = (EditText) view.findViewById(R.id.hot_text);
        hotRecycler = (RecyclerView) view.findViewById(R.id.hot_recycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        hotRecycler.setLayoutManager(layoutManager);
        if (getActivity() != null) {
            adapter = new HotList(getActivity(), new ArrayList<HotContent>(), flag);
            hotRecycler.setAdapter(adapter);
        }

        hotText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().length() == 0) {
                    hotCommit.setEnabled(false);
                } else {
                    hotCommit.setEnabled(true);
                }
            }
        });

        hotCommit.setEnabled(false);
        hotCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commitContent();
            }
        });
    }

    //构造函数
    public static hotDiscuss newInstance(int i) {

        Bundle args = new Bundle();

        args.putInt("flag", i);

        hotDiscuss fragment = new hotDiscuss();
        fragment.setArguments(args);
        return fragment;
    }
}

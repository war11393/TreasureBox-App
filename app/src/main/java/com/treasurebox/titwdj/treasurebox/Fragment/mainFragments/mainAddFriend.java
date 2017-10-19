package com.treasurebox.titwdj.treasurebox.Fragment.mainFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.treasurebox.titwdj.treasurebox.Activity.FragmentActivitys;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Utils.HttpUtil;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Utils.Util;
import com.treasurebox.titwdj.treasurebox.Adapter.friend_add_search;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.FriendList;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.FriendList_Table;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.User;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
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
 * Created by 11393 on 2017/8/20.
 * 添加好友
 */
public class mainAddFriend extends Fragment implements View.OnClickListener {
    private static final String TAG = "mainAddFriend";

    EditText mainAddfriendPhone;
    Button mainAddfriendSearch;
    RecyclerView mainAddfriendRecycler;

    private int status;
    List<User> userInfoList = new ArrayList<>(), userInfoList2 = new ArrayList<>();
    friend_add_search addSearchAdapter = null;

    //构造
    public static mainAddFriend newInstance(String string) {
        Bundle args = new Bundle();
        args.putString("type", string);
        mainAddFriend fragment = new mainAddFriend();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_addfriend, container, false);
        matchStatus(getArguments().getString("type"));//匹配关系代码
        initView(view);//初始化视图,设置点击项,配置适配器
        return view;
    }

    //处理点击
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_addfriend_search:
                //搜索符合条件得用户,后期应排除已添加的好友
                RequestBody body = new FormBody.Builder()
                        .add("uid", MyApplication.user.getUid() + "")
                        .add("selectName", mainAddfriendPhone.getText().toString().trim())
                        .build();
                HttpUtil.sendPostOkHttpRequest(HttpPathUtil.vagueSelectFriend(), body, true, new Callback() {
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
                        final String resp = response.body().string();
                        LogUtil.d(TAG, resp);
                        final List<FriendList> userFriends = SQLite.select()
                                .from(FriendList.class)
                                .where(FriendList_Table.uid.eq(MyApplication.user.getUid()))
                                .queryList();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                userInfoList2 = JSON.parseArray(resp, User.class);
                                for (User userInfo:userInfoList) {
                                    for (FriendList userFriend: userFriends) {
                                        if (!userInfo.getNumber().equals(userFriend.getFriendNumber())){
                                            userInfoList.add(userInfo);
                                        }
                                    }
                                }
                                if (userInfoList.size() == 0) {
                                    new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("友情提示")
                                            .setContentText("有关此信息的人或已成为您的好友，已没有更多信息")
                                            .show();
                                }
                                Util.closeKeyBoard(getActivity());
                                addSearchAdapter = new friend_add_search(MyApplication.user.getUid(), status, userInfoList, getActivity());
                                mainAddfriendRecycler.setAdapter(addSearchAdapter);
                            }
                        });
                    }
                });
                break;
            default:
                break;
        }
    }

    //匹配关系
    private void matchStatus(String type) {
        switch (type) {
            case FragmentActivitys.addfamily:
                status = 1;
                break;
            case FragmentActivitys.addfriend:
                status = 5;
                break;
            case FragmentActivitys.addlover:
                status = 2;
                break;
            default:
                break;
        }
    }

    //初始化视图,设置点击项,配置适配器
    private void initView(View view) {
        mainAddfriendRecycler = (RecyclerView) view.findViewById(R.id.main_addfriend_recycler);
        mainAddfriendSearch = (Button) view.findViewById(R.id.main_addfriend_search);
        mainAddfriendPhone = (EditText) view.findViewById(R.id.main_addfriend_phone);

        mainAddfriendSearch.setOnClickListener(this);

        addSearchAdapter = new friend_add_search(MyApplication.user.getUid(), status, userInfoList, getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mainAddfriendRecycler.setLayoutManager(layoutManager);
        mainAddfriendRecycler.setAdapter(addSearchAdapter);
    }
}

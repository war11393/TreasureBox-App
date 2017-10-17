package com.treasurebox.titwdj.treasurebox.Fragment.mainFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.treasurebox.titwdj.treasurebox.Activity.FragmentActivitys;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Utils.HttpUtil;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Adapter.friend_list;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.FriendList;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
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


/**
 * Created by 11393 on 2017/8/14.
 * 主页-朋友页
 */

public class mainFriends extends Fragment {
    private static final String TAG = "mainFriends";

    RecyclerView loverRV, familyRV, friendRV;
    public static List<FriendList> lover = new ArrayList<>(), family = new ArrayList<>(), friend = new ArrayList<>();

    ImageView mainFriendsAddfamily, mainFriendsAddfriend, mainFriendsAddlover;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_friends, container, false);
        initView(view);//初始化视图，添加点击事件
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();//获取数据
    }

    private void initData() {
        lover.clear();family.clear();friend.clear();

        if (MyApplication.userFriendLists != null) {
            matchFriends(MyApplication.userFriendLists);//匹配好友类型
        } else {
            RequestBody body = new FormBody.Builder()
                    .add("uid", MyApplication.user.getUid() + "")
                    .build();
            HttpUtil.sendPostOkHttpRequest(HttpPathUtil.selectAllFriends(), body, true, new Callback() {
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
                    String resp = response.body().string();
                    LogUtil.d(TAG, resp);
                    if (resp.equals("您还没有好友哦，快去添加几个好友吧！")){
                        lover.clear();family.clear();friend.clear();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateAdapter();
                            }
                        });
                        //此处可以提醒用户添加好友去
                    } else {
                        MyApplication.userFriendLists = JSON.parseArray(resp, FriendList.class);
                        for (FriendList friendSample:MyApplication.userFriendLists) {
                            friendSample.save();
                        }
                        matchFriends(MyApplication.userFriendLists);//匹配好友类型
                    }
                }
            });
        }
    }

    //匹配好友类型
    static friend_list lAdapter, faAdapter, frAdapter;
    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext());
    LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
    private void matchFriends(List<FriendList> userFriendList) {
        for (FriendList friend:userFriendList) {
            switch (friend.getCid()) {//1--亲人，2--爱人，3--长辈，4--同事/学，5--朋友
                case 1:
                    family.add(friend);break;
                case 2:
                    lover.add(friend);break;
                case 5:
                    mainFriends.friend.add(friend);break;
                default:
                    LogUtil.d(TAG, "查询全部好友匹配时发现未知分类");
                    break;
            }
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateAdapter();
            }
        });
    }

    //更新适配器数据
    private void updateAdapter() {
        lAdapter.notifyDataSetChanged();
        faAdapter.notifyDataSetChanged();
        frAdapter.notifyDataSetChanged();
    }

    //初始化视图，添加点击事件
    private void initView(View view) {
        mainFriendsAddlover = (ImageView) view.findViewById(R.id.main_friends_addlover);
        mainFriendsAddfriend = (ImageView) view.findViewById(R.id.main_friends_addfriend);
        mainFriendsAddfamily = (ImageView) view.findViewById(R.id.main_friends_addfamily);
        friendRV = (RecyclerView) view.findViewById(R.id.main_friends_friend);
        familyRV = (RecyclerView) view.findViewById(R.id.main_friends_family);
        loverRV = (RecyclerView) view.findViewById(R.id.main_friends_lover);

        if (getActivity() != null) {
            lAdapter = new friend_list(lover, getActivity());
            faAdapter = new friend_list(family, getActivity());
            frAdapter = new friend_list(friend, getActivity());
        } else {
            lAdapter = new friend_list(lover);
            faAdapter = new friend_list(family);
            frAdapter = new friend_list(friend);
        }
        loverRV.setLayoutManager(layoutManager);
        loverRV.setAdapter(lAdapter);
        familyRV.setLayoutManager(layoutManager1);
        familyRV.setAdapter(faAdapter);
        friendRV.setLayoutManager(layoutManager2);
        friendRV.setAdapter(frAdapter);

        mainFriendsAddlover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FragmentActivitys.class);
                intent.putExtra(FragmentActivitys.extra_flag, FragmentActivitys.addlover);
                intent.putExtra(FragmentActivitys.extra_title, "添加恋人");
                startActivity(intent);
            }
        });
        mainFriendsAddfamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FragmentActivitys.class);
                intent.putExtra(FragmentActivitys.extra_flag, FragmentActivitys.addfamily);
                intent.putExtra(FragmentActivitys.extra_title, "添加亲人");
                startActivity(intent);
            }
        });
        mainFriendsAddfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FragmentActivitys.class);
                intent.putExtra(FragmentActivitys.extra_flag, FragmentActivitys.addfriend);
                intent.putExtra(FragmentActivitys.extra_title, "添加朋友");
                startActivity(intent);
            }
        });
    }
}

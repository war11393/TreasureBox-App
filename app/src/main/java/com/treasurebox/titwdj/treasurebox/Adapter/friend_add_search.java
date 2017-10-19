package com.treasurebox.titwdj.treasurebox.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Utils.HttpUtil;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Utils.Util;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.FriendList;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.User;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
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
 * Created by 11393 on 2017/9/10.
 * 添加好友-搜索结果适配器
 */

public class friend_add_search extends RecyclerView.Adapter<friend_add_search.ViewHolder> {
    private static final String TAG = "friend_add_search_adapter";
    private Context mContext;
    private Activity activity;

    private int uid;
    private int cid;
    private List<User> userInfoList;

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User userInfo = userInfoList.get(position);
        Glide.with(mContext).load(userInfo.getUfacing2()).into(holder.mainAddfriendUserhead);
        holder.mainAddfriendUsernum.setText(userInfo.getNumber());
        holder.mainAddfriendUsername.setText(userInfo.getUsername());
        holder.mainAddfriendUserphone.setText(userInfo.getPhone());

        setOnclick(holder.mainAddfriendSearchSuccessButton, position, holder);
    }

    //搜寻成功结果中的按钮
    private void setOnclick(Button successBtn, final int position, final ViewHolder holder) {
        successBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //添加好友
                RequestBody body = new FormBody.Builder()
                        .add("number", userInfoList.get(position).getNumber())
                        .add("uid", uid + "")
                        .add("cid", cid + "")
                        .add("friendUsername", holder.mainAddfriendSearchSuccessName.getText().toString().trim())
                        .build();
                HttpUtil.sendPostOkHttpRequest(HttpPathUtil.addFriend(), body, true, new Callback() {
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
                        if (Util.JsonUtils.isGoodJson(resp)) {
                            FriendList userFriend = new Gson().fromJson(resp, FriendList.class);
                            MyApplication.userFriendLists.add(userFriend);
                            userFriend.save();
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("添加成功o(*￣▽￣*)ブ")
                                            .setConfirmText("关闭")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    LogUtil.d(TAG, cid + "-------cid");
                                                    activity.finish();
                                                }
                                            })
                                            .show();
                                }
                            });
                        } else {
                            if (activity != null) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final SweetAlertDialog dialog = new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
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
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView mainAddfriendUserhead;
        TextView mainAddfriendUsernum, mainAddfriendUsername, mainAddfriendUserphone, mainAddfriendDescription;
        Button mainAddfriendSearchSuccessButton, mainAddfriendSearchFailButton;
        LinearLayout mainAddfriendSearchSuccess, mainAddfriendSearchFail;
        EditText mainAddfriendSearchFailName, mainAddfriendSearchSuccessName;

        public ViewHolder(View itemView) {
            super(itemView);
            mainAddfriendUserhead = (CircleImageView) itemView.findViewById(R.id.main_addfriend_userhead);
            mainAddfriendUsernum = (TextView) itemView.findViewById(R.id.main_addfriend_usernum);
            mainAddfriendUsername = (TextView) itemView.findViewById(R.id.main_addfriend_username);
            mainAddfriendUserphone = (TextView) itemView.findViewById(R.id.main_addfriend_userphone);
            mainAddfriendSearchSuccessButton = (Button) itemView.findViewById(R.id.main_addfriend_search_success_button);
//            mainAddfriendSearchSuccess = (LinearLayout) itemView.findViewById(R.id.main_addfriend_search_success);
            mainAddfriendSearchSuccessName = (EditText) itemView.findViewById(R.id.main_addfriend_search_success_name);

            //查无此人的结果---待扩展
//            mainAddfriendSearchFail = (LinearLayout) itemView.findViewById(R.id.main_addfriend_search_fail);
//            mainAddfriendSearchFailName = (EditText) itemView.findViewById(R.id.main_addfriend_search_fail_name);
//            mainAddfriendDescription = (TextView) itemView.findViewById(R.id.main_addfriend_description);
//            mainAddfriendSearchFailButton = (Button) itemView.findViewById(R.id.main_addfriend_search_fail_button);
        }
    }

    public friend_add_search(int uid, int cid, List<User> userInfoList, Activity activity) {
        this.cid = cid;
        this.uid = uid;
        this.userInfoList = userInfoList;
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        return userInfoList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_friends_search_result, parent, false);
        return new ViewHolder(view);
    }
}

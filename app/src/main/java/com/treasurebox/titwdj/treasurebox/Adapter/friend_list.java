package com.treasurebox.titwdj.treasurebox.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.treasurebox.titwdj.treasurebox.Activity.FriendActivity;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Utils.HttpUtil;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Utils.Util;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.FriendList;

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
 * Created by 11393 on 2017/8/18.
 * 好友列表适配器
 */

public class friend_list extends RecyclerView.Adapter<friend_list.ViewHolder> {
    private static final String TAG = "friend_list";
    private Context mContext;
    private Activity mActivity;

    private List<FriendList> friends;

    public FriendList people;

    public friend_list(List<FriendList> peoples, Activity activity) {
        this.friends = peoples;
        this.mActivity = activity;
    }

    public friend_list(List<FriendList> peoples) {
        this.friends = peoples;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (friends.size() == 0) {
            return;
        } else {
            people = friends.get(position);
            final int fId = people.getFid();
            final String fNumber = people.getFriendNumber();

            Glide.with(mContext).load(people.getFacing2()).into(holder.friendsHead);
            holder.friendsName.setText("".equals(people.getFriendUsername()) ? people.getFriendNickname() : people.getFriendUsername());

            //给每个子项视图设置点击监听
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RequestBody body = new FormBody.Builder()
                            .add("uid", MyApplication.user.getUid() + "")
                            .add("fid", fId + "")
                            .add("friendNumber", fNumber).build();
                    HttpUtil.sendPostOkHttpRequest(HttpPathUtil.selectFriendData(), body, true, new Callback() {
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
                                Intent intent = new Intent(mContext, FriendActivity.class);
                                intent.putExtra("friend", resp);
                                intent.putExtra("fid", fId + "");
                                mContext.startActivity(intent);
                            } else {
                                if (mActivity != null) {
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            final SweetAlertDialog dialog = new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE)
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

            //给每个子项视图设置长按监听，长按修改备注
            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    holder.newFriendName.setVisibility(View.VISIBLE);
                    holder.newFriendNameBtn.setVisibility(View.VISIBLE);

                    holder.newFriendName.addTextChangedListener(new TextWatcher() {//文本域文本长度监听
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            if (editable.toString().length() != 0) {
                                holder.newFriendNameBtn.setText("完成");
                            } else {
                                holder.newFriendNameBtn.setText("取消");
                            }
                        }
                    });

                    holder.newFriendNameBtn.setOnClickListener(new View.OnClickListener() {//修改备注名按钮监听
                        @Override
                        public void onClick(View view) {
                            if (!TextUtils.isEmpty(holder.newFriendName.getText())) {
                                //修改好友备注
                                RequestBody body = new FormBody.Builder()
                                        .add("fid", fId + "")
                                        .add("friendUsername", holder.newFriendName.getText().toString().trim()).build();
                                HttpUtil.sendPostOkHttpRequest(HttpPathUtil.updateFriendName(), body, true, new Callback() {
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
                                        if ("".equals(resp)) {//修改成功
                                            LogUtil.d(TAG, "修改备注名：" + holder.newFriendName.getText().toString().trim());
                                        } else {
                                            LogUtil.d(TAG, "修改备注名：可能是修改失败了" + holder.newFriendName.getText().toString().trim());
                                        }
                                    }
                                });
                            }
                            holder.newFriendName.setVisibility(View.GONE);
                            holder.newFriendNameBtn.setVisibility(View.GONE);
                        }
                    });

                    return true;
                }
            });
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        CircleImageView friendsHead;
        TextView friendsName;
        EditText newFriendName;
        Button newFriendNameBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            try {
                cardView = (CardView) itemView;
            } catch (Exception e) {
                e.printStackTrace();
            }
            friendsHead = (CircleImageView) itemView.findViewById(R.id.main_friends_head);
            friendsName = (TextView) itemView.findViewById(R.id.main_friends_name);
            newFriendName = (EditText) itemView.findViewById(R.id.main_friends_newname);
            newFriendNameBtn = (Button) itemView.findViewById(R.id.main_friends_newname_btn);
        }
    }

    @Override
    public int getItemCount() {
        return friends.size() == 0 ? 1 : friends.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (friends.size() == 0) {
            return -1;
        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view;
        if (viewType == -1) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_empty_friend, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_friends_member, parent, false);
        }
        return new ViewHolder(view);
    }
}

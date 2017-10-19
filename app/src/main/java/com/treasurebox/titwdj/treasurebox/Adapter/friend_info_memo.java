package com.treasurebox.titwdj.treasurebox.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.treasurebox.titwdj.treasurebox.Activity.FriendActivity;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Utils.HttpUtil;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.MemoList;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.MemoList_Table;

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
 * Created by 11393 on 2017/8/19.
 * 好友详细信息--便签格式数据适配器
 * 可实现自定义得便签长按删除得功能
 */
public class friend_info_memo extends RecyclerView.Adapter<friend_info_memo.ViewHolder>{
    private static final String TAG = "friend_info_memo";

    private Context mContext;
    private Activity activity;

    private List<MemoList> infoList;

    public friend_info_memo(List<MemoList> infoList, Activity activity) {
        this.infoList = infoList;
        this.activity = activity;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MemoList item = infoList.get(position);
        holder.title.setText(item.getMemoName());
        holder.content.setText(item.getFriendContent());

        if (item.getMemoId() != -1) {
            holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("便签操作")
                                    .setContentText("是否删除这条便签？")
                                    .setConfirmText("删除")
                                    .setCancelText("取消")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {//删除
                                            RequestBody body = new FormBody.Builder()
                                                    .add("memoId", item.getMemoId() + "").build();
                                            HttpUtil.sendPostOkHttpRequest(HttpPathUtil.deleteMemo(), body, true, new Callback() {
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
                                                    if ("".equals(resp)) {
                                                        List<MemoList> memoList = FriendActivity.friendInfo.getMemoList();
                                                        for (int i = 0; i < memoList.size(); i++) {
                                                            if (memoList.get(i).getMemoId() == item.getMemoId()){
                                                                memoList.remove(i);
                                                                break;
                                                            }
                                                        }

                                                        SQLite.delete(MemoList.class).where(MemoList_Table.memoId.eq(item.getMemoId())).execute();

                                                        final List<MemoList> memoList2 = memoList;
                                                        FriendActivity.friendInfo.setMemoList(memoList2);
                                                        FriendActivity.friendInfo.save();
                                                        LogUtil.d(TAG, "删除便签：" + item.getMemoId() + "----" + item.getMemoName() + "----" + resp + memoList.size());
                                                        activity.runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                FriendActivity.adapter = new friend_info_memo(memoList2, activity);
                                                                FriendActivity.mainFriendinfoRecycler.setAdapter(FriendActivity.adapter);
                                                                //此处可扩展撤销此操作得交互框
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                            sweetAlertDialog.dismiss();
                                        }
                                    });
                            sweetAlertDialog.show();
                        }
                    });
                    return true;
                }
            });
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        TextView title, content;

        public ViewHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout) itemView;
            title = itemView.findViewById(R.id.item_friend_title);
            content = itemView.findViewById(R.id.item_friend_content);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_friend_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }
}

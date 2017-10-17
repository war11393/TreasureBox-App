package com.treasurebox.titwdj.treasurebox.Adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.treasurebox.titwdj.treasurebox.Fragment.noteFragments.noteWith;
import com.treasurebox.titwdj.treasurebox.Fragment.noteFragments.noteWrite;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.FriendList;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 11393 on 2017/8/30.
 * 权限选择列表--关于谁--适配器
 */
public class note_permission_with extends RecyclerView.Adapter<note_permission_with.ViewHolder> {
    private Context mContext;

    private int status;
    private List<FriendList> userFriendLists;
    public List<Boolean> userFriendChecked;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Glide.with(mContext).load(R.drawable.part_check).into(((ViewHolder)msg.obj).image);
                    break;
                case 1:
                    Glide.with(mContext).load(R.drawable.part_trans).into(((ViewHolder)msg.obj).image);
                    break;
            }
        }
    };

    /**
     * 列表化好友选择适配器构造方法
     * @param peoples 好友列表userFriendList
     * @param status 状态位：0-权限选择，1-关于谁
     */
    public note_permission_with(List<FriendList> peoples, int status) {
        this.userFriendLists = peoples;
        this.status = status;
        userFriendChecked = new ArrayList<>();

        boolean a = false;
        for (int i = 0; i < peoples.size(); i++) {
            userFriendChecked.add(a);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final FriendList userFriend = userFriendLists.get(position);

        if ("".equals(userFriend.getFriendUsername())){
            holder.name.setText(userFriend.getFriendNickname());
        } else {
            holder.name.setText(userFriend.getFriendNickname() + "(" + userFriend.getFriendUsername() + ")");
        }
        Glide.with(mContext).load(R.drawable.part_trans).into(holder.image);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setImage(position, holder);
            }
        });

        if (status == 1) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(holder.linearLayout.getLayoutParams());
            params.setMargins(0, 0, 0, 0);
            holder.linearLayout.setLayoutParams(params);
        }
    }

    //设置图片
    private void setImage(final int position, ViewHolder holder) {
        userFriendChecked.set(position, !userFriendChecked.get(position));
        setNumberList(position);
        Message msg = new Message();
        if (userFriendChecked.get(position)){
            msg.what = 0;
        } else {
            msg.what = 1;
        }
        msg.obj = holder;
        handler.sendMessage(msg);
    }

    //设置账号集
    private void setNumberList(int position) {
        if (status == 0) {//权限选择
            if (userFriendChecked.get(position)) {
                noteWrite.notePermissionList.add(userFriendLists.get(position).getFriendNumber());
            } else {
                noteWrite.notePermissionList.remove(userFriendLists.get(position).getFriendNumber());
            }
        } else {
            if (userFriendChecked.get(position)) {
                noteWith.noteWithList.add(userFriendLists.get(position).getFriendNumber());
                noteWith.noteWithNameList.add("".equals(userFriendLists.get(position).getFriendUsername())?userFriendLists.get(position).getFriendNickname():userFriendLists.get(position).getFriendUsername());
            } else {
                noteWith.noteWithList.remove(userFriendLists.get(position).getFriendNumber());
                noteWith.noteWithNameList.remove("".equals(userFriendLists.get(position).getFriendUsername())?userFriendLists.get(position).getFriendNickname():userFriendLists.get(position).getFriendUsername());
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        CircleImageView image;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.item_permission_linear);
            image = (CircleImageView) itemView.findViewById(R.id.item_permission_image);
            name = (TextView) itemView.findViewById(R.id.item_permission_name);
        }
    }

    @Override
    public int getItemCount() {
        return userFriendLists.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_permission_member, parent, false);
        return new ViewHolder(view);
    }
}

package com.treasurebox.titwdj.treasurebox.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Model.robot.RobotMsg;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 予以心 on 2017/7/24.
 */

public class user_robot_msg extends RecyclerView.Adapter<user_robot_msg.ViewHolder> {

    private Context mContext;

    private List<RobotMsg> mMsgList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftlayout;
        LinearLayout rightlayout;
        TextView leftMsg;
        TextView rightMsg;
        CircleImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            leftlayout = (LinearLayout) itemView.findViewById(R.id.user_robot_left_layout);
            rightlayout = (LinearLayout) itemView.findViewById(R.id.user_robot_right_layout);
            leftMsg = (TextView) itemView.findViewById(R.id.user_robot_left_msg);
            rightMsg = (TextView) itemView.findViewById(R.id.user_robot_right_msg);
            imageView = (CircleImageView) itemView.findViewById(R.id.user_robot_userhead);
        }
    }

    public user_robot_msg(List<RobotMsg> mMsgList) {
        this.mMsgList = mMsgList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view;
        if (viewType == -1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty_robot, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_userrobot_msg, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mMsgList.size() == 0) {
            return;
        } else {
            RobotMsg msg = mMsgList.get(position);
            if (msg.getType() == RobotMsg.TYPE_RECEIVED){
                holder.leftlayout.setVisibility(View.VISIBLE);//设置布局可见
                holder.rightlayout.setVisibility(View.GONE);//设置布局不可见
                holder.leftMsg.setText(msg.getContent());
            } else if (msg.getType() == RobotMsg.TYPE_SENT){
                holder.leftlayout.setVisibility(View.GONE);
                holder.rightlayout.setVisibility(View.VISIBLE);
                holder.rightMsg.setText(msg.getContent());
                Glide.with(mContext).load(R.drawable.part_defaultimage).into(holder.imageView);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mMsgList.size() == 0?1:mMsgList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mMsgList.size() == 0){
            return -1;
        }
        else
            return super.getItemViewType(position);
    }
}

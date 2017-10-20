package com.treasurebox.titwdj.treasurebox.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.treasurebox.titwdj.treasurebox.Fragment.userFragments.userRemain;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.FriendInfo;
import com.treasurebox.titwdj.treasurebox.Model.nother.HotContent;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Utils.Util;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 11393 on 2017/9/17.
 */

public class HotList extends RecyclerView.Adapter<HotList.ViewHolder> {

    private Context mContext;
    private Activity mActivity;
    private List<HotContent> contentList;
    private int flag;

    public HotList(FragmentActivity activity, List<HotContent> contentList, int flag) {
        this.mActivity = activity;
        this.contentList = contentList;
        this.flag = flag;
    }

    @Override
    public void onBindViewHolder(HotList.ViewHolder holder, int position) {
        if (position == 0) {
            holder.cardView.setVisibility(View.VISIBLE);
            holder.linearLayout.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(0x00000000);
            Glide.with(mActivity).load(flag==1?R.drawable.home_hot1:R.drawable.home_hot2).into(holder.image);
        } else if (position == 1 && contentList.size() == 0) {
            return;
        } else {
            holder.cardView.setVisibility(View.GONE);
            holder.linearLayout.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(0xffffffff);
            HotContent hot = contentList.get(position - 1);
            String name = Util.RandomName.getRandomDoubleName();
            Glide.with(mActivity).load("").placeholder(
                    Util.MakeRandomPhoto.getInstance().setWidth(48).setHeight(48).setTxtSize(20).setTxtColor(Color.parseColor("#ffffff")).setShowNum(2).makeRandomPhotoDrawable(name.substring(0,1))
            ).dontAnimate().into(holder.imageView);
            holder.userName.setText(name);
            holder.content.setText(hot.getTopicContent());
            holder.time.setText(hot.getTime());
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView, item;
        LinearLayout linearLayout;
        CircleImageView imageView;
        ImageView image;
        TextView content, userName, time;
        public ViewHolder(View itemView) {
            super(itemView);
            if (itemView.getId() != R.id.hot_total_container) {
                return;
            } else {
                itemView = itemView.findViewById(R.id.hot_total_container);
                image = itemView.findViewById(R.id.hot_image);
                cardView = itemView.findViewById(R.id.hot_image_container);
                linearLayout = itemView.findViewById(R.id.hot_normal_container);
                imageView = itemView.findViewById(R.id.hot_item_head);
                content = itemView.findViewById(R.id.hot_item_content);
                userName = itemView.findViewById(R.id.hot_item_name);
                time = itemView.findViewById(R.id.hot_item_time);
            }
        }
    }

    @Override
    public HotList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view;
        if (viewType == -1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty_hot, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hot, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (contentList.size() == 0) {
            return 2;
        } else {
            return contentList.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 1 && contentList.size() == 0) {
            return -1;
        } else {
            return super.getItemViewType(position);
        }
    }
}

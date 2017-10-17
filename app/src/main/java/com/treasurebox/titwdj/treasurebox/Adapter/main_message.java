package com.treasurebox.titwdj.treasurebox.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Custom.View.PopWindow_ChooseCall;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.Message;

import java.util.List;

/**
 * Created by 11393 on 2017/9/17.
 */

public class main_message extends RecyclerView.Adapter<main_message.ViewHolder> {
    private static final String splitKey = "dyqhhrww";

    private Context mContext;
    private Activity activity;
    private List<Message> Messages;
    private PopWindow_ChooseCall mPop;

    private String phone;
    Intent callIntent;

    public main_message(List<Message> Messages, Activity activity) {
        this.Messages = Messages;
        this.activity = activity;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (Messages.size() == 0) {
            return;
        } else {
            final Message Message = Messages.get(position);
            if (Message.getWcintent().indexOf(splitKey) >= 0) {
                String[] strings = Message.getWcintent().split(splitKey);
                if (strings.length == 2) {
                    holder.mainMessageContent.setText(strings[0]);
                    holder.mainMessageSysMsg.setText(strings[1]);
                } else {
                    holder.mainMessageContent.setText(strings[0]);
                    holder.mainMessageSysMsg.setVisibility(View.GONE);
                }
            } else {
                holder.mainMessageContent.setText(Message.getWcintent());
                holder.mainMessageSysMsg.setVisibility(View.GONE);
            }
            holder.mainMessageTitle.setText("来自过去的提醒");
            holder.mainMessageName.setText(Message.getWto());
            holder.mainMessagePhone.setText(Message.getWphone());
            holder.mainMessageTime.setText(Message.getWtime());
            holder.mainMessagePhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    phone = Message.getWphone();
                    callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phone));//给打电话intent装入好友电话号
                    mPop.showAtLocation(activity.findViewById(R.id.main_message_recycler) , Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                }
            });
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mainMessageTitle, mainMessageContent, mainMessageSysMsg, mainMessagePhone, mainMessageName, mainMessageTime;
        LinearLayout linearLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.main_message_to);
            mainMessageTitle = (TextView) itemView.findViewById(R.id.main_message_title);
            mainMessageContent = (TextView) itemView.findViewById(R.id.main_message_content);
            mainMessageSysMsg = (TextView) itemView.findViewById(R.id.main_message_sysMsg);
            mainMessagePhone = (TextView) itemView.findViewById(R.id.main_message_phone);
            mainMessageName = (TextView) itemView.findViewById(R.id.main_message_name);
            mainMessageTime = (TextView) itemView.findViewById(R.id.main_message_time);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view;
        if (viewType == -1) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_empty_message, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_main_message, parent, false);
        }
        mPop = new PopWindow_ChooseCall(activity, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.call_call:
                        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, 3);
                        } else {
                            activity.startActivity(callIntent);
                        }
                        mPop.dismiss();
                        break;
                    case R.id.call_sendSms:
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone));
                        activity.startActivity(intent);
                        mPop.dismiss();
                        break;
                }
            }
        });
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return Messages.size() ==0?1:Messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (Messages.size() == 0) {
            return -1;
        } else {
            return super.getItemViewType(position);
        }
    }
}

package com.treasurebox.titwdj.treasurebox.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.treasurebox.titwdj.treasurebox.Fragment.userFragments.userRemain;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.FriendInfo;

import java.util.List;

/**
 * Created by 11393 on 2017/9/17.
 */

public class simple_text extends RecyclerView.Adapter<simple_text.ViewHolder> {

    private Context mContext;
    private Activity mActivity;
    private List<FriendInfo> friendLists;

    public simple_text(List<FriendInfo> friendLists, FragmentActivity activity) {
        this.friendLists = friendLists;
        this.mActivity = activity;
    }

    @Override
    public void onBindViewHolder(simple_text.ViewHolder holder, int position) {
        final FriendInfo friend = friendLists.get(position);
        String string = "";
        string += friend.getFriendUsername();
        string += "(" + friend.getPhone() + ")";
        holder.simpleText.setText(string);
        LogUtil.d("string", string);

        final String finalString = string;
        holder.simpleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRemain.number = friend.getFriendUsername();
                userRemain.with = finalString;
                userRemain.phone = friend.getPhone();
                userRemain.userRemainWithText.setText(userRemain.with);
                mActivity.finish();
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView simpleText;
        public ViewHolder(View itemView) {
            super(itemView);
            simpleText = itemView.findViewById(R.id.simple_text);
        }
    }

    @Override
    public simple_text.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple_text, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return friendLists.size();
    }
}

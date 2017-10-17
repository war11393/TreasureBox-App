package com.treasurebox.titwdj.treasurebox.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.sqk.emojirelease.EmojiUtil;
import com.treasurebox.titwdj.treasurebox.Model.nother.Note;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;
import com.treasurebox.titwdj.treasurebox.Custom.View.PopWindow_PAR;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.FriendList;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.FriendList_Table;

import java.io.IOException;
import java.util.List;

/**
 * Created by 11393 on 2017/8/21.
 * 评论回复适配器
 */
public class note_review extends RecyclerView.Adapter<note_review.ViewHolder> {
    private Context mContext;
    private List<Note.NoteEvaluate> parList;
    private PopWindow_PAR mPopWindow = null;
    private Activity mActivity;

    private RecyclerView recyclerView;
    private Note Note;
    private Note.NoteEvaluate oldReview;

    public note_review(Note Note, PopWindow_PAR mPopWindow, Activity mActivity, RecyclerView recyclerView) {
        this.Note = Note;
        this.parList = Note.getEvaluate();
        this.mPopWindow = mPopWindow;
        this.mActivity = mActivity;
        this.recyclerView = recyclerView;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Note.NoteEvaluate review = parList.get(position);
        if (position != 0) {
            oldReview = parList.get(position - 1);
        }

        FriendList subFriend, obFriend;
        String content = "";

        if (review.getEflag() == 1) {//1：评论纸条
            if (review.getIfObv() == 0) {//主体选择匿名
                content = content + "匿名：";//未来可以考虑加入随机命名
            } else if (review.getCommentId() == MyApplication.user.getUid() ){
                content = content + "<font color='#606060'>" + MyApplication.user.getUsername() + "</font>：";
            } else {
                subFriend = SQLite.select()
                        .from(FriendList.class)
                        .where(FriendList_Table.friendNumber.eq(review.getCommentNum()))
                        .and(FriendList_Table.uid.eq(MyApplication.user.getUid()))
                        .queryList().get(0);
                String string = subFriend.getFriendUsername().equals("")?subFriend.getFriendNickname():subFriend.getFriendUsername();
                content = content + "<font color='#77beff'>" + string + "</font>：";
            }
        } else if (review.getEflag() == 2 || review.getEflag() == 3) {//2：回复评价
            if (review.getIfObv() == 0) {//主体选择匿名
                content = content + "匿名";//未来可以考虑加入随机命名
            } else if (review.getCommentId() == MyApplication.user.getUid() ){
                content = content + "<font color='#606060'>" + MyApplication.user.getUsername() + "</font>";
            } else {
                subFriend = SQLite.select()
                        .from(FriendList.class)
                        .where(FriendList_Table.friendNumber.eq(review.getCommentNum()))
                        .and(FriendList_Table.uid.eq(MyApplication.user.getUid()))
                        .queryList().get(0);
                content = content + "<font color='#77beff'>" + MyApplication.user.getUsername() + "</font>";
            }

            if (oldReview.getCommentNum() == null) {//客体选择匿名
                content += " 回复 匿名：";
            } else if (oldReview.getCommentNum().equals(MyApplication.user.getNumber())){
                content += " 回复 <font color='#606060'>" + MyApplication.user.getUsername() + "</font>：";
            } else {
                obFriend = SQLite.select()
                        .from(FriendList.class)
                        .where(FriendList_Table.friendNumber.eq(review.getCommentNum()))
                        .and(FriendList_Table.uid.eq(MyApplication.user.getUid()))
                        .queryList().get(0);
                String string = obFriend.getFriendUsername().equals("")?obFriend.getFriendNickname():obFriend.getFriendUsername();
                content += " 回复 <font color='#77beff'>" + string + "</font>：";
            }
        }

        content += review.getEcontent();//添加评论内容
        displayTextView(holder.reviewContent, content);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopWindow.setNote(Note);
                mPopWindow.setNoteId(Note.getNoteId());
                mPopWindow.setReviewRecycler(recyclerView);
                mPopWindow.setReplyEid(review.getEid());
                if (review.getReplyId() == 0) {
                    mPopWindow.setInputType(PopWindow_PAR.revert);
                    LogUtil.d("setInputType", "revert");
                } else {
                    mPopWindow.setInputType(PopWindow_PAR.toRevert);
                    LogUtil.d("setInputType", "toRevert");
                }
                mPopWindow.setReplyId(review.getCommentId());
                mPopWindow.showAtLocation(mActivity.findViewById(R.id.activity_note), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                LogUtil.d("review.getReplyId()", "" + review.getReplyId());
                LogUtil.d("Note.getNoteId()", "" + Note.getNoteId());
                LogUtil.d("review.getEid()", "" + review.getEid());
            }
        });
    }

    //提取字串，将标记为表情的部分匹配出表情并显示
    public void displayTextView(TextView textView, String econtent) {
        try {
            EmojiUtil.handlerEmojiText(textView, Html.fromHtml(econtent).toString(), mActivity.getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView reviewSubject, reviewObject, reviewContent, subText;
        LinearLayout linearLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout) itemView;
            reviewSubject = itemView.findViewById(R.id.item_review_subject);
            reviewObject = itemView.findViewById(R.id.item_review_object);
            reviewContent = itemView.findViewById(R.id.item_review_content);
            subText = itemView.findViewById(R.id.item_review_text);
        }
    }

    @Override//加载子项视图并借助缓存类返回
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recycler_review, parent, false);
        return new ViewHolder(view);
    }

    @Override//返回子项数目
    public int getItemCount() {
        return parList.size();
    }
}

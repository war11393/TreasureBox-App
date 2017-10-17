package com.treasurebox.titwdj.treasurebox.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.treasurebox.titwdj.treasurebox.Model.nother.Note;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Utils.Util;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;
import com.treasurebox.titwdj.treasurebox.Custom.View.PopWindow_PAR;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.FriendList;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.FriendList_Table;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 11393 on 2017/8/15.
 * 纸条数据适配器
 */

public class note_list extends RecyclerView.Adapter<note_list.ViewHolder> implements View.OnClickListener{
    private static final String TAG = "note_list";

    private Context mContext;
    private Activity mActivity;
    private int status;
    private List<Note> mNote;

    private static PopWindow_PAR mPopWindow = null;
    private int width;

    //适配数据到每一个子项
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (mNote.size() == 0) {
            return;
        } else {
            final Note Note = mNote.get(position);
            if (Note.getUser().getNumber().equals(MyApplication.user.getNumber())){//头像显示+名称显示
                LogUtil.d(TAG, "我的纸条：" + Note.getNoteId() + "---" + Note.getNoteContent());
                Glide.with(mContext).load(MyApplication.user.getUfacing2()).into(holder.head);
                holder.name.setText(MyApplication.user.getUsername());
            } else {
                LogUtil.d(TAG, "好友纸条：" + Note.getNoteId() + "---" + Note.getNoteContent());
                FriendList friendList = SQLite.select()
                        .from(FriendList.class)
                        .where(FriendList_Table.friendNumber.eq(Note.getUser().getNumber()))
                        .and(FriendList_Table.uid.eq(MyApplication.user.getUid()))
                        .querySingle();
                if (friendList != null) {
                    Glide.with(mContext).load(friendList.getFacing2()).into(holder.head);
                    holder.name.setText(friendList.getFriendUsername().equals("")?friendList.getFriendNickname():friendList.getFriendUsername());
                }
            }

            holder.time.setText(Note.getTime());//时间显示
            holder.cntText.setText(Note.getNoteContent());//显示文字内容
            setImageContent(holder, Note.getImageList());//填充图片区域---待优化
            if ("".equals(Note.getLocate())){//位置显示
                holder.locate.setVisibility(View.GONE);
            } else {
                holder.locate.setText(Note.getLocate());
            }
            setHeart(holder.heart, Note.getMood());

            //显示关于谁
            List<String> friendsNum = Note.getNoteAdout();
            String friendNum;
            holder.withWho.setText("With:");
            for (int i = 0; i < friendsNum.size(); i++) {
                friendNum = friendsNum.get(i);
                if (friendNum.equals(MyApplication.user.getNumber())) {
                    holder.withWho.setText(holder.withWho.getText().toString() + "我");
                    if (i != (friendsNum.size() - 1)) {
                        holder.withWho.setText(holder.withWho.getText().toString() + ",");
                    }
                } else {
                    FriendList friend = SQLite.select()
                            .from(FriendList.class)
                            .where(FriendList_Table.friendNumber.eq(friendNum))
                            .and(FriendList_Table.uid.eq(MyApplication.user.getUid()))
                            .querySingle();
                    if (friend != null) {
                        if ("".equals(friend.getFriendUsername())) {
                            holder.withWho.setText(holder.withWho.getText().toString() + friend.getFriendNickname());
                        } else {
                            holder.withWho.setText(holder.withWho.getText().toString() + friend.getFriendUsername());
                        }
                        if (i != (friendsNum.size() - 1)) {
                            holder.withWho.setText(holder.withWho.getText().toString() + ",");
                        }
                    }
                }
            }
            if ("With:".equals(holder.withWho.getText().toString().trim())){
                holder.withWho.setText("With:无");
            }

            setReviewInfo(holder.reviewRecycler, Note);//Note.getEvaluate()配置评论相关信息------------------------------待完善

            //配置点击事件
            holder.delete.setOnClickListener(this);//删除---------功能未实现
            holder.dianzan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });//点赞
            holder.pinglun.setOnClickListener(new View.OnClickListener() {//评论纸条，这里一定是直接评论
                @Override
                public void onClick(View view) {
                    mPopWindow.setNoteId(Note.getNoteId());
                    mPopWindow.setNote(Note);
                    mPopWindow.setReviewRecycler(holder.reviewRecycler);
                    mPopWindow.showAtLocation(mActivity.findViewById(R.id.activity_note), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                }
            });//评论
            holder.shoucang.setOnClickListener(this);//收藏字条---功能未实现
        }
    }

    //匹配心情数据
    private void setHeart(ImageView heart, int mood) {
        switch (mood) {
            case 1:
                Glide.with(mContext).load(R.drawable.note_heartsty_12).into(heart);break;
            case 2:
                Glide.with(mContext).load(R.drawable.note_heartsty_22).into(heart);break;
            case 3:
                Glide.with(mContext).load(R.drawable.note_heartsty_32).into(heart);break;
            case 4:
                Glide.with(mContext).load(R.drawable.note_heartsty_42).into(heart);break;
            case 5:
                Glide.with(mContext).load(R.drawable.note_heartsty_52).into(heart);break;
        }
    }

    //配置评论
    private note_review reviewAdapter;
    public void setReviewInfo(RecyclerView recycler, Note Note) {
        if (Note.getEvaluate() == null)
            return;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(linearLayoutManager);//设置布局管理器
        reviewAdapter = new note_review(Note, mPopWindow, mActivity, recycler);
        recycler.setAdapter(reviewAdapter);
    }

    //点击处理
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.note_recycler_item_delete://删除这个纸条
                LogUtil.d(TAG, "删除");break;
            case R.id.note_recycler_item_shoucang://收藏为自己的纸条
                LogUtil.d(TAG, "收藏");break;
        }
    }

    //自定义视图缓冲类
    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView head;
        TextView name, time, cntText, locate, withWho;
        ImageView delete, dianzan, pinglun, shoucang, heart;
        GridLayout cntImages;
        RecyclerView reviewRecycler;
        int width;
        public ViewHolder(View itemView) {
            super(itemView);
            width = itemView.getWidth();

            head = (CircleImageView) itemView.findViewById(R.id.note_recycler_item_head);
            name = (TextView) itemView.findViewById(R.id.note_recycler_item_name);
            time = (TextView) itemView.findViewById(R.id.note_recycler_item_time);
            delete = (ImageView) itemView.findViewById(R.id.note_recycler_item_delete);
            cntText = (TextView) itemView.findViewById(R.id.note_recycler_item_cnttext);
            cntImages = (GridLayout) itemView.findViewById(R.id.note_recycler_item_cntimage);
            locate = (TextView) itemView.findViewById(R.id.note_recycler_item_locate);
            dianzan = (ImageView) itemView.findViewById(R.id.note_recycler_item_dianzan);
            pinglun = (ImageView) itemView.findViewById(R.id.note_recycler_item_pinglun);
            shoucang = (ImageView) itemView.findViewById(R.id.note_recycler_item_shoucang);
            heart = (ImageView) itemView.findViewById(R.id.note_recycler_item_heart);
            withWho = (TextView) itemView.findViewById(R.id.note_recycler_item_status);
            reviewRecycler = (RecyclerView) itemView.findViewById(R.id.note_recycler_item_review_recycler);
        }
    }

    //构造方法
    public note_list(Activity activity, List<Note> mNoteList, int status) {
        this.mActivity = activity;
        this.mNote = mNoteList;
        this.status = status;
    }

    //初始化视图
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
            width = mContext.getResources().getDisplayMetrics().widthPixels;
        }
        if (mPopWindow == null) {
            mPopWindow = new PopWindow_PAR(mActivity, PopWindow_PAR.review, this);//初始化弹窗
        }
        View view;
        if (viewType == -1) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_empty_note, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_recycler_note, parent, false);
        }
        return new ViewHolder(view);
    }

    //获取总长
    @Override
    public int getItemCount() {
        return mNote.size()==0?1:mNote.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mNote.size() == 0) {
            return -1;
        } else {
            return super.getItemViewType(position);
        }
    }

    //填充图片区域---待优化
    private void setImageContent(ViewHolder holder, List<String> pics) {
        holder.cntImages.removeAllViews();
        if (pics.size() == 1){
            holder.cntImages.setColumnCount(1);
            ImageView image = new ImageView(mContext);
            image.setBackgroundColor(0x00cccccc);
            image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            image.setMaxHeight(Util.dip2px(mContext, 300));
            image.setLayoutParams(new ViewGroup.LayoutParams((width - Util.dip2px(mContext, 20)), ViewGroup.LayoutParams.WRAP_CONTENT));
            Glide.with(mContext).load(pics.get(0).toString()).into(image);
            holder.cntImages.addView(image);
        } else if (pics.size() > 1 && pics.size() != 4) {
            holder.cntImages.setColumnCount(3);
            for (int i = 0; i < pics.size(); i++) {
                ImageView image = new ImageView(mContext);
                image.setBackgroundColor(0xffcccccc);
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);

                GridLayout.LayoutParams ps = new GridLayout.LayoutParams(new LinearLayout.LayoutParams(width/3 - Util.dip2px(mContext, 9), width/3 - Util.dip2px(mContext, 9)));
                ps.setMargins(Util.dip2px(mContext, 2),Util.dip2px(mContext, 1),Util.dip2px(mContext, 2),Util.dip2px(mContext, 1));
                ps.setGravity(Gravity.CENTER_VERTICAL);
                image.setLayoutParams(ps);
                Glide.with(mContext).load(pics.get(i).toString()).into(image);
                holder.cntImages.addView(image);
            }
        } else if (pics.size() == 4) {
            holder.cntImages.setColumnCount(3);
            for (int i = 0; i < pics.size(); i++) {
                ImageView image = new ImageView(mContext);
                image.setBackgroundColor(0xffcccccc);
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);

                GridLayout.LayoutParams ps = new GridLayout.LayoutParams(new LinearLayout.LayoutParams(width/3 - Util.dip2px(mContext, 9), width/3 - Util.dip2px(mContext, 9)));
                ps.setMargins(Util.dip2px(mContext, 2),Util.dip2px(mContext, 1),Util.dip2px(mContext, 2),Util.dip2px(mContext, 1));
                ps.setGravity(Gravity.CENTER_VERTICAL);
                image.setLayoutParams(ps);
                if (i < 2 ) {
                    Glide.with(mContext).load(pics.get(i).toString()).into(image);
                } else if (i > 2) {
                    Glide.with(mContext).load(pics.get(i - 1).toString()).into(image);
                } else {
                    image.setBackgroundColor(0x00000000);
                }
                holder.cntImages.addView(image);
            }
        }
    }

    //返回弹窗对象
    public PopWindow_PAR getmPopWindow() {
        return mPopWindow;
    }
}

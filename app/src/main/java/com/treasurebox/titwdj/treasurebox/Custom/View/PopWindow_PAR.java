package com.treasurebox.titwdj.treasurebox.Custom.View;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.sqk.emojirelease.EmojiUtil;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;
import com.treasurebox.titwdj.treasurebox.Model.nother.Note;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Utils.HttpUtil;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Utils.Util;
import com.treasurebox.titwdj.treasurebox.Adapter.note_list;

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
 * Created by 11393 on 2017/8/12.
 * 自定义底部评论框
 */
public class PopWindow_PAR extends PopupWindow {
    private static final String TAG = "PopWindow_PAR";
    public static final String revert = "revert";     //回复
    public static final String toRevert = "toRevert";//回复回复
    public static final String review = "review";    //评论

    private String inputType;
    private boolean emojiShow = false;
    private static Activity mActivity;

    public LinearLayout window, emojiwindow;
    public EditText notePARContent;
    private Button notePARButton;
    private ImageView reviewEmoji, reviewAutonym, reviewAnonymity;

    //参数----commentId：发言的人的id（这里大概是uid）
    private int eFlag = 1;//1--评论，2--回复，3--回复回复
    private boolean isAutonym = true;//false-匿名，true--实名

    private static int noteId, replyId, replyEid;
    private static Note Note;
    private static note_list noteList;
    private static RecyclerView reviewRecycler;

    //构造函数
    public PopWindow_PAR(Activity activity, String flag, note_list noteList) {
        super(activity);
        mActivity = activity;
        //装载布局
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.part_popwindow_review, null);

        inputType = flag;
        this.noteList = noteList;

        initView(view);//初始化弹窗内容
        setType(flag);//根据状态标志设置按钮字样
        setView();//设置点击响应与其他逻辑
        initPopWindow(view);//初始化弹窗
    }

    //设置点击响应与其他逻辑
    private void setView() {
        setContentEnent(notePARContent);

        //评论按钮点击
        notePARButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputType.equals(review)) {//评论纸条
                    if (!TextUtils.isEmpty(notePARContent.getText())) {
                        //发表评论
                        // 所需参数：noteId（评回的纸条的id），commentId（评论人id），ifObv（是否匿名0是,1否），econtent（评论内容）
                        RequestBody body = new FormBody.Builder()
                                .add("noteId", noteId + "")
                                .add("commentId", MyApplication.user.getUid() + "")
                                .add("ifObv", isAutonym ? 1 + "" : 0 + "")
                                .add("econtent", notePARContent.getText().toString().trim()).build();
                        HttpUtil.sendPostOkHttpRequest(HttpPathUtil.pushcomment(), body, true, new Callback() {
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
                                serversLoadTimes = 0;dialog.dismiss();
                                String resp = response.body().string();
                                LogUtil.d(TAG, resp);
                                if (Util.JsonUtils.isGoodJson(resp)) {
                                    List<Note.NoteEvaluate> list =
                                            JSON.parseArray(resp, Note.NoteEvaluate.class);
                                    Note.setEvaluate(list);
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            noteList.setReviewInfo(reviewRecycler, Note);
                                            PopWindow_PAR.this.dismiss();
                                        }
                                    });
                                    clearInput();
                                } else {
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
                        });
                    } else {
                        tx();//提醒用户没写东西
                    }
                } else {//回复评论或回复回复
                    if (!TextUtils.isEmpty(notePARContent.getText())) {
                        /**所需参数：noteId（评回的纸条id），commentId（本次发言人的id），replyId（回复的人的id），ifObv（是否匿名0是,1否），econtent（回复内容）
                         * eflag（被回复的评回的标志位 1：评价纸条 2：回复评价 3：回复回复）*/
                        RequestBody body = new FormBody.Builder()
                                .add("noteId", noteId + "")
                                .add("commentId", MyApplication.user.getUid() + "")
                                .add("replyId", replyId + "")
                                .add("ifObv", isAutonym ? 1 + "" : 0 + "")
                                .add("econtent", notePARContent.getText().toString().trim())
                                .add("eflag", eFlag + "")
                                .add("replyEid", replyEid + "").build();
                        LogUtil.d(TAG, eFlag + "eFlag");
                        HttpUtil.sendPostOkHttpRequest(HttpPathUtil.pushReply(), body, true, new Callback() {
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
                                serversLoadTimes = 0;dialog.dismiss();
                                String resp = response.body().string();
                                LogUtil.d(TAG, resp);
                                if (Util.JsonUtils.isGoodJson(resp)) {
                                    List<Note.NoteEvaluate> list =
                                            JSON.parseArray(resp, Note.NoteEvaluate.class);
                                    Note.setEvaluate(list);
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            noteList.setReviewInfo(reviewRecycler, Note);
                                            PopWindow_PAR.this.dismiss();
                                        }
                                    });
                                    clearInput();
                                } else {
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
                        });
                    } else {
                        tx();//提醒用户没写东西
                    }
                }
            }
        });

        //点击表情弹出emoji选择页
        reviewEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emojiShow) {
                    emojiwindow.setVisibility(View.GONE);
                } else {
                    emojiwindow.setVisibility(View.VISIBLE);
                }
                emojiShow = !emojiShow;
            }
        });

        reviewAutonym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAutonym = true;
                Glide.with(mActivity).load(R.drawable.note_review_plp).into(reviewAutonym);
                Glide.with(mActivity).load(R.drawable.note_review_nm).into(reviewAnonymity);
            }
        });

        reviewAnonymity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAutonym = false;
                Glide.with(mActivity).load(R.drawable.note_review_pl).into(reviewAutonym);
                Glide.with(mActivity).load(R.drawable.note_review_nmp).into(reviewAnonymity);
            }
        });
    }

    private void setContentEnent(EditText notePARContent) {
        //输入内容变动,动态设置按钮
        notePARContent.addTextChangedListener(new TextWatcher() {
            //s:源字串； i:变化的起始位置； i1:本次减少的长度；i2:本次增加的长度
            @Override//变化前,最先执行，源字串为变化前字串
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override//变化详情，中间执行，源字串为变化后字串
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() == 0) {
                    notePARButton.setBackground(mActivity.getResources().getDrawable(R.drawable.selector_graybutton));
                    notePARButton.setTextColor(0xff808080);
                    setType(inputType);
                    notePARButton.setEnabled(false);
                } else {
                    notePARButton.setBackground(mActivity.getResources().getDrawable(R.drawable.selector_button));
                    notePARButton.setTextColor(0xffffffff);
                    setType(inputType);
                    notePARButton.setEnabled(true);
                }
            }

            @Override//变化后，最后执行，，源字串为变化后字串
            public void afterTextChanged(Editable editable) {
            }
        });

        notePARContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emojiwindow.setVisibility(View.GONE);
                emojiShow = false;
            }
        });
    }

    //提取字串，将标记为表情的部分匹配出表情并显示
    public void displayTextView() {
        try {
            EmojiUtil.handlerEmojiText(notePARContent, notePARContent.getText().toString(), mActivity.getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //根据状态标志设置按钮字样
    private void setType(String flag) {
        switch (flag) {
            case revert:
                notePARButton.setText("回复");
                eFlag = 2;
                break;
            case toRevert:
                notePARButton.setText("回复");
                eFlag = 3;
                break;
            case review:
                notePARButton.setText("评论");
                eFlag = 1;
                break;
            default:
                break;
        }
    }

    //初始化弹窗内容
    private void initView(View view) {
        window = (LinearLayout) view;
        emojiwindow = (LinearLayout) view.findViewById(R.id.note_PAR_emoji_choose_visible);
        notePARContent = view.findViewById(R.id.note_PAR_content);
        notePARButton = view.findViewById(R.id.note_PAR_button);
        reviewEmoji = (ImageView) view.findViewById(R.id.note_PAR_emoji);
        reviewAutonym = (ImageView) view.findViewById(R.id.note_PAR_autonym);
        reviewAnonymity = (ImageView) view.findViewById(R.id.note_PAR_anonymity);

        notePARButton.setEnabled(false);
    }

    //初始化弹窗
    private void initPopWindow(View view) {
        //设置SelectPicPopupWindow的View
        this.setContentView(view);

        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setAnimationStyle(R.style.AnimBottom);//设置SelectPicPopupWindow弹出窗体动画效果
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));//设置SelectPicPopupWindow弹出窗体的背景全透明以使view视图完整展示

        //添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int height = window.getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    //set
    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public void setReplyId(int replyId) {
        this.replyId = replyId;
    }

    public void setReplyEid(int replyEid) {
        PopWindow_PAR.replyEid = replyEid;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
        setType(this.inputType);
    }

    public void setNote(Note Note) {
        this.Note = Note;
    }

    public void setReviewRecycler(RecyclerView reviewRecycler) {
        this.reviewRecycler = reviewRecycler;
    }

    //提醒用户没写东西
    private void tx() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("请写些东西再发表哦")
                        .show();
            }
        });
    }

    //清除输入的数据
    private void clearInput() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notePARContent.setText("");
            }
        });
    }
}

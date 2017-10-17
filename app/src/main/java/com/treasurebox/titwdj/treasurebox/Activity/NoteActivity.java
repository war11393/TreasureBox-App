package com.treasurebox.titwdj.treasurebox.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.sqk.emojirelease.Emoji;
import com.sqk.emojirelease.FaceFragment;
import com.treasurebox.titwdj.treasurebox.Model.nother.Note;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.AppManager;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Utils.HttpUtil;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Utils.ProjectUtil;
import com.treasurebox.titwdj.treasurebox.Utils.Util;
import com.treasurebox.titwdj.treasurebox.Adapter.note_list;
import com.treasurebox.titwdj.treasurebox.Custom.EndlessRecyclerOnScrollListener;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
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

public class NoteActivity extends BaseActivity implements FaceFragment.OnEmojiClickListener {
    private static final String TAG = "NoteActivity";
    public static final int MY_NOTE = 3;
    public static final int FRIEND_NOTE = 4;

    Toolbar noteToolbar;
    TextView noteToolbarText;
    ImageView noteToolbarBack;
    RecyclerView noteRecyclerView;
    SwipeRefreshLayout noteSwipeRefresh;

    //视图管理处
    private note_list noteAdapter;
    private int status = 0;//0--自己的纸条，1--好友的纸条
    private int flag = 3;//初始显示项：3--我的纸条，4--好友纸条
    private SweetAlertDialog programDia;
    private int loadCount = 2, oldCount = 1;

    //纸条数据数据存放处
    List<Note> allNotes = new ArrayList<>();
    List<Note> myNotes = new ArrayList<>();
    List<Note> myFriendNotes = new ArrayList<>();
    public static int myNoteId = 0, myFriendNoteId = 0;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ProjectUtil.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        initView();//初始化视图

        sharedPreferences = getSharedPreferences("noteFlag", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        flag = getIntent().getIntExtra("flag", MY_NOTE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.checkFriendList();
        if (flag == MY_NOTE) {
            noteToolbarText.setText("我的纸条");
            myNoteId = 0;
            getMyNotes(myNoteId);
        } else {
            noteToolbarText.setText("好友纸条");
            myFriendNoteId = 0;
            getMyFriendNotes(myFriendNoteId);
        }
    }

    //配置最终显示的纸条数据
    private void setNoteList(List<Note> Notes) {
        allNotes.addAll(Notes);
        if (status == 0) {
            myNoteId = allNotes.size();
        } else if (status == 1) {
            myFriendNoteId = allNotes.size();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                noteAdapter = new note_list(NoteActivity.this, allNotes, status);
                noteRecyclerView.setAdapter(noteAdapter);
                if (status == 0) {
                    noteRecyclerView.scrollToPosition(myNoteId);
                } else if (status == 1) {
                    noteRecyclerView.scrollToPosition(myFriendNoteId);
                }
            }
        });
    }

    //更新数据--上拉显示更多
    private void setLoadMore(){
        noteRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager) noteRecyclerView.getLayoutManager()) {
            @Override
            public void onLoadMore(int currentPage) {
//                programDia.show();
                if (loadCount == oldCount) {
                    if (status == 0) {
                        loadCount++;
                        getMyNotes(myNoteId);
                    } else if (status == 1) {
                        getMyFriendNotes(myFriendNoteId);
                    }
                }
            }
        });
    }

    //更新数据--下拉刷新
    private void initSwipeRefresh() {
        noteSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        noteSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (status == 0) {
                    myNoteId = 0;
                    oldCount = loadCount - 1;
                    allNotes.clear();
                    getMyNotes(myNoteId);
                } else if (status == 1) {
                    myFriendNoteId = 0;
                    oldCount = loadCount - 1;
                    allNotes.clear();
                    getMyFriendNotes(myFriendNoteId);
                }
            }
        });
    }
    /* ------------------------------------------ emoji接口实现 ----------------------------------------- */
    @Override//emoji点击监听
    public void onEmojiDelete() {
        String text = noteAdapter.getmPopWindow().notePARContent.getText().toString();
        if (text.isEmpty()) {
            return;
        }
        if ("]".equals(text.substring(text.length() - 1, text.length()))) {
            int index = text.lastIndexOf("[");
            if (index == -1) {
                int action = KeyEvent.ACTION_DOWN;
                int code = KeyEvent.KEYCODE_DEL;
                KeyEvent event = new KeyEvent(action, code);
                noteAdapter.getmPopWindow().notePARContent.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                noteAdapter.getmPopWindow().displayTextView();
                return;
            }
            noteAdapter.getmPopWindow().notePARContent.getText().delete(index, text.length());
            noteAdapter.getmPopWindow().displayTextView();
            return;
        }
        int action = KeyEvent.ACTION_DOWN;
        int code = KeyEvent.KEYCODE_DEL;
        KeyEvent event = new KeyEvent(action, code);
        noteAdapter.getmPopWindow().notePARContent.onKeyDown(KeyEvent.KEYCODE_DEL, event);
        noteAdapter.getmPopWindow().displayTextView();
    }
    @Override
    public void onEmojiClick(Emoji emoji) {
        if (emoji != null) {
            int index = noteAdapter.getmPopWindow().notePARContent.getSelectionStart();
            Editable editable = noteAdapter.getmPopWindow().notePARContent.getEditableText();
            if (index < 0) {
                editable.append(emoji.getContent());
            } else {
                editable.insert(index, emoji.getContent());
            }
        }
        noteAdapter.getmPopWindow().displayTextView();
    }
    /* ------------------------------------------ emoji接口实现 ----------------------------------------- */

    //初始化标题栏
    private void initBar() {
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.note_appbar);
        noteToolbarText.setText("我的纸条");
        //Glide.with(this).load(R.drawable.part_menu_add).into(noteToolbarImage);
        noteToolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManager.getInstance().finishActivity(NoteActivity.this);
            }
        });
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float i = ((float)verticalOffset/appBarLayout.getTotalScrollRange());
                noteToolbarText.setAlpha(1-i*i);
                noteToolbarBack.setAlpha(1-i*i);
            }
        });
        setSupportActionBar(noteToolbar);
    }

    //点两次退出键退出,点击返回时先关闭菜单
    private long firstTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (AppManager.getInstance().getStackSize() > 1) {
                    AppManager.getInstance().finishActivity(this);
                    return true;
                } else {
                    long secondTime = System.currentTimeMillis();
                    if (secondTime - firstTime > 2000) {//如果两次按键时间间隔大于2秒，则不退出
                        Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                        firstTime = secondTime;//更新firstTime
                        return true;
                    } else {//两次按键小于2秒时，退出应用
                        AppManager.getInstance().finishActivity(this);
                    }
                }
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    //初始化视图
    private void initView() {
        noteToolbar = (Toolbar) findViewById(R.id.note_toolbar);
        noteToolbarText = (TextView) findViewById(R.id.note_toolbar_text);
        noteToolbarBack = (ImageView) findViewById(R.id.note_toolbar_back);
        noteRecyclerView = (RecyclerView) findViewById(R.id.note_recycler_view);
        noteSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.note_swipe_refresh);
        noteSwipeRefresh.setEnabled(false);//暂时关闭下拉刷新

        noteRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        noteAdapter = new note_list(NoteActivity.this, allNotes, status);
        noteRecyclerView.setAdapter(noteAdapter);

        initBar();//初始化标题栏
        //initMenuFragment();//初始化菜单碎片-上下文菜单
        invalidateOptionsMenu();//调用invalidateOptionsMenu()方法，然后系统将调用onPrepareOptionsMenu()执行update操作
        final FloatingActionsMenu menu = initFloatButton();//初始化浮动菜单钮
        noteRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                menu.collapse();
                super.onScrolled(recyclerView, dx, dy);
            }
        });

//        initSwipeRefresh();//初始化下拉菜单
        setLoadMore();//配置上拉显示更多

        programDia = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        programDia.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        programDia.setCancelable(true);
    }

    public FloatingActionsMenu initFloatButton() {
        final FloatingActionsMenu menu = (FloatingActionsMenu) findViewById(R.id.float_menu);
        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.note_fab_add_note);
        FloatingActionButton friendNote = (FloatingActionButton) findViewById(R.id.note_fab_friend_note);
        FloatingActionButton myNote = (FloatingActionButton) findViewById(R.id.note_fab_my_note);
        FloatingActionButton home = (FloatingActionButton) findViewById(R.id.note_fab_home);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoteActivity.this, FragmentActivitys.class);
                intent.putExtra(FragmentActivitys.extra_title, "写纸条");
                intent.putExtra(FragmentActivitys.extra_flag, FragmentActivitys.notewrite);
                startActivity(intent);
                menu.collapse();
            }
        });
        friendNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyApplication.userFriendLists.size() == 0) {
                    final SweetAlertDialog dialog = new SweetAlertDialog(NoteActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("小贴士").setContentText("请先添加一个已注册的好友吧:-)")
                            .setCancelText("知道了").setConfirmText("去添加");
                    dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            dialog.dismiss();
                            Intent intent = new Intent(NoteActivity.this, FragmentActivitys.class);
                            intent.putExtra(FragmentActivitys.extra_flag, FragmentActivitys.addfriend);
                            intent.putExtra(FragmentActivitys.extra_title, "添加朋友");
                            startActivity(intent);
                        }
                    });
                    dialog.show();
                } else {
                    allNotes.clear();myFriendNoteId = 0;getMyFriendNotes(myFriendNoteId);status = 0;
                    noteToolbarText.setText("好友纸条");
                    noteAdapter = new note_list(NoteActivity.this, allNotes, status);
                    noteRecyclerView.setAdapter(noteAdapter);
                }
                menu.collapse();
            }
        });
        myNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allNotes.clear();myNoteId = 0;getMyNotes(myNoteId);status = 1;
                noteToolbarText.setText("我的纸条");
                noteAdapter = new note_list(NoteActivity.this, allNotes, status);
                noteRecyclerView.setAdapter(noteAdapter);
                menu.collapse();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(NoteActivity.this, MainActivity.class);
                startActivity(homeIntent);
                menu.collapse();
                AppManager.getInstance().finishActivity(NoteActivity.this);
            }
        });
        return menu;
    }

    //获取纸条数据--我的纸条
    private void getMyNotes(final int myNoteId) {
        LogUtil.d(TAG, "当前数量：" + myNoteId);
        //分页显示用户可见的所有字条
        RequestBody body = new FormBody.Builder()
                .add("uid", MyApplication.user.getUid() + "")
                .add("noteId", myNoteId + "").build();
        HttpUtil.sendPostOkHttpRequest(HttpPathUtil.showMyAllNote(), body, true, new Callback() {
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
                if (Util.JsonUtils.isGoodJson(resp)){
                    if (myNoteId == 0) {
                        allNotes.clear();
                    }
                    myNotes = JSON.parseArray(resp, Note.class);
                    setNoteList(myNotes);
                    closeRefresh();
                    oldCount++;
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final SweetAlertDialog dialog = new SweetAlertDialog(NoteActivity.this, SweetAlertDialog.WARNING_TYPE)
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
    }

    //获取纸条数据--好友的纸条
    private void getMyFriendNotes(final int friendNoteCount) {
        LogUtil.d(TAG, "当前数量：" + friendNoteCount);
        //分页显示用户可见的所有字条
        RequestBody body = new FormBody.Builder()
                .add("uid", MyApplication.user.getUid() + "")
                .add("myuserNunber", MyApplication.user.getNumber())
                .add("noteId", friendNoteCount + "").build();
        HttpUtil.sendPostOkHttpRequest(HttpPathUtil.showAllfriNote(), body, true, new Callback() {
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
                if (Util.JsonUtils.isGoodJson(resp)){
                    if (friendNoteCount == 0) {
                        allNotes.clear();
                    }
                    myFriendNotes = JSON.parseArray(resp, Note.class);

                    //选取本次访问最新纸条，存入数据库
                    int noteId = sharedPreferences.getInt("noteFlag", 1);
                    for (Note note:myFriendNotes) {
                        if (note.getNoteId() >= noteId) {
                            noteId = note.getNoteId();
                        }
                    }
                    editor.putInt("noteFlag", noteId);
                    editor.apply();

                    setNoteList(myFriendNotes);
                    closeRefresh();
                    oldCount++;
                } else {
                    runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final SweetAlertDialog dialog = new SweetAlertDialog(NoteActivity.this, SweetAlertDialog.WARNING_TYPE)
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
    }

    //关闭进度条
    private void closeRefresh() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                noteSwipeRefresh.setRefreshing(false);
                if (programDia.isShowing()) {
                    programDia.dismiss();
                }
            }
        });
    }
}

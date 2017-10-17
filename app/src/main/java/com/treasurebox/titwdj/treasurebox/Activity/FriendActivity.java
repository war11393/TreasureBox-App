package com.treasurebox.titwdj.treasurebox.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.FriendList_Table;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.AppManager;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Utils.HttpUtil;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Utils.ProjectUtil;
import com.treasurebox.titwdj.treasurebox.Utils.Util;
import com.treasurebox.titwdj.treasurebox.Adapter.friend_info_memo;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.FriendInfo;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.FriendList;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.MemoList;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.client;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.dialog;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.maxLoadTimes;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.serversLoadTimes;

public class FriendActivity extends AppCompatActivity {
    private static final String TAG = "FriendActivity";

    ImageView mainFriendinfoImage;
    Toolbar mainFriendinfoToolbar;
    CollapsingToolbarLayout mainFriendinfoCollaptoolbar;
    AppBarLayout mainFriendinfoAppbar;
    public static RecyclerView mainFriendinfoRecycler;
    EditText mainFriendinfoAddtitle, mainFriendinfoAddcontent;
    TextView phone;
    FloatingActionButton mainFriendinfoCall, mainFriendinfoSend;
    CircleImageView mainFriendinfoHead;
    Button addConfirm, delete;

    private boolean isClick = false;

    //存放数据
    public static String fid = null;
    public static FriendInfo friendInfo = null;
    public static friend_info_memo adapter = null;

    final Intent callIntent = new Intent(Intent.ACTION_CALL);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ProjectUtil.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);
        getDataByIntent();//从Intent获取数据
        initView();//初始化视图
        setData();//配置数据和监听点击
        callIntent.setData(Uri.parse("tel:" + friendInfo.getPhone()));//给打电话intent装入好友电话号
    }

    //配置数据和监听点击
    private void setData() {
        setAlpha();//配置头像和手机号显示动画
        setDataByIntent();//装载从Intent获取的数据

        //给好友通话
        mainFriendinfoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SweetAlertDialog dialog = new SweetAlertDialog(FriendActivity.this, SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText("是否向Ta通话")
                        .setConfirmText("是")
                        .setCancelText("取消");
                dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        if (ActivityCompat.checkSelfPermission(FriendActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(FriendActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 0);
                        } else {
                            startActivity(callIntent);
                        }
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        //给好友发短信
        mainFriendinfoSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SweetAlertDialog dialog = new SweetAlertDialog(FriendActivity.this, SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText("是否向Ta发短信")
                        .setConfirmText("是")
                        .setCancelText("取消");
                dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + friendInfo.getPhone()));
                        //intent.putExtra("sms_body", "The SMS text");//这个可以设置默认文本，打开短信工具时就直接有这段话
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        //添加自定义标签项
        addConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isClick) {
                    mainFriendinfoAddtitle.setVisibility(View.VISIBLE);
                    mainFriendinfoAddcontent.setVisibility(View.VISIBLE);
                    mainFriendinfoAddtitle.setText("");
                    mainFriendinfoAddcontent.setText("");
                    addConfirm.setText("完成");
                    isClick = true;
                    return;
                }
                if (isClick) {//后期需加入各种空值判断，合法性分析
                    mainFriendinfoAddtitle.setVisibility(View.GONE);
                    mainFriendinfoAddcontent.setVisibility(View.GONE);

                    //添加好友便签
                    final String title = mainFriendinfoAddtitle.getText().toString().trim();
                    final String content = mainFriendinfoAddcontent.getText().toString().trim();
                    RequestBody body = new FormBody.Builder()
                            .add("uid", MyApplication.user.getUid() + "")
                            .add("fid", fid)
                            .add("memoName", title)
                            .add("friendContent", content).build();
                    HttpUtil.sendPostOkHttpRequest(HttpPathUtil.addMemo(), body, true, new Callback() {
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
                            String res = response.body().string();
                            LogUtil.d(TAG, res);
                            if (Util.JsonUtils.isGoodJson(res)) {
                                MemoList memo = new Gson().fromJson(res, MemoList.class);

                                final List<MemoList> items = friendInfo.getMemoList();
                                items.add(memo);
                                friendInfo.setMemoList(items);
                                friendInfo.save();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter = new friend_info_memo(items, FriendActivity.this);
                                        mainFriendinfoRecycler.setAdapter(adapter);
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            final SweetAlertDialog dialog = new SweetAlertDialog(FriendActivity.this, SweetAlertDialog.WARNING_TYPE)
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

                    addConfirm.setText("添加项");
                    isClick = false;
                    return;
                }
            }
        });

        //删除好友
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestBody body = new FormBody.Builder()
                        .add("fid", fid).build();
                LogUtil.d(TAG, fid);
                HttpUtil.sendPostOkHttpRequest(HttpPathUtil.deleteFriend(), body, true, new Callback() {
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
                        SQLite.delete(FriendList.class)
                                .where(FriendList_Table.fid.eq(Integer.parseInt(fid)));
                        AppManager.getInstance().finishActivity(FriendActivity.this);
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 &&grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(callIntent);
                } else {
                    Toast.makeText(this, "权限禁止", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void setDataByIntent() {
        Glide.with(this).load(friendInfo.getHead2()).into(mainFriendinfoImage);//图片背景
        Glide.with(this).load(friendInfo.getHead2()).into(mainFriendinfoHead);//头像数据
        phone.setText(friendInfo.getPhone());//手机号显示
        mainFriendinfoCollaptoolbar.setTitle(friendInfo.getFriendUsername());//标题显示--好友昵称
        //适配好友信息数据项
        adapter = new friend_info_memo(friendInfo.getMemoList(), this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        mainFriendinfoRecycler.setLayoutManager(layoutManager);
        mainFriendinfoRecycler.setAdapter(adapter);
    }

    //配置头像和手机号显示动画
    private void setAlpha() {
        mainFriendinfoAppbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float i = ((float)verticalOffset/mainFriendinfoAppbar.getTotalScrollRange());
                mainFriendinfoHead.setAlpha(i*i);
                phone.setAlpha(1-i*i);
                mainFriendinfoToolbar.setAlpha(i*i*i*i);
            }
        });
    }

    //从Intent获取数据
    private void getDataByIntent() {
        Intent intent = getIntent();
        String resp = intent.getStringExtra("friend");
        fid = intent.getStringExtra("fid");
        friendInfo = new Gson().fromJson(resp, FriendInfo.class);
        FriendInfo friend = new Gson().fromJson(resp, FriendInfo.class);
        friend.save();
        friendInfo.save();
    }

    //初始化视图
    private void initView() {
        mainFriendinfoImage = (ImageView) findViewById(R.id.main_friendinfo_image);
        mainFriendinfoToolbar = (Toolbar) findViewById(R.id.main_friendinfo_toolbar);
        mainFriendinfoCollaptoolbar = (CollapsingToolbarLayout) findViewById(R.id.main_friendinfo_collaptoolbar);
        mainFriendinfoAppbar = (AppBarLayout) findViewById(R.id.main_friendinfo_appbar);
        mainFriendinfoRecycler = (RecyclerView) findViewById(R.id.main_friendinfo_recycler);
        mainFriendinfoAddtitle = (EditText) findViewById(R.id.main_friendinfo_addtitle);
        mainFriendinfoAddcontent = (EditText) findViewById(R.id.main_friendinfo_addcontent);
        mainFriendinfoCall = (FloatingActionButton) findViewById(R.id.main_friendinfo_call);
        mainFriendinfoSend = (FloatingActionButton) findViewById(R.id.main_friendinfo_send);
        mainFriendinfoHead = (CircleImageView) findViewById(R.id.main_friendinfo_head);
        addConfirm = (Button) findViewById(R.id.main_friendinfo_addconfirm);
        delete = (Button) findViewById(R.id.main_friendinfo_delete);
        phone = (TextView) findViewById(R.id.main_friendinfo_phone);

        setSupportActionBar(mainFriendinfoToolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null)
            actionbar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            AppManager.getInstance().finishActivity(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

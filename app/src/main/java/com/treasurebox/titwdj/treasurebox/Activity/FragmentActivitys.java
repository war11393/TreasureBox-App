package com.treasurebox.titwdj.treasurebox.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.treasurebox.titwdj.treasurebox.Fragment.mainFragments.adviceDetail;
import com.treasurebox.titwdj.treasurebox.Fragment.mainFragments.hotDiscuss;
import com.treasurebox.titwdj.treasurebox.Fragment.mainFragments.mainAddFriend;
import com.treasurebox.titwdj.treasurebox.Fragment.noteFragments.notePermission;
import com.treasurebox.titwdj.treasurebox.Fragment.noteFragments.noteWith;
import com.treasurebox.titwdj.treasurebox.Fragment.noteFragments.noteWrite;
import com.treasurebox.titwdj.treasurebox.Fragment.userFragments.userAboutUs;
import com.treasurebox.titwdj.treasurebox.Fragment.userFragments.userCallUs;
import com.treasurebox.titwdj.treasurebox.Fragment.userFragments.userChangePass;
import com.treasurebox.titwdj.treasurebox.Fragment.userFragments.userInfoSet;
import com.treasurebox.titwdj.treasurebox.Fragment.userFragments.userPersonal;
import com.treasurebox.titwdj.treasurebox.Fragment.userFragments.userRemain;
import com.treasurebox.titwdj.treasurebox.Fragment.userFragments.userRemainWith;
import com.treasurebox.titwdj.treasurebox.Fragment.userFragments.userRobot;
import com.treasurebox.titwdj.treasurebox.Fragment.userFragments.userUpdate;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.AppManager;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Utils.ProjectUtil;
import com.treasurebox.titwdj.treasurebox.Utils.Util;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 11393 on 2017/8/14.
 * 这是一个碎片统一管理活动
 * 开启时需要传过来标题title和内容标签contentFlag
 */
public class FragmentActivitys extends BaseActivity {
    public static final String TAG = "FragmentActivitys";

    public static final String extra_title = "title";
    public static final String extra_flag = "flag";

    public static final String userinfo = "userinfo";
    public static final String userrobot = "userrobot";
    public static final String userpersonal = "userpersonal";
    public static final String userremain = "userremain";
    public static final String userremainwith = "userremainwith";
    public static final String userchangepass = "userchangepass";
    public static final String useraboutus = "useraboutus";
    public static final String userupdate = "userupdate";
    public static final String usercallus = "usercallus";
    public static final String notewrite = "notewrite";
    public static final String notepermission = "notepermission";
    public static final String noteWith = "noteWith";
    public static final String addfamily = "addfamily";
    public static final String addfriend = "addfriend";
    public static final String addlover = "addlover";
    public static final String adviceDetail = "adviceDetail";
    public static final String hot1 = "hot1";
    public static final String hot2 = "hot2";

    public static String cameraPath;

    NestedScrollView scrollview;
    FrameLayout noScroll;

    private Toolbar toolbar;
    private FragmentManager fm;
    private FragmentTransaction ft;

    private String title, contentFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ProjectUtil.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);
        scrollview = (NestedScrollView) findViewById(R.id.activity_fragments_scrollview);
        noScroll = (FrameLayout) findViewById(R.id.activity_fragments_container_no_scroll);
        setScroll();//给滚动布局设置监听器，滚动时关闭键盘

        //获取绑定数据
        Intent intent = getIntent();
        title = intent.getStringExtra(extra_title);
        contentFlag = intent.getStringExtra(extra_flag);

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();

        toolbar = initToolbar();
        setContent(contentFlag);
    }

    private void setScroll() {
        scrollview.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (contentFlag.equals(userinfo)){
                    Util.closeKeyBoard(FragmentActivitys.this);
                }
            }
        });
    }

    //根据绑定得标签数据提交碎片替换事务,以后需传递用户信息
    private void setContent(String contentFlag) {
        if (contentFlag.equals(userrobot) || contentFlag.equals(hot1) || contentFlag.equals(hot2)) {
            scrollview.setVisibility(View.GONE);
            noScroll.setVisibility(View.VISIBLE);
        } else {
            scrollview.setVisibility(View.VISIBLE);
            noScroll.setVisibility(View.GONE);
        }
        switch (contentFlag) {
            case userinfo://用户信息
                ft.replace(R.id.activity_fragments_container, new userInfoSet()).commit();
                break;
            case userrobot://机器人
                ft.replace(R.id.activity_fragments_container_no_scroll, new userRobot()).commit();
                break;
            case userpersonal://个性化
                ft.replace(R.id.activity_fragments_container, new userPersonal()).commit();
                break;
            case userremain://小闹钟
                ft.replace(R.id.activity_fragments_container, new userRemain()).commit();
                break;
            case userremainwith://小闹钟
                ft.replace(R.id.activity_fragments_container, new userRemainWith()).commit();
                break;
            case userchangepass://修改密码
                ft.replace(R.id.activity_fragments_container, new userChangePass()).commit();
                break;
            case useraboutus://关于我们
                ft.replace(R.id.activity_fragments_container, new userAboutUs()).commit();
                break;
            case userupdate://版本更新
                ft.replace(R.id.activity_fragments_container, new userUpdate()).commit();
                break;
            case usercallus://反馈
                ft.replace(R.id.activity_fragments_container, new userCallUs()).commit();
                break;
            case notewrite://写纸条
                ft.replace(R.id.activity_fragments_container, new noteWrite()).commit();
                break;
            case notepermission://纸条权限设置
                ft.replace(R.id.activity_fragments_container, new notePermission()).commit();
                break;
            case noteWith://纸条权限设置
                ft.replace(R.id.activity_fragments_container, new noteWith()).commit();
                break;
            case addlover:
                ft.replace(R.id.activity_fragments_container, mainAddFriend.newInstance(addlover)).commit();
                break;
            case addfamily:
                ft.replace(R.id.activity_fragments_container, mainAddFriend.newInstance(addfamily)).commit();
                break;
            case addfriend:
                ft.replace(R.id.activity_fragments_container, mainAddFriend.newInstance(addfriend)).commit();
                break;
            case adviceDetail:
                ft.replace(R.id.activity_fragments_container, new adviceDetail()).commit();
                break;
            case hot1:
                ft.replace(R.id.activity_fragments_container_no_scroll, hotDiscuss.newInstance(1)).commit();
                break;
            case hot2:
                ft.replace(R.id.activity_fragments_container_no_scroll, hotDiscuss.newInstance(2)).commit();
                break;
            default:
                LogUtil.d(TAG, "出现不知名标签");
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Util.CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "权限禁止，将无法拍照", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    cameraPath = Util.startCamera(this);//权限通过了就打开相机
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    AppManager.getInstance().finishActivity(this);
                }
        }
    }

    //处理活动回调结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Util.CAMERA_REQUEST_CODE:
                    if (contentFlag.equals(userinfo)) {
                        Glide.with(FragmentActivitys.this).load(cameraPath).into((CircleImageView) findViewById(R.id.compinfo_headimage));
                        userInfoSet.headImagePath = cameraPath;
                    } else if (contentFlag.equals(notewrite)) {
                        noteWrite.pics.add(cameraPath);
                    }
                    break;
                case Util.ALBUM_REQUEST_CODE:
                    if (contentFlag.equals(userinfo)) {
                        final String absolutePath = getPath(data.getData());
                        Glide.with(FragmentActivitys.this).load(absolutePath).into((CircleImageView) findViewById(R.id.compinfo_headimage));
                        userInfoSet.headImagePath = absolutePath;
                    } else if (contentFlag.equals(notewrite)) {
                        final String absolutePath = getPath(data.getData());
                        noteWrite.pics.add(absolutePath);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    //初始化标题栏
    private Toolbar initToolbar() {
        return ProjectUtil.setToolBar(this, title, R.drawable.part_back, backClick);
    }
    //处理标题栏项点击
    View.OnClickListener backClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.toolbar_image) {
                AppManager.getInstance().finishActivity(FragmentActivitys.this);
            }
        }
    };

    //转换路径
    private String getPath(Uri data) {
        try {
            return Util.getAbsolutePath(FragmentActivitys.this, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}

package com.treasurebox.titwdj.treasurebox.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.treasurebox.titwdj.treasurebox.Activity.PremainActivity.LoginActivity;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.AppManager;
import com.treasurebox.titwdj.treasurebox.Utils.ProjectUtil;
import com.treasurebox.titwdj.treasurebox.Utils.Util;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;


import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "UserActivity";

    LinearLayout robot, personnal, remain, changepass, aboutus, updateCheck, unlogin, exit, userInfo, callus;
    CircleImageView headImage;
    TextView userName, userNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ProjectUtil.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        initView();
    }

    private void initView() {
        ProjectUtil.setToolBar(this, "用户中心", R.drawable.part_back, backClick);

        robot = (LinearLayout) findViewById(R.id.user_main_menu_robot);
        personnal = (LinearLayout) findViewById(R.id.user_main_menu_personnal);
        remain = (LinearLayout) findViewById(R.id.user_main_menu_remain);
        changepass = (LinearLayout) findViewById(R.id.user_main_menu_changepass);
        aboutus = (LinearLayout) findViewById(R.id.user_main_menu_aboutus);
        updateCheck = (LinearLayout) findViewById(R.id.user_main_menu_update);
        unlogin = (LinearLayout) findViewById(R.id.user_main_menu_unlogin);
        exit = (LinearLayout) findViewById(R.id.user_main_menu_exit);
        headImage = (CircleImageView) findViewById(R.id.user_main_headimage);
        userName = (TextView) findViewById(R.id.user_main_username);
        userNumber = (TextView) findViewById(R.id.user_main_usernumber);
        userInfo = (LinearLayout) findViewById(R.id.user_main_userinfo);
        callus = (LinearLayout) findViewById(R.id.user_main_menu_callus);

        Glide.with(this).load(MyApplication.user.getUfacing2()).into(headImage);
        userName.setText(MyApplication.user.getUsername()==null?"暂无":MyApplication.user.getUsername());
        userNumber.setText(MyApplication.user.getNumber()==null?"暂未查到":MyApplication.user.getNumber());

        userInfo.setOnClickListener(this);
        robot.setOnClickListener(this);
        personnal.setOnClickListener(this);
        remain.setOnClickListener(this);
        changepass.setOnClickListener(this);
        aboutus.setOnClickListener(this);
        updateCheck.setOnClickListener(this);
        callus.setOnClickListener(this);
        unlogin.setOnClickListener(this);
        exit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(UserActivity.this, FragmentActivitys.class);
        switch (view.getId()) {
            case R.id.user_main_userinfo:
                intent.putExtra(FragmentActivitys.extra_title, "编辑信息");
                intent.putExtra(FragmentActivitys.extra_flag, FragmentActivitys.userinfo);
                startActivity(intent);break;
            case R.id.user_main_menu_robot:
                intent.putExtra(FragmentActivitys.extra_title, "机器人");
                intent.putExtra(FragmentActivitys.extra_flag, FragmentActivitys.userrobot);
                startActivity(intent);break;
            case R.id.user_main_menu_personnal:
                intent.putExtra(FragmentActivitys.extra_title, "个性化");
                intent.putExtra(FragmentActivitys.extra_flag, FragmentActivitys.userpersonal);
                startActivity(intent);break;
            case R.id.user_main_menu_remain:
                intent.putExtra(FragmentActivitys.extra_title, "小闹钟");
                intent.putExtra(FragmentActivitys.extra_flag, FragmentActivitys.userremain);
                startActivity(intent);break;
            case R.id.user_main_menu_changepass:
                intent.putExtra(FragmentActivitys.extra_title, "修改密码");
                intent.putExtra(FragmentActivitys.extra_flag, FragmentActivitys.userchangepass);
                startActivity(intent);break;
            case R.id.user_main_menu_aboutus:
                intent.putExtra(FragmentActivitys.extra_title, "关于我们");
                intent.putExtra(FragmentActivitys.extra_flag, FragmentActivitys.useraboutus);
                startActivity(intent);break;
            case R.id.user_main_menu_update:
                intent.putExtra(FragmentActivitys.extra_title, "版本检查");
                intent.putExtra(FragmentActivitys.extra_flag, FragmentActivitys.userupdate);
                startActivity(intent);break;
            case R.id.user_main_menu_callus:
                intent.putExtra(FragmentActivitys.extra_title, "反馈信息");
                intent.putExtra(FragmentActivitys.extra_flag, FragmentActivitys.usercallus);
                startActivity(intent);break;
            case R.id.user_main_menu_unlogin:
                Intent intent1 = new Intent(UserActivity.this, LoginActivity.class);
                startActivity(intent1);AppManager.getInstance().finishActivity(this);break;
            case R.id.user_main_menu_exit:AppManager.getInstance().finishActivity(this);
                System.exit(0);break;
            default:break;
        }
    }

    //点两次退出键退出
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

    //处理标题栏项点击
    View.OnClickListener backClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.toolbar_image) {
                AppManager.getInstance().finishActivity(UserActivity.this);
            }
        }
    };
}

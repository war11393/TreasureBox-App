package com.treasurebox.titwdj.treasurebox.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.treasurebox.titwdj.treasurebox.Fragment.mainFragments.mainContent;
import com.treasurebox.titwdj.treasurebox.Fragment.mainFragments.mainFriends;
import com.treasurebox.titwdj.treasurebox.Fragment.mainFragments.mainMessage;
import com.treasurebox.titwdj.treasurebox.Fragment.mainFragments.mainWater;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.AppManager;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Utils.HttpUtil;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Utils.ProjectUtil;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 主页Activity
 */
public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    private static final String Home = "Home";
    private static final String Water = "Water";
    private static final String Friends = "Friends";
    private static final String Time = "Time";
    private static final String Future = "Future";
    private static final String Message = "Message";
    private static String flag = Home;

    private FragmentManager fragmentManager;//碎片管理
    private DrawerLayout mDrawerLayout;//滑动菜单
    private LocationClient mLocationClient;//百度地图
    private FloatingActionsMenu floatMenu;

    NestedScrollView nestedScrollView;
    FrameLayout frameLayout;
    CircleImageView circleImageView;
    TextView textView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ProjectUtil.setTheme(this);
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        setContentView(R.layout.activity_main);
        nestedScrollView = (NestedScrollView) findViewById(R.id.main_container);
        frameLayout = (FrameLayout) findViewById(R.id.main_container_no_scroll);

        //开启推送业务，为设备获取通信id
        PushManager.startWork(this, PushConstants.LOGIN_TYPE_API_KEY, "luCn0f4d0zrGRxoCtX9fD6qRE3s4rl7u");

        setFirstActionBar();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_container, new mainContent()).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MyApplication.user != null) {
            textView.setText(MyApplication.user.getUsername());
            Glide.with(this).load(MyApplication.user.getUfacing2()).into(circleImageView);
        }
        permissionCheck();//检查并获取权限
    }

    //检查并获取权限：位置信息，手机状态，访存
    private void permissionCheck() {
        final List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.INTERNET);
            LogUtil.d(TAG, "permissionCheck: 网络权限未获取");
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            LogUtil.d(TAG, "permissionCheck: 位置权限未获取");
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
            LogUtil.d(TAG, "permissionCheck: 设备状态未获取");
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            LogUtil.d(TAG, "permissionCheck: 访存权限未获取");
        }
        if (!permissionList.isEmpty()) {
            LogUtil.d(TAG, "permissionCheck: 权限表不空啊！");
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("权限请求信息")
                    .setContentText("小盒需要获得主人的一些权限\n才能向主人服务哦^_^")
                    .setCancelText("退出应用")
                    .setConfirmText("确认")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
                            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    })
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            AppManager.getInstance().finishActivity(MainActivity.this);
                        }
                    }).show();
        } else {
            requestLocation();//获取位置信息-百度地图
        }
    }

    /**
     * ------------------------------------------------ 活动方法块 ----------------------------------------------------
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationClient.isStarted()) {
            mLocationClient.stop();//及时释放资源避免在后台持续费电
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

    //处理权限申请
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1://百度地图权限请求
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "权限禁止<0_0>", Toast.LENGTH_SHORT).show();
                            AppManager.getInstance().finishActivity(this);
                            return;
                        }
                        requestLocation();//获取位置信息-百度地图
                    }
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    AppManager.getInstance().finishActivity(this);
                }
                break;
            default:
                break;
        }
    }
    /** ------------------------------------------------ 活动方法块 ---------------------------------------------------- */
    /**
     * ------------------------------------------------ 位置服务方法块 ----------------------------------------------------
     */
    //发起位置服务请求
    private void requestLocation() {
        LocationClientOption option = new LocationClientOption();
        //option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);//将定位模式指定成传感器模式，只能使用GPS定位
        option.setScanSpan(600000);//10分钟更新一次
        option.setIsNeedAddress(true);//将位置信息转出为详细的地址信息
        mLocationClient.setLocOption(option);
        mLocationClient.start();//开启位置服务
    }

    //自定义位置监听器
    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            //解析并转储位置数据,暂时没有使用
            if (bdLocation.getCity() != null) {
                MyApplication.locate = new HashMap<>();
//                locate.put("纬", String.valueOf(bdLocation.getLatitude()));
//                locate.put("经", String.valueOf(bdLocation.getLongitude()));
//                locate.put("国", bdLocation.getCountry());
                MyApplication.locate.put("省", bdLocation.getProvince());
                MyApplication.locate.put("市", bdLocation.getCity());
                MyApplication.locate.put("区", bdLocation.getDistrict());
                MyApplication.locate.put("街", bdLocation.getStreet());
//                if (bdLocation.getLocType() == BDLocation.TypeGpsLocation){
//                    locate.put("定位方式", "GPS");
//                } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){
//                    locate.put("定位方式", "网络");
//                }
                LogUtil.d("位置信息", "onReceiveLocation: " + bdLocation.getProvince() + bdLocation.getCity() + bdLocation.getDistrict());
//                if (flag.equals(Home)){
//                    fragmentManager.beginTransaction().replace(R.id.main_container, mainContent.newInstance(
//                            MyApplication.locate.get("省").toString(), MyApplication.locate.get("市").toString(),
//                            MyApplication.locate.get("区").toString())).commit();
//                }
            }
        }
    }
    /** ------------------------------------------------ 位置服务方法块 ---------------------------------------------------- */
    /**
     * ------------------------------------------------ 滑动菜单方法块 ----------------------------------------------------
     */
    //在这里配置标题栏
    private void setFirstActionBar() {
        toolbar = ProjectUtil.setToolBar(this, "月光宝盒", R.drawable.slide_toolbar_btn, homeImageClick);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.main_nav_view);
        View headerView = navigationView.getHeaderView(0);
        circleImageView = (CircleImageView) headerView.findViewById(R.id.main_nav_head);
        textView = (TextView) headerView.findViewById(R.id.main_nav_username);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                startActivity(intent);
                mDrawerLayout.closeDrawers();
            }
        });
        //navigationView.setCheckedItem(R.id.main_nav_home);//设置默认选中
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                mDrawerLayout.closeDrawers();//点击后就关闭滑动菜单
                item.setChecked(false);
                item.setCheckable(false);//取消选中样式

                switch (item.getItemId()) {
                    case R.id.main_nav_home:
                        flag = Home;
                        nestedScrollView.setVisibility(View.VISIBLE);
                        frameLayout.setVisibility(View.GONE);
                        floatMenu.setVisibility(View.VISIBLE);
                        fragmentManager.beginTransaction().replace(R.id.main_container, new mainContent()).commit();
                        break;
                    case R.id.main_nav_water:
                        flag = Water;
                        nestedScrollView.setVisibility(View.GONE);
                        frameLayout.setVisibility(View.VISIBLE);
                        floatMenu.setVisibility(View.GONE);
                        fragmentManager.beginTransaction().replace(R.id.main_container_no_scroll, new mainWater()).commit();
                        break;
                    case R.id.main_nav_friends:
                        flag = Friends;
                        nestedScrollView.setVisibility(View.VISIBLE);
                        frameLayout.setVisibility(View.GONE);
                        floatMenu.setVisibility(View.GONE);
                        fragmentManager.beginTransaction().replace(R.id.main_container, new mainFriends()).commit();
                        break;
//                    case R.id.main_nav_time:
//                        flag = Time;
//                        fragmentManager.beginTransaction().replace(R.id.main_container, new mainTime()).commit();
//                        break;
//                    case R.id.main_nav_future:
//                        flag = Future;
//                        fragmentManager.beginTransaction().replace(R.id.main_container, new mainFuture()).commit();
//                        break;
                    case R.id.main_nav_message:
                        flag = Message;
                        nestedScrollView.setVisibility(View.VISIBLE);
                        frameLayout.setVisibility(View.GONE);
                        floatMenu.setVisibility(View.GONE);
                        fragmentManager.beginTransaction().replace(R.id.main_container, new mainMessage()).commit();
                        break;
                }
                return true;
            }
        });

        floatMenu = initFloatButton();
        NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.main_container);
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                floatMenu.collapse();
            }
        });
    }
    //点击自定义按钮后显示滑动菜单
    View.OnClickListener homeImageClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.toolbar_image){
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        }
    };
    /** ------------------------------------------------ 滑动菜单方法块 ---------------------------------------------------- */
    //初始化浮动按钮组
    public FloatingActionsMenu initFloatButton() {
        final FloatingActionsMenu menu = (FloatingActionsMenu) findViewById(R.id.float_menu);
        FloatingActionButton notebtn = (FloatingActionButton) findViewById(R.id.float_button_note);
        FloatingActionButton minebtn = (FloatingActionButton) findViewById(R.id.float_button_mine);
        notebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                startActivity(intent);
                menu.collapse();
            }
        });
        minebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                startActivity(intent);
                menu.collapse();
            }
        });
        return menu;
    }
}

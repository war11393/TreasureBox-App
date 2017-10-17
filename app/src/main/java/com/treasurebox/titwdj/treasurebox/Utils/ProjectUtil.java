package com.treasurebox.titwdj.treasurebox.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.treasurebox.titwdj.treasurebox.Activity.MainActivity;
import com.treasurebox.titwdj.treasurebox.Activity.NoteActivity;
import com.treasurebox.titwdj.treasurebox.Activity.UserActivity;
import com.treasurebox.titwdj.treasurebox.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by 11393 on 2017/9/22.
 */

public class ProjectUtil {
    //为活动动态设置主题
    public static void setTheme(Activity activity) {
        SharedPreferences pref = activity.getSharedPreferences("myTheme", Context.MODE_PRIVATE);
        int res = pref.getInt("tId", 1);
        switch (res) {
            case 1:activity.setTheme(R.style.BlueTheme);break;
            case 2:activity.setTheme(R.style.DarkBlueTheme);break;
            case 3:activity.setTheme(R.style.RedTheme);break;
            case 4:activity.setTheme(R.style.GreenTheme);break;
            case 5:activity.setTheme(R.style.YellowTheme);break;
            case 6:activity.setTheme(R.style.OrangeTheme);break;
            case 7:activity.setTheme(R.style.PurpleTheme);break;
            case 8:activity.setTheme(R.style.GreenPTheme);break;
            case 9:activity.setTheme(R.style.BlackTheme);break;
            default:break;
        }
    }

    //为活动动态修改主题
    public static void changeTheme(final Activity activity, final int resId, final String str) {
        final SharedPreferences.Editor edit = activity.getSharedPreferences("myTheme", Context.MODE_PRIVATE).edit();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final SweetAlertDialog dialog = new SweetAlertDialog(activity, SweetAlertDialog.NORMAL_TYPE).setTitleText("确认修改")
                        .setContentText("应用此主题：" + str).setCancelText("取消").setConfirmText("确认");
                dialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        dialog.dismiss();
                    }
                });
                dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        edit.putInt("tId", resId);
                        edit.apply();
                        dialog.dismiss();
                        AppManager.getInstance().finishAllActivity();
                        Intent intent = new Intent(activity, MainActivity.class);
                        activity.startActivity(intent);
                    }
                });
                dialog.show();
            }
        });
    }

    //给活动设置标题栏
    public static Toolbar setToolBar(AppCompatActivity activity, String title, int imageRes, View.OnClickListener clickListener) {
        AppBarLayout appBarLayout = (AppBarLayout) activity.findViewById(R.id.appbar);
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        final TextView text = (TextView) activity.findViewById(R.id.toolbar_text);
        final ImageView image = (ImageView) activity.findViewById(R.id.toolbar_image);

        text.setText(title);
        if (imageRes == R.drawable.part_trans)//如果是透明图片，说明不需要令其可用
            image.setEnabled(false);
        Glide.with(activity).load(imageRes).into(image);
        image.setOnClickListener(clickListener);

        activity.setSupportActionBar(toolbar);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float i = ((float)verticalOffset/appBarLayout.getTotalScrollRange());
                text.setAlpha(1-i*i);
                image.setAlpha(1-i*i);
            }
        });

        return toolbar;
    }
}

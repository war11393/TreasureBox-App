package com.treasurebox.titwdj.treasurebox.Custom.View;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

import com.treasurebox.titwdj.treasurebox.R;

/**
 * Created by 11393 on 2017/8/12.
 * 自定义底部弹出菜单-选择拍照或者从图库选择
 */
public class PopWindow_ChooseCall extends PopupWindow {
    private Button btn_call, btn_send, btn_cancel;
    private View mMenuView;

    public PopWindow_ChooseCall(Activity context, View.OnClickListener itemsOnClick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.part_popwindow_choose_call, null);
        btn_call = (Button) mMenuView.findViewById(R.id.call_call);
        btn_send = (Button) mMenuView.findViewById(R.id.call_sendSms);
        btn_cancel = (Button) mMenuView.findViewById(R.id.call_exit);
        btn_cancel.setOnClickListener(new View.OnClickListener() {//取消按钮
            public void onClick(View v) {
                dismiss();//销毁弹出框
            }
        });
        //设置按钮监听
        btn_send.setOnClickListener(itemsOnClick);
        btn_call.setOnClickListener(itemsOnClick);

        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);

        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(CoordinatorLayout.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setAnimationStyle(R.style.AnimBottom);//设置SelectPicPopupWindow弹出窗体动画效果
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));//设置SelectPicPopupWindow弹出窗体的背景全透明以使view视图完整展示

        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int height = mMenuView.findViewById(R.id.part_camera_choose_menu_layout).getTop();
                int y=(int) event.getY();
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(y<height){
                        dismiss();
                    }
                }
                return true;
            }
        });
    }
}

package com.treasurebox.titwdj.treasurebox.Fragment.mainFragments.homeLayouts;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flavienlaurent.discrollview.lib.Discrollvable;
import com.treasurebox.titwdj.treasurebox.R;

/**
 * 实现由屏幕两侧滑入视图,中心显示圆圈的视差效果
 */
public class discSubLayout extends LinearLayout implements Discrollvable {

    private TextView textView1, textView2;
    private ImageView imageView;

    private float textTranslationX;
    private float btnTranslationX;

    public discSubLayout(Context context) {
        super(context);
    }

    public discSubLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public discSubLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        textView1 = (TextView) findViewById(R.id.main_home_sub_left);
        textTranslationX = textView1.getTranslationX();
        textView2 = (TextView) findViewById(R.id.main_home_sub_right);
        btnTranslationX = textView2.getTranslationX();

        imageView = (ImageView) findViewById(R.id.main_home_sub_sub);
    }

    @Override
    public void onResetDiscrollve() {
        textView1.setAlpha(0);
        textView2.setAlpha(0);
        textView1.setTranslationX(textTranslationX);
        textView2.setTranslationX(btnTranslationX);
    }

    @Override
    public void onDiscrollve(float ratio) {//左右移的动画效果
        if(ratio <= 0.5f) {
            textView1.setTranslationX(0);
            textView2.setTranslationX(0);
            float rratio1 = ratio / 0.5f;
            textView1.setTranslationX(textTranslationX * (1 - rratio1));
            textView2.setTranslationX(btnTranslationX * (1 - rratio1));
        } else {
            textView1.setTranslationX(0);
            textView2.setTranslationX(0);
        }

        textView1.setAlpha(ratio);
        textView2.setAlpha(ratio);

        float rratio = (ratio - 0.65f) / 0.35f;
        rratio = Math.min(rratio, 1.0f);
        imageView.setAlpha(1 * rratio);
        imageView.setScaleX(1.0f * rratio);
        imageView.setScaleY(1.0f * rratio);
    }
}

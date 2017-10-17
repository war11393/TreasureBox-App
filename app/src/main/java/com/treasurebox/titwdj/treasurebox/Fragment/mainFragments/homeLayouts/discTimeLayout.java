package com.treasurebox.titwdj.treasurebox.Fragment.mainFragments.homeLayouts;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flavienlaurent.discrollview.lib.Discrollvable;
import com.treasurebox.titwdj.treasurebox.R;

/**
 * 实现由屏幕两侧滑入视图的视差效果
 */
public class discTimeLayout extends LinearLayout implements Discrollvable {

    private TextView textView;
    private TextView button;

    private float textTranslationX;
    private float btnTranslationX;

    public discTimeLayout(Context context) {
        super(context);
    }

    public discTimeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public discTimeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

//        textView = (TextView) findViewById(R.id.main_home_time_text);
//        textTranslationX = textView.getTranslationX();
//        button = (TextView) findViewById(R.id.main_home_time_btn);
//        button.setText(Html.fromHtml("时光轴......"));
        btnTranslationX = button.getTranslationX();
    }

    @Override
    public void onResetDiscrollve() {
        textView.setAlpha(0);
        button.setAlpha(0);
        textView.setTranslationX(textTranslationX);
        button.setTranslationX(btnTranslationX);
    }

    @Override
    public void onDiscrollve(float ratio) {//左右移的动画效果z
        if(ratio <= 0.5f) {
            button.setTranslationX(0);
            button.setAlpha(0.0f);
            float rratio = ratio / 0.5f;
            textView.setTranslationX(textTranslationX * (1 - rratio));
            textView.setAlpha(rratio);
        } else {
            textView.setTranslationX(0);
            textView.setAlpha(1.0f);
            float rratio = (ratio - 0.5f) / 0.5f;
            button.setTranslationX(btnTranslationX * (1 - rratio));
            button.setAlpha(rratio);
        }
    }
}

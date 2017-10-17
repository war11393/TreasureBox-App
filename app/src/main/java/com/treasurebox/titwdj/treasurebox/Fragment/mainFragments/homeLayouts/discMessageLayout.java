package com.treasurebox.titwdj.treasurebox.Fragment.mainFragments.homeLayouts;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flavienlaurent.discrollview.lib.Discrollvable;
import com.treasurebox.titwdj.treasurebox.R;

/**
 * 颜色渐变效果
 */
public class discMessageLayout extends LinearLayout implements Discrollvable {

    private LinearLayout linearLayout;
    private TextView textView1,textView2;
    private float mGreenView1TranslationY;
    private int mBlackColor = 0xff6f6f6f;
    private int mWhiteColor = Color.WHITE;
    private ArgbEvaluator mArgbEvaluator = new ArgbEvaluator();//颜色渐变器

    //继承来的构造方法
    public discMessageLayout(Context context) {
        super(context);
    }

    public discMessageLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public discMessageLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        linearLayout = (LinearLayout) findViewById(R.id.main_home_message_total);
//        textView1 = (TextView) findViewById(R.id.main_home_message_text);
//        textView2 = (TextView) findViewById(R.id.main_home_message_btn);
        textView2.setText(Html.fromHtml("消息......"));
        textView1.setAlpha(0);
        textView2.setAlpha(0);
        mGreenView1TranslationY = linearLayout.getTranslationY();
    }

    @Override
    public void onResetDiscrollve() {
        linearLayout.setTranslationY(mGreenView1TranslationY);
        textView1.setTextColor(mBlackColor);
        textView2.setTextColor(mBlackColor);
        //setBackgroundColor(mGreenColor);
    }

    @Override
    public void onDiscrollve(float ratio) {
        linearLayout.setTranslationY(mGreenView1TranslationY * (1.1f - ratio));
//        if(ratio >= 0.5f) {
//            ratio = (ratio - 0.5f) / 0.5f;
//            textView1.setTextColor((Integer) mArgbEvaluator.evaluate(ratio, mWhiteColor, mBlackColor));
//            textView2.setTextColor((Integer) mArgbEvaluator.evaluate(ratio, mWhiteColor, mBlackColor));
//            //setBackgroundColor((Integer) mArgbEvaluator.evaluate(ratio, mGreenColor, mBlueColor));
//        } else {
//            textView1.setTextColor(0xffffffff);
//            textView2.setTextColor(0xffffffff);
//        }
        textView1.setAlpha(ratio);
        textView2.setAlpha(ratio);
    }
}

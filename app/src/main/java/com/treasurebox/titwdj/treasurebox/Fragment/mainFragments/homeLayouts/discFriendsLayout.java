package com.treasurebox.titwdj.treasurebox.Fragment.mainFragments.homeLayouts;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flavienlaurent.discrollview.lib.Discrollvable;
import com.treasurebox.titwdj.treasurebox.R;

/**
 *
 */
public class discFriendsLayout extends FrameLayout implements Discrollvable {

    private LinearLayout mRedView2;

    public discFriendsLayout(Context context) {
        super(context);
    }

    public discFriendsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public discFriendsLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

//        mRedView2 = (LinearLayout) findViewById(R.id.main_home_friends_total);
//        ((TextView) findViewById(R.id.main_home_friends_btn)).setText(Html.fromHtml("亲友列表......"));
    }

    @Override
    public void onResetDiscrollve() {

    }

    @Override
    public void onDiscrollve(float ratio) {
        float rratio = (ratio - 0.65f) / 0.35f;
        rratio = Math.min(rratio, 1.0f);
        mRedView2.setAlpha(1 * rratio);
        mRedView2.setScaleX(1.0f * rratio);
        mRedView2.setScaleY(1.0f * rratio);
    }
}

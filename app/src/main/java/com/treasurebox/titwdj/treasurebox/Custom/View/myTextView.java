package com.treasurebox.titwdj.treasurebox.Custom.View;

import android.content.Context;
import android.view.Gravity;

/**
 * Created by 11393 on 2017/8/14.
 * 想要弄一个自定义控件，但是不会，有空学！
 */

public class myTextView extends android.support.v7.widget.AppCompatTextView {
    public myTextView(Context context) {
        super(context);

        setTextSize(20);
        setTextColor(0x808080);
        setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
    }
}

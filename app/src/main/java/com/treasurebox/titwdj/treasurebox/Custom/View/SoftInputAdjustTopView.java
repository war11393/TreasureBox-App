package com.treasurebox.titwdj.treasurebox.Custom.View;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.FrameLayout;

/**
 * Created by 11393 on 2017/9/6.
 * 有编辑框的界面，在manifest里及时配置了adjustResize，键盘弹出后，也不会把界面上推，会导致键盘会覆盖到编辑框（如果应用的编辑框距离底部比较近），体验很不好。
 * 这个时候，需要的是在这个界面里加入自己定义的view，同时去override fitSystemWindows方法以及omApplyWindowInsets方法
 */

public class SoftInputAdjustTopView extends FrameLayout {
    private int[] mInsets = new int[4];

    public SoftInputAdjustTopView(Context context) {
        super(context);
    }

    public SoftInputAdjustTopView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SoftInputAdjustTopView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected final boolean fitSystemWindows(Rect insets) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mInsets[0] = insets.left;
            mInsets[1] = insets.top;
            mInsets[2] = insets.right;
            insets.left = 0;
            insets.top = 0;
            insets.right = 0;
        }
        return super.fitSystemWindows(insets);
    }

    @Override
    public final WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mInsets[0] = insets.getSystemWindowInsetLeft();
            mInsets[1] = insets.getSystemWindowInsetTop();
            mInsets[2] = insets.getSystemWindowInsetRight();
            return super.onApplyWindowInsets(insets.replaceSystemWindowInsets(0, 0, 0,
                    insets.getSystemWindowInsetBottom()));
        } else {
            return insets;
        }
    }
}

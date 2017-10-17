package com.treasurebox.titwdj.treasurebox.Custom;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by 11393 on 2017/9/15.
 * 加载更多功能
 * RecyclerView 有一个方法 addOnScrollListener ，我们只要传入一个RecyclerView.OnScrollListener 就可以实现加载更多了，但是事实是为了充分保证
 * RecyclerView 的灵活性，Android 本身是没有对这个滑动接口做处理的，需要我们自定义个加载更多的接口去实现它，然后才能真正实现加载更多。
 * 实现起来也很简单，我们只要重写 onScrolled 方法即可。下面是一个封装好的加载更多的接口实现类，然后作为参数传进去就好了。
 */

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener{
    private int previousTotal = 0;
    private boolean loading = true;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int currentPage = 1;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(
            LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading
                && (totalItemCount - visibleItemCount) <= firstVisibleItem) {
            currentPage++;
            onLoadMore(currentPage);
            loading = true;
        }
    }

    public abstract void onLoadMore(int currentPage);
}

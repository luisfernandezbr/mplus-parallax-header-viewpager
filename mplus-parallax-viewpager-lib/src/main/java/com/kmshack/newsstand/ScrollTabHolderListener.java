package com.kmshack.newsstand;

import android.support.v4.app.Fragment;
import android.widget.AbsListView;

public interface ScrollTabHolderListener {

    void setScrollTabHolder(ScrollTabHolderListener scrollTabHolder);

	void adjustScroll(int scrollHeight);

	void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition);

    Fragment getFragment();
}

package com.kmshack.newsstand;

import android.support.v4.app.Fragment;
import android.widget.AbsListView;

public abstract class ScrollTabHolderFragment extends Fragment implements ScrollTabHolderListener {

	protected ScrollTabHolderListener mScrollTabHolder;

	public void setScrollTabHolder(ScrollTabHolderListener scrollTabHolder) {
		mScrollTabHolder = scrollTabHolder;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
		// nothing
	}

}
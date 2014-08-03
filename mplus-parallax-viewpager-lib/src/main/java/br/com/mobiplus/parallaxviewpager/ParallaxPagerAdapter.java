package br.com.mobiplus.parallaxviewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.SparseArrayCompat;

import com.kmshack.newsstand.ScrollTabHolderFragment;
import com.kmshack.newsstand.ScrollTabHolderListener;

/**
 * Created by luisfernandez on 8/2/14.
 */
public abstract class ParallaxPagerAdapter extends FragmentPagerAdapter {

    private SparseArrayCompat<ScrollTabHolderListener> mScrollTabHolders;

    private ScrollTabHolderListener mListener;

    public ParallaxPagerAdapter(FragmentManager fm) {
        super(fm);
        mScrollTabHolders = new SparseArrayCompat<ScrollTabHolderListener>();
    }

    @Override
    public abstract CharSequence getPageTitle(int position);

    @Override
    public abstract int getCount();

    public abstract ScrollTabHolderListener getFragment(int position);

    @Override
    public Fragment getItem(int position) {
        ScrollTabHolderListener fragment = getFragment(position);


        mScrollTabHolders.put(position, fragment);
        if (mListener != null) {
            fragment.setScrollTabHolder(mListener);
        }

        return fragment.getFragment();
    }

    public SparseArrayCompat<ScrollTabHolderListener> getScrollTabHolders() {
        return mScrollTabHolders;
    }

    public void setTabHolderScrollingContent(ScrollTabHolderListener listener) {
        mListener = listener;
    }
}

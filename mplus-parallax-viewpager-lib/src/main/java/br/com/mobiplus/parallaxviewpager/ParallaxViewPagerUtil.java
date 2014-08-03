package br.com.mobiplus.parallaxviewpager;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.astuetz.PagerSlidingTabStrip;
import com.flavienlaurent.notboringactionbar.AlphaForegroundColorSpan;
import com.flavienlaurent.notboringactionbar.KenBurnsSupportView;
import com.kmshack.newsstand.ScrollTabHolderListener;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by luisfernandez on 8/2/14.
 */
public class ParallaxViewPagerUtil {
    private static AccelerateDecelerateInterpolator sSmoothInterpolator = new AccelerateDecelerateInterpolator();
    private TypedValue mTypedValue = new TypedValue();

    private RectF mRect1 = new RectF();
    private RectF mRect2 = new RectF();

    ActionBarActivity mActivity;
    ActionBar mActionBar;

    private int mActionBarHeight;
    private int mMinHeaderHeight;
    private int mHeaderHeight;
    private int mMinHeaderTranslation;

    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager mViewPager;
    private ParallaxPagerAdapter mPagerAdapter;

    private ImageView mHeaderLogo;
    private View mHeader;

    private KenBurnsSupportView mHeaderPicture;

    private SpannableString mSpannableString;
    private AlphaForegroundColorSpan mAlphaForegroundColorSpan;

    private ScrollTabHolderListener mScrollTabHolder;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;

    public void setAlphaForegroundColorSpan(int foregroundColorSpan) {
        this.mAlphaForegroundColorSpan = new AlphaForegroundColorSpan(foregroundColorSpan);
    }

    public void setSpannableString(int resId) {
        this.mSpannableString = new SpannableString(mActivity.getString(resId));
    }

    public ParallaxViewPagerUtil(ActionBarActivity actionBarActivity) {
        this.mActivity = actionBarActivity;
        this.mActionBar = actionBarActivity.getSupportActionBar();

        mMinHeaderHeight = getResources().getDimensionPixelSize(R.dimen.mpluspvp_min_header_height);
        mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.mpluspvp_header_height);
        mMinHeaderTranslation = -mMinHeaderHeight + this.getActionBarHeight();

        mScrollTabHolder = new ScrollTabHolderListener() {
            @Override
            public void adjustScroll(int scrollHeight) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
                onPageScroll(view, firstVisibleItem, visibleItemCount, totalItemCount, pagePosition);
            }
        };

        mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                SparseArrayCompat<ScrollTabHolderListener> scrollTabHolders = mPagerAdapter.getScrollTabHolders();
                ScrollTabHolderListener currentHolder = scrollTabHolders.valueAt(position);

                currentHolder.adjustScroll((int) (mHeader.getHeight() + ViewHelper.getTranslationY(mHeader)));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
    }

    public void init(ParallaxPagerAdapter pagerAdapter) {
        mHeaderLogo = (ImageView) findViewById(R.id.mpluspvp_header_logo);
        mHeader = findViewById(R.id.mpluspvp_frameLayoutHeader);

        this.initPagerSlidingTabStrip(pagerAdapter, mScrollTabHolder, mOnPageChangeListener);

        ViewHelper.setAlpha(getActionBarIconView(), 0f);
        mActionBar.setBackgroundDrawable(null);
    }


    public void setTitleAlpha(float alpha) {
        mAlphaForegroundColorSpan.setAlpha(alpha);
        mSpannableString.setSpan(mAlphaForegroundColorSpan, 0, mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mActivity.getSupportActionBar().setTitle(mSpannableString);
    }

    public void interpolate() {
        float ratio = getRatio();
        interpolate(mHeaderLogo, getActionBarIconView(), sSmoothInterpolator.getInterpolation(ratio));
    }

    public float getRatio() {
        return clamp(ViewHelper.getTranslationY(mHeader) / mMinHeaderTranslation, 0.0f, 1.0f);
    }

    public void onPageScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
        if (mViewPager.getCurrentItem() == pagePosition) {
            int scrollY = getScrollY(view);
            ViewHelper.setTranslationY(mHeader, Math.max(-scrollY, mMinHeaderTranslation));
            interpolate();
            setTitleAlpha(clamp(5.0F * getRatio() - 4.0F, 0.0F, 1.0F));
        }
    }

    public int getScrollY(AbsListView view) {
        View c = view.getChildAt(0);
        if (c == null) {
            return 0;
        }

        int firstVisiblePosition = view.getFirstVisiblePosition();
        int top = c.getTop();

        int headerHeight = 0;
        if (firstVisiblePosition >= 1) {
            headerHeight = mHeaderHeight;
        }

        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }

    private void interpolate(View view1, View view2, float interpolation) {
        getOnScreenRect(mRect1, view1);
        getOnScreenRect(mRect2, view2);

        float scaleX = 1.0F + interpolation * (mRect2.width() / mRect1.width() - 1.0F);
        float scaleY = 1.0F + interpolation * (mRect2.height() / mRect1.height() - 1.0F);
        float translationX = 0.5F * (interpolation * (mRect2.left + mRect2.right - mRect1.left - mRect1.right));
        float translationY = 0.5F * (interpolation * (mRect2.top + mRect2.bottom - mRect1.top - mRect1.bottom));

        ViewHelper.setTranslationX(view1, translationX);
        ViewHelper.setTranslationY(view1, translationY - ViewHelper.getTranslationY(mHeader));
        ViewHelper.setScaleX(view1, scaleX);
        ViewHelper.setScaleY(view1, scaleY);
    }

    private RectF getOnScreenRect(android.graphics.RectF rect, View view) {
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        return rect;
    }

    private float clamp(float value, float max, float min) {
        return Math.max(Math.min(value, min), max);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ImageView getActionBarIconView() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return (ImageView) findViewById(android.R.id.home);
        }

        return null;
        //return (ImageView) findViewById(android.support.v7.appcompat.R.id.home);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public int getActionBarHeight() {
        if (mActionBarHeight != 0) {
            return mActionBarHeight;
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            getTheme().resolveAttribute(android.R.attr.actionBarSize, mTypedValue, true);
        } else {
            getTheme().resolveAttribute(R.attr.actionBarSize, mTypedValue, true);
        }

        mActionBarHeight = TypedValue.complexToDimensionPixelSize(mTypedValue.data, getResources().getDisplayMetrics());

        return mActionBarHeight;
    }

    private Resources getResources() {
        return mActivity.getResources();
    }

    private Resources.Theme getTheme() {
        return mActivity.getTheme();
    }

    public void setHeaderImages(int... resId) {
        mHeaderPicture = (KenBurnsSupportView) findViewById(R.id.mpluspvp_header_picture);
        mHeaderPicture.setResourceIds(resId);
    }

    private View findViewById(int resId) {
        return mActivity.findViewById(resId);
    }

    public void initPagerSlidingTabStrip(ParallaxPagerAdapter pagerAdapter, ScrollTabHolderListener sth, ViewPager.OnPageChangeListener listener) {
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.mpluspvp_tabs);
        mViewPager = (ViewPager) findViewById(R.id.mpluspvp_pager);
        mViewPager.setOffscreenPageLimit(4);

        mPagerAdapter = pagerAdapter;

//        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mPagerAdapter.setTabHolderScrollingContent(sth);




        mViewPager.setAdapter(mPagerAdapter);

        mPagerSlidingTabStrip.setViewPager(mViewPager);
        mPagerSlidingTabStrip.setOnPageChangeListener(listener);
    }

    private FragmentManager getSupportFragmentManager() {
        return mActivity.getSupportFragmentManager();
    }

//    public class PagerAdapter extends FragmentPagerAdapter {
//
//        private SparseArrayCompat<ScrollTabHolder> mScrollTabHolders;
//        private final String[] TITLES = {"Capitais", "Bandeiras", "Relevo e Clima", "Países"};
//        private ScrollTabHolder mListener;
//
//        public PagerAdapter(FragmentManager fm) {
//            super(fm);
//            mScrollTabHolders = new SparseArrayCompat<ScrollTabHolder>();
//        }
//
//        public void setTabHolderScrollingContent(ScrollTabHolder listener) {
//            mListener = listener;
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return TITLES[position];
//        }
//
//        @Override
//        public int getCount() {
//            return TITLES.length;
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            ScrollTabHolderFragment fragment = (ScrollTabHolderFragment) SampleListFragment.newInstance(position);
//
//            mScrollTabHolders.put(position, fragment);
//            if (mListener != null) {
//                fragment.setScrollTabHolder(mListener);
//            }
//
//            return fragment;
//        }
//
//        public SparseArrayCompat<ScrollTabHolder> getScrollTabHolders() {
//            return mScrollTabHolders;
//        }
//
//    }
}

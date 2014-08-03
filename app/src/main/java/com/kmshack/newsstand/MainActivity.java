package com.kmshack.newsstand;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

import br.com.mobiplus.parallaxviewpager.ParallaxPagerAdapter;
import br.com.mobiplus.parallaxviewpager.ParallaxViewPagerUtil;
import br.com.mobiplus.parallaxviewpager.SampleListFragment;

public class MainActivity extends ActionBarActivity {

    private ParallaxViewPagerUtil parallaxViewPagerUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parallaxViewPagerUtil = new ParallaxViewPagerUtil(this);

        setContentView(R.layout.activity_main);

        MyAdapter mPagerAdapter = new MyAdapter(getSupportFragmentManager());
        parallaxViewPagerUtil.init(mPagerAdapter);

        parallaxViewPagerUtil.setHeaderImages(R.drawable.pic3, R.drawable.pic4);
        parallaxViewPagerUtil.setSpannableString(R.string.actionbar_title);
        parallaxViewPagerUtil.setAlphaForegroundColorSpan(0xffffffff);
    }

    private class MyAdapter extends ParallaxPagerAdapter {

        private final String[] TITLES = {"Capitais", "Bandeiras", "Relevo e Clima", "Países"};

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public ScrollTabHolderFragment getFragment(int position) {
            return (ScrollTabHolderFragment) SampleListFragment.newInstance(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }
    }
}

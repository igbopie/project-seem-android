package com.seem.android.mockup1.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.seem.android.mockup1.MyApplication;
import com.seem.android.mockup1.R;
import com.seem.android.mockup1.util.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by igbopie on 23/04/14.
 */
public class HomeFragment extends Fragment {
    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    private TabsPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        mPagerAdapter = new TabsPagerAdapter(getChildFragmentManager());
        mViewPager = (ViewPager) getActivity().findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) getActivity().findViewById(R.id.tabs);
        tabs.setViewPager(mViewPager);
        tabs.setIndicatorColorResource(R.color.SeemYellow);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment f = mPagerAdapter.getItem(mViewPager.getCurrentItem());
        return f.onOptionsItemSelected(item);
    }


    // Since this is an object collection, use a FragmentStatePagerAdapter,
    // and NOT a FragmentPagerAdapter.
    public class TabsPagerAdapter extends FragmentStatePagerAdapter {

        private Map<Integer,Fragment> mPageReferenceMap = new HashMap<Integer, Fragment>();

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = mPageReferenceMap.get(i);
            if(fragment != null){
                return fragment;
            }
            if(i == 0){
                fragment = new FeedListFragment();
            } else {
                fragment = new SeemListFragment();
            }

            mPageReferenceMap.put(i, fragment);

            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 0){
                return "Timeline";
            }else{
                return "Recent Seems";
            }

        }

        public void destroyItem(View container, int position, Object object) {
            super.destroyItem(container, position, object);
            mPageReferenceMap.remove(position);

            Utils.debug(getClass(), "Pager: destroyItem:"+position);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Utils.debug(getClass(),"onSaveInstanceState");
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.debug(getClass(),"onResume");
        mViewPager.setCurrentItem(mViewPager.getCurrentItem());
    }
    @Override
    public void onPause() {
        super.onPause();
        Utils.debug(getClass(),"onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.debug(getClass(),"onStop");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Utils.debug(getClass(), "OnDetach");
    }



}



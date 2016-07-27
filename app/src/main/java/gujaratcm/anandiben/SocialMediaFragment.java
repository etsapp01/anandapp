package gujaratcm.anandiben;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import gujaratcm.anandiben.common.CustomViewPager;
import gujaratcm.anandiben.customui.SlidingTabLayout;

/**
 * Created by ACER on 23-07-2016.
 */
public class SocialMediaFragment extends Fragment {


    SlidingTabLayout tabs;
    CustomViewPager pager;
    //    MaterialSearchView searchView;
    ListView listContainer;


    public static SocialMediaFragment getInstance() {
        SocialMediaFragment fragment = new SocialMediaFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.interview_main_fragment, container, false);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        FragmentManager frgManager = getFragmentManager();
                        Fragment fragment = new MainFragment();
                        frgManager
                                .beginTransaction()
                                .replace(R.id.frame_container, fragment).commit();
                        return true;
                    }
                }
                return false;
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pager = (CustomViewPager) view.findViewById(R.id.pager);
        tabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
        getActivity().invalidateOptionsMenu();


        pager.setOffscreenPageLimit(4);
        pager.setAdapter(new MyInnerPagerAdapter(getChildFragmentManager()));
        tabs.setDistributeEvenly(true);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.parseColor("#14C8DF");
            }
        });
        tabs.setBackgroundColor(Color.parseColor("#2E68B1"));
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        pager.setPagingEnabled(false);
        tabs.setViewPager(pager);


    }

    public interface selectedfragment {
        void fragmentBecameVisible();
    }

    class MyInnerPagerAdapter extends FragmentPagerAdapter {

        String[] tabs = getResources().getStringArray(R.array.tab_social);

        public MyInnerPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    TwitterBuzzFragment frag = TwitterBuzzFragment.getInstance();
                    return frag;
                case 1:
                    FacebookBuzzFragment pop = FacebookBuzzFragment.getInstance();
                    return pop;
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

        @Override
        public int getCount() {
            return tabs.length;
        }
    }

}
package com.swufe.buffermergetool.ui.gallery;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class GalleryPageAdapter extends FragmentPagerAdapter {

    private final String[] titles=new String[]{"Recruit","Needs","Internship"};

    public GalleryPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){
            return new RecruitFragment();
        }else if (position==1){
            return new NeedsFragment();
        }else {
            return new InternshipFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}

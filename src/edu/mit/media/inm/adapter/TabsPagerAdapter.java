package edu.mit.media.inm.adapter;

import edu.mit.media.inm.MajorFragment;
import edu.mit.media.inm.status.*;
import edu.mit.media.inm.story.*;

import android.app.ListFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
 
public class TabsPagerAdapter extends FragmentPagerAdapter {
 
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
 
    public MajorFragment getItem(int index) {
 
        switch (index) {
        case 0:
            return new StatusFragment();
        case 1:
            return new FeedFragment();
        case 2:
            return new UpdateFragment();
        case 3:
            return new TellFragment();
        }
        return null;
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 4;
    }
 
}
package marcel.com.stormy.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

import marcel.com.stormy.R;
import marcel.com.stormy.ui.HumidityFragment;
import marcel.com.stormy.ui.TemperatureFragment;
import marcel.com.stormy.ui.WindFragment;
import marcel.com.stormy.weather.Hour;

/**
 * Created by Marcel on 7/20/2015.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    protected Context mContext;
    protected Hour[] mHours;
    private TemperatureFragment mTemperatureFragment;
    private HumidityFragment mHumidityFragment;
    private WindFragment mWindFragment;
    public SectionsPagerAdapter(Context context,FragmentManager fm) {
        super(fm);
        mContext=context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        switch (position){

            case 0:
                return new TemperatureFragment();

            case 1:

                return new HumidityFragment();

            case 2:
                return new WindFragment();

        }
        return null;

    }

    @Override
    public int getCount() {
        //Number of tabs
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return mContext.getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return mContext.getString(R.string.title_section2).toUpperCase(l);
            case 2:
                return mContext.getString(R.string.title_section33).toUpperCase(l);
            default:
                break;
        }
        return null;
    }
}

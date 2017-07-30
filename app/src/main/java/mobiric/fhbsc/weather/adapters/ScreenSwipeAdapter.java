package mobiric.fhbsc.weather.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import mobiric.fhbsc.weather.fragments.BarometerFragment;
import mobiric.fhbsc.weather.fragments.RainFragment;
import mobiric.fhbsc.weather.fragments.TemperatureFragment;
import mobiric.fhbsc.weather.fragments.WindFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the
 * sections/tabs/pages.
 */
public class ScreenSwipeAdapter extends FragmentPagerAdapter {
    Context context;

    public ScreenSwipeAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        // TODO don't create a new fragment each time this is called
        switch (position) {
            case 0: {
                return new WindFragment();
            }
            case 1: {
                return new TemperatureFragment();
            }
            case 2: {
                return new BarometerFragment();
            }
            case 3: {
                return new RainFragment();
            }
            default: {
                return new WindFragment();
            }
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Wind";
            case 1:
                return "Temperature";
            case 2:
                return "Barometer";
            case 3:
                return "Rain";
        }
        return null;
    }
}

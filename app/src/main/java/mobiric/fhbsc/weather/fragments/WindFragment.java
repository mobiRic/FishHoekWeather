package mobiric.fhbsc.weather.fragments;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

import lib.debug.Dbug;
import mobiric.fhbsc.weather.R;
import mobiric.fhbsc.weather.WeatherApp;
import mobiric.fhbsc.weather.intents.IntentConstants.Actions;
import mobiric.fhbsc.weather.intents.IntentConstants.Extras;
import mobiric.fhbsc.weather.model.WeatherReading;


/**
 * Fragment that displays the wind data.
 */
public class WindFragment extends ARefreshableFragment {
    public static final String[] DIRECTIONS =
            {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW",
                    "NW", "NNW", "N",};

    public static final String GRAPH_DAY_WIND = "daywind.png";
    public static final String GRAPH_DAY_WIND_DIR = "daywinddir.png";
    public static final String GRAPH_WEEK_WIND = "weekwind.png";
    public static final String GRAPH_WEEK_WIND_DIR = "weekwinddir.png";

    TextView tvWindSpeed;
    TextView tvWindDir;
    // TextView tvWindGustSpeed;
    // TextView tvWindGustDir;
    ImageView ivDayWind;
    ImageView ivDayWindDir;
    ImageView ivWeekWind;
    ImageView ivWeekWindDir;
    ImageView ivArrowWindDir;

    int windDirDegrees = 180;
    int oldWindDirDegrees = 180;

    public WindFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View rootView = inflater.inflate(R.layout.fragment_wind, container, false);
        tvWindSpeed = (TextView) rootView.findViewById(R.id.tvWindSpeed);
        tvWindDir = (TextView) rootView.findViewById(R.id.tvWindDir);
        // tvWindGustSpeed = (TextView) rootView.findViewById(R.id.tvWindGustSpeed);
        // tvWindGustDir = (TextView) rootView.findViewById(R.id.tvWindGustDir);

        ivDayWind = (ImageView) rootView.findViewById(R.id.ivDayWind);
        zoomIfApi14(ivDayWind, rootView, GRAPH_DAY_WIND);

        ivDayWindDir = (ImageView) rootView.findViewById(R.id.ivDayWindDir);
        zoomIfApi14(ivDayWindDir, rootView, GRAPH_DAY_WIND_DIR);

        ivWeekWind = (ImageView) rootView.findViewById(R.id.ivWeekWind);
        zoomIfApi14(ivWeekWind, rootView, GRAPH_WEEK_WIND);

        ivWeekWindDir = (ImageView) rootView.findViewById(R.id.ivWeekWindDir);
        zoomIfApi14(ivWeekWindDir, rootView, GRAPH_WEEK_WIND_DIR);

        ivArrowWindDir = (ImageView) rootView.findViewById(R.id.ivArrowWindDir);

        return rootView;
    }

    /**
     * Updates the data displayed by this fragment. Called when data is refreshed, or when fragment
     * is created or resumed. </p>
     * <p>
     * Data is fetched from the {@link WeatherApp} instance. Updating via {@link Intent} extras does
     * not allow fragments to receive the update when paused.
     *
     * @param animate <code>true</code> to animate the changes (on a refresh); <code>false</code>
     *                otherwise (on resume)
     */
    void updateData(boolean animate) {
        // get data from the application cache
        WeatherReading reading = myApp.getCachedWeatherReading();

        String windSpeed = reading.windSpeed;
        tvWindSpeed.setText(windSpeed);

        // String windGustSpeed = reading.windGust;
        // tvWindGustSpeed.setText(windGustSpeed);
        //
        // String windGustDir = reading.windGustDir;
        // tvWindGustDir.setText(windGustDir);

        if (reading.windDir != null) {
            setWindDirDegrees(reading.windDir);
            rotateArrow(animate);
        }

        String windDir = reading.windDir + " (" + getCardinal(windDirDegrees) + ")";
        tvWindDir.setText(windDir);
    }

    String getCardinal(double degrees) {
        return DIRECTIONS[(int) Math.round((((double) degrees % 360) / 22.5))];
    }

    boolean numberInRange(int number, float min, float max) {
        return ((min <= number) && (number <= max));
    }

    void initImages() {
        updateImage(ivDayWind, GRAPH_DAY_WIND);
        updateImage(ivDayWindDir, GRAPH_DAY_WIND_DIR);
        updateImage(ivWeekWind, GRAPH_WEEK_WIND);
        updateImage(ivWeekWindDir, GRAPH_WEEK_WIND_DIR);
    }

    void rotateArrow(boolean animate) {
        int to = windDirDegrees;
        int from;
        if (animate) {
            from = oldWindDirDegrees;
        } else {
            from = to;
        }

        RotateAnimation rotate =
                new RotateAnimation(from, to, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotate.setFillAfter(true);
        rotate.setDuration(700);
        ivArrowWindDir.startAnimation(rotate);
    }

    public void setWindDirDegrees(String windDir) {
        int degrees;
        try {
            String strDegrees = windDir.substring(0, windDir.length() - 1);
            degrees = Integer.parseInt(strDegrees);
        } catch (NumberFormatException e) {
            degrees = 180;
        }

        // random data fluctuations for UI debugging
        if (Dbug.RANDOM_DATA) {
            degrees += new Random().nextInt(20) - 10;
        }

        // range check
        if (degrees >= 360) {
            degrees -= 360;
        } else if (degrees < 0) {
            degrees += 360;
        }

        oldWindDirDegrees = windDirDegrees;
        windDirDegrees = degrees;
    }

    @Override
    IntentFilter getRefreshIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Actions.REFRESH_WEATHER);
        filter.addAction(Actions.REFRESH_IMAGE);
        return filter;
    }

    @Override
    void onRefreshIntentReceived(Intent intent) {
        if (Actions.REFRESH_WEATHER.equals(intent.getAction())) {
            updateData(true);
        } else if (Actions.REFRESH_IMAGE.equals(intent.getAction())) {
            String imageName = bundle.getString(Extras.IMG_NAME);
            if ("daywind.png".equals(imageName)) {
                updateImage(ivDayWind, "daywind.png");
            } else if ("daywinddir.png".equals(imageName)) {
                updateImage(ivDayWindDir, "daywinddir.png");
            } else if ("weekwind.png".equals(imageName)) {
                updateImage(ivWeekWind, "weekwind.png");
            } else if ("weekwinddir.png".equals(imageName)) {
                updateImage(ivWeekWindDir, "weekwinddir.png");
            }

            Dbug.log("Updating image [", imageName, "]");
        }
    }

    @Override
    void refreshOnResume() {
        updateData(false);
        initImages();
    }

}

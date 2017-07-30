package mobiric.fhbsc.weather.fragments;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
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
 * Fragment that displays the temperature data.
 */
public class TemperatureFragment extends ARefreshableFragment {
    public static final String GRAPH_DAY_TEMP = "daytempdew.png";
    public static final String GRAPH_WEEK_TEMP = "weektempdew.png";

    /**
     * Assumed maximum temperature the thermometer will show.
     */
    public static final int MAX_TEMP_RANGE = 40;
    /**
     * Assumed minimum temperature the thermometer will show.
     */
    public static final int MIN_TEMP_RANGE = -15;

    TextView tvOutTemp;
    ImageView ivDayTempDew;
    ImageView ivWeekTempDew;
    ImageView ivThermometer;
    View vThermometerRed;

    float tempDegrees = MIN_TEMP_RANGE;
    float oldTempDegrees = MIN_TEMP_RANGE;

    public TemperatureFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_temperature, container, false);
        tvOutTemp = (TextView) rootView.findViewById(R.id.tvRainRate);

        ivDayTempDew = (ImageView) rootView.findViewById(R.id.ivDayRain);
        zoomIfApi14(ivDayTempDew, rootView, GRAPH_DAY_TEMP);
        ivWeekTempDew = (ImageView) rootView.findViewById(R.id.ivMonthRain);
        zoomIfApi14(ivWeekTempDew, rootView, GRAPH_WEEK_TEMP);

        ivThermometer = (ImageView) rootView.findViewById(R.id.ivRainMeter);
        vThermometerRed = rootView.findViewById(R.id.vRainBlue);

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
    void updateData(final boolean animate) {
        // get data from the application cache
        WeatherReading reading = myApp.getCachedWeatherReading();

        String outTemp = reading.outTemp;
        tvOutTemp.setText(outTemp);

        if (outTemp != null) {
            setTempDegrees(outTemp);

            // post to UI thread since it requires the height of ivThermometer
            ivThermometer.post(new Runnable() {
                public void run() {
                    setThermometerHeight(animate);
                }
            });
        }
    }

    void initImages() {
        updateImage(ivDayTempDew, GRAPH_DAY_TEMP);
        updateImage(ivWeekTempDew, GRAPH_WEEK_TEMP);
    }

    void setThermometerHeight(boolean animate) {
        int to = calcOffsetForDegrees(tempDegrees);
        int from;
        if (animate) {
            from = calcOffsetForDegrees(oldTempDegrees);
        } else {
            from = to;
        }

        TranslateAnimation translate = new TranslateAnimation(0, 0, from, to);
        translate.setFillAfter(true);
        translate.setDuration(700);
        vThermometerRed.startAnimation(translate);
    }

    /**
     * Calculates the offset of the red thermometer background view, based on a given temperature.
     *
     * @param degrees temperature
     * @return offset for the view
     */
    int calcOffsetForDegrees(float degrees) {
        float heightRed =
                (degrees - MIN_TEMP_RANGE) * ivThermometer.getHeight()
                        / (MAX_TEMP_RANGE - MIN_TEMP_RANGE);
        int topOffset = (int) (ivThermometer.getHeight() - heightRed);
        return topOffset;
    }

    public void setTempDegrees(String temp) {
        float degrees;
        try {
            String strDegrees = temp.substring(0, temp.length() - 2);
            degrees = Float.parseFloat(strDegrees);
        } catch (NumberFormatException e) {
            degrees = 20;
        }

        // random data fluctuations for UI debugging
        if (Dbug.RANDOM_DATA) {
            degrees += new Random().nextInt(20) - 10;
        }

        // range check
        if (degrees >= MAX_TEMP_RANGE) {
            degrees = MAX_TEMP_RANGE;
        } else if (degrees < MIN_TEMP_RANGE) {
            degrees = MIN_TEMP_RANGE;
        }

        oldTempDegrees = tempDegrees;
        tempDegrees = degrees;
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
            if ("daytempdew.png".equals(imageName)) {
                updateImage(ivDayTempDew, "daytempdew.png");
            } else if ("weektempdew.png".equals(imageName)) {
                updateImage(ivWeekTempDew, "weektempdew.png");
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

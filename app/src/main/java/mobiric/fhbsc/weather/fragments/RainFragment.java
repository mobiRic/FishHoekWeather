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
 * Fragment that displays the rain data.
 */
public class RainFragment extends ARefreshableFragment {
    public static final String GRAPH_DAY_RAIN = "dayrain.png";
    public static final String GRAPH_MONTH_RAIN = "monthrain.png";

    /**
     * Assumed maximum rain rate the meter will show.
     */
    public static final int MAX_RAIN_RANGE = 15;
    /**
     * Assumed minimum rain rate the meter will show.
     */
    public static final int MIN_RAIN_RANGE = 0;

    TextView tvNoRainTitle;
    TextView tvRainTitle;
    TextView tvRainRate;
    ImageView ivDayRain;
    ImageView ivMonthRain;
    ImageView ivRainMeter;
    View vRainBlue;

    float rainMillimetres = MIN_RAIN_RANGE;
    float oldRainMillimetres = MIN_RAIN_RANGE;

    public RainFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_rain, container, false);
        tvNoRainTitle = (TextView) rootView.findViewById(R.id.tvNoRainTitle);
        tvRainTitle = (TextView) rootView.findViewById(R.id.tvRainTitle);
        tvRainRate = (TextView) rootView.findViewById(R.id.tvRainRate);

        ivDayRain = (ImageView) rootView.findViewById(R.id.ivDayRain);
        zoomIfApi14(ivDayRain, rootView, GRAPH_DAY_RAIN);
        ivMonthRain = (ImageView) rootView.findViewById(R.id.ivMonthRain);
        zoomIfApi14(ivMonthRain, rootView, GRAPH_MONTH_RAIN);

        ivRainMeter = (ImageView) rootView.findViewById(R.id.ivRainMeter);
        vRainBlue = rootView.findViewById(R.id.vRainBlue);

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

        final String rainRate = reading.rainRateNow;
        if (rainRate != null) {
            setRainRateMillimetres(rainRate);

            // post to UI thread since it requires the height of ivThermometer
            ivRainMeter.post(new Runnable() {
                public void run() {
                    setRainHeight(animate);
                    setRainText(rainRate);
                }
            });
        }
    }

    void initImages() {
        updateImage(ivDayRain, GRAPH_DAY_RAIN);
        updateImage(ivMonthRain, GRAPH_MONTH_RAIN);
    }

    void setRainHeight(boolean animate) {
        int to = calcOffsetForMillis(rainMillimetres);
        int from;
        if (animate) {
            from = calcOffsetForMillis(oldRainMillimetres);
        } else {
            from = to;
        }

        TranslateAnimation translate = new TranslateAnimation(0, 0, from, to);
        translate.setFillAfter(true);
        translate.setDuration(700);
        vRainBlue.startAnimation(translate);
    }

    void setRainText(String rainRate) {
        boolean isDry = "0.0 mm/hr".equals(rainRate);
        tvNoRainTitle.setVisibility((isDry ? View.VISIBLE : View.GONE));
        tvRainTitle.setVisibility((isDry ? View.GONE : View.VISIBLE));
        tvRainRate.setVisibility((isDry ? View.GONE : View.VISIBLE));

        tvRainRate.setText(rainRate);
    }

    /**
     * Calculates the offset of the blue rain meter background view, based on a given rain rate.
     *
     * @param millis rain rate mm/hr
     * @return offset for the view
     */
    int calcOffsetForMillis(float millis) {
        float heightBlue =
                (millis - MIN_RAIN_RANGE) * ivRainMeter.getHeight()
                        / (MAX_RAIN_RANGE - MIN_RAIN_RANGE);
        int topOffset = (int) (ivRainMeter.getHeight() - heightBlue);
        return topOffset;
    }

    public void setRainRateMillimetres(String rainRate) {
        float millis;
        try {
            String strMillis = rainRate.substring(0, rainRate.length() - 6);
            millis = Float.parseFloat(strMillis);
        } catch (NumberFormatException e) {
            millis = 0;
        }

        // random data fluctuations for UI debugging
        if (Dbug.RANDOM_DATA) {
            millis += new Random().nextInt(8) - 4;
        }

        // range check
        if (millis >= MAX_RAIN_RANGE) {
            millis = MAX_RAIN_RANGE;
        } else if (millis < MIN_RAIN_RANGE) {
            millis = MIN_RAIN_RANGE;
        }

        oldRainMillimetres = rainMillimetres;
        rainMillimetres = millis;
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
            if ("dayrain.png".equals(imageName)) {
                updateImage(ivDayRain, "dayrain.png");
            } else if ("monthrain.png".equals(imageName)) {
                updateImage(ivMonthRain, "monthrain.png");
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

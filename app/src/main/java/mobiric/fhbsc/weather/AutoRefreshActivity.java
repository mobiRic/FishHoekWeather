package mobiric.fhbsc.weather;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Timer;
import java.util.TimerTask;

import lib.debug.Dbug;

/**
 * Provides auto refresh capability to the app.
 */
public class AutoRefreshActivity extends FragmentActivity {
    private static final int MANUAL_REFRESH = -1;

    /**
     * Handle to {@link WeatherApp} instance for caching data.
     */
    WeatherApp myApp;

    int[] autoRefreshValues;
    int autoRefreshPeriod;
    Timer autoRefreshTimer = null;

    public AutoRefreshActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        myApp = (WeatherApp) getApplication();
        autoRefreshValues = getResources().getIntArray(R.array.auto_refresh_values);
        int autoRefresh = loadAutoRefreshSetting();
        if ((autoRefresh < 0) || (autoRefresh >= autoRefreshValues.length)) {
            autoRefresh = autoRefreshValues.length - 1;
            saveAutoRefreshSetting(autoRefresh);
        }
        autoRefreshPeriod = autoRefreshValues[autoRefresh];
    }

    /**
     * Cancel auto refresh when paused.
     */
    @Override
    public void onPause() {
        setAutoRefreshTimer(MANUAL_REFRESH);

        super.onPause();
    }

    /**
     * Restart auto refresh when resumed.
     */
    @Override
    public void onResume() {
        autoRefreshPeriod = autoRefreshValues[loadAutoRefreshSetting()];
        if (MANUAL_REFRESH != autoRefreshPeriod) {
            setAutoRefreshTimer(autoRefreshPeriod);
            doRefresh();
        }

        super.onResume();
    }

    /**
     * Adds the auto-refresh options spinner.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Sets the auto refresh period in seconds.
     *
     * @param position Position in the auto refresh entries & values arrays
     */
    private void setAutoRefreshPeriod(int position) {
        // save setting
        saveAutoRefreshSetting(position);

        // set timer
        autoRefreshPeriod = autoRefreshValues[position];
        setAutoRefreshTimer(autoRefreshPeriod);
        doRefresh();
    }

    /**
     * Sets a repeating timer task to refresh the data.
     *
     * @param autoRefreshPeriod Time in millis between auto-refresh calls
     */
    private void setAutoRefreshTimer(int autoRefreshPeriod) {
        Dbug.log("auto refresh - ", autoRefreshPeriod);

        // cancel previous tasks
        if (autoRefreshTimer != null) {
            autoRefreshTimer.cancel();
            autoRefreshTimer.purge();
            autoRefreshTimer = null;
        }

        if (autoRefreshPeriod != MANUAL_REFRESH) {
            autoRefreshTimer = new Timer();
            autoRefreshTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    doRefresh();
                }
            }, autoRefreshPeriod, autoRefreshPeriod);
        }
    }

    private void saveAutoRefreshSetting(int position) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Editor e = prefs.edit();
        e.putInt("AUTO_REFRESH", position);
        e.apply();
    }

    private int loadAutoRefreshSetting() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getInt("AUTO_REFRESH", 1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh: {
                // manually set the pull to refresh library
                doRefresh();

                break;
            }
            case R.id.menu_spinner: {
                showAutoRefreshOptions();

                break;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }

        // handled by us
        return true;
    }

    /**
     * Shows a dialog with the auto-refresh options in it.
     */
    private void showAutoRefreshOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.refresh_options);
        builder.setItems(R.array.auto_refresh_entries, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                setAutoRefreshPeriod(position);
            }
        });
        builder.show();
    }

    /**
     * Helper method to start refresh from button or auto refresh.
     */
    void doRefresh() {
        runOnUiThread(new Runnable() {
            public void run() {
                Dbug.log("... refreshing ... ");

                myApp.doRefresh();
            }
        });
    }

}
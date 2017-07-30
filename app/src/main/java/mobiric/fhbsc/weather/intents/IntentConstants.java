package mobiric.fhbsc.weather.intents;

/**
 * Declares constants used for Intents. Includes Actions, keys in the Extras, common values.
 */
public class IntentConstants {
    /**
     * Declares intent actions.
     */
    public static class Actions {
        public static final String REFRESH_UPDATE_TIME = "REFRESH_UPDATE_TIME";
        public static final String REFRESH_WEB_WEATHER = "REFRESH_WEB_WEATHER";
        public static final String REFRESH_WEATHER = "REFRESH_WEATHER";
        public static final String REFRESH_IMAGE = "REFRESH_IMAGE";
    }

    /**
     * Declares constants used when passing arguments in Bundles.
     */
    public static class Extras {
        /**
         * Fragment argument for the base URL to use when loading the data.
         */
        public static final String BASE_URL = "BASE_URL";
        /**
         * Fragment argument for the HTML data to load.
         */
        public static final String HTML_DATA = "HTML_DATA";

        /**
         * Fragment argument for the time of the last data update.
         */
        public static final String TIME = "TIME";

        /**
         * Fragment argument for the wind speed.
         */
        public static final String WIND_SPEED = "WIND_SPEED";
        /**
         * Fragment argument for the wind direction.
         */
        public static final String WIND_DIR = "WIND_DIR";
        /**
         * Fragment argument for the wind gust speed.
         */
        public static final String WIND_GUST = "WIND_GUST";
        /**
         * Fragment argument for the wind gust direction.
         */
        public static final String WIND_GUST_DIR = "WIND_GUST_DIR";

        /**
         * Fragment argument for the outside temperature.
         */
        public static final String OUT_TEMP = "OUT_TEMP";
        /**
         * Fragment argument for the minimum outside temperature for the day.
         */
        public static final String OUT_TEMP_MIN = "OUT_TEMP_MIN";
        /**
         * Fragment argument for the maximum outside temperature for the day.
         */
        public static final String OUT_TEMP_MAX = "OUT_TEMP_MAX";

        /**
         * Fragment argument for the rain rate.
         */
        public static final String RAIN_RATE = "RAIN_RATE";
        /**
         * Fragment argument for the minimum rain rate for the day.
         */
        public static final String RAIN_RATE_MIN = "RAIN_RATE_MIN";
        /**
         * Fragment argument for the maximum rain rate for the day.
         */
        public static final String RAIN_RATE_MAX = "RAIN_RATE_MAX";

        /**
         * Fragment argument for the current barometer reading.
         */
        public static final String BAROMETER = "BAROMETER";

        /**
         * Fragment argument for the name of the updated image.
         */
        public static final String IMG_NAME = "IMG_NAME";
    }
}

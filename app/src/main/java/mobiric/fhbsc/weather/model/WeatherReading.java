package mobiric.fhbsc.weather.model;

import com.google.gson.annotations.SerializedName;

import lib.gson.MyGson;

/**
 * Represents a single weather reading data point as received from the server.
 */
public class WeatherReading {
    /*
     * JSON string is: {"Time":"08-Sep-2014 15:25", "windSpeed":"22 knots", "windDir":"135&#176;",
	 * "windGust":"26 knots", "windGustDir":"135&#176;", "barometer":"1024.9 mbar",
	 * "outTemp":"15.3&#176;C", "outTempMin":"14.5&#176;C", "outTempMax":"15.4&#176;C",
	 * "rainDayTotal":"0.0 mm", "rainRateNow":"0.0 mm/hr", "rainMinRate":"0.0 mm/hr",
	 * "rainMaxRate":"0.0 mm/hr"}
	 */

    @SerializedName("Time")
    public String time;
    public String windSpeed;
    public String windDir;
    public String windGust;
    public String windGustDir;
    public String barometer;
    public String outTemp;
    public String outTempMin;
    public String outTempMax;
    public String rainRateNow;
    public String rainDayTotal;
    public String rainMinRate;
    public String rainMaxRate;

    @Override
    public String toString() {
        return MyGson.PARSER.toJson(this);
    }

}

package lib.gson;

import com.google.gson.Gson;

/**
 * Global singleton {@link Gson} container.
 */
public class MyGson {
    // no need to lazy initialise this as it is used app-wide
    public static final Gson PARSER = new Gson();
}

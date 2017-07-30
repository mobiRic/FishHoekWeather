package mobiric.fhbsc.weather.tasks;

import android.os.AsyncTask;

import org.apache.http.client.methods.HttpGet;

import lib.web.WebUtils;

/**
 * Web Service task that GETs a given web address in the background. Caller is notified of results
 * through the {@link BaseWebService.OnBaseWebServiceResponseListener} interface callbacks.
 */
public class BaseWebService extends AsyncTask<String, Void, String> {

    OnBaseWebServiceResponseListener listener;

    public BaseWebService(OnBaseWebServiceResponseListener listener) {
        super();
        this.listener = listener;
    }

    /**
     * Make a GET request to a URL.
     *
     * @param params URL must be passed as the first string parameter
     * @return response body from the given URL
     */
    @Override
    protected String doInBackground(String... params) {
        // empty check
        if ((params == null) || (params.length < 1)) {
            return "Error: No URL provided.";
        }

        HttpGet httpGet = new HttpGet(params[0]);

        // update the results with the body of the response
        return WebUtils.getHttpResponse(httpGet);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (listener != null) {
            listener.onBaseWebServiceResult(result);
        }
    }

    /**
     * Listener interface to be implemented by any class interested in calling this
     * {@link BaseWebService}.
     */
    public static interface OnBaseWebServiceResponseListener {
        /**
         * Called when the response has been successfully received.
         *
         * @return the response
         */
        public void onBaseWebServiceResult(String result);

        /**
         * Called when there was an error loading the response.
         *
         * @return error string
         */
        public void onBaseWebServiceError(String error);
    }

}

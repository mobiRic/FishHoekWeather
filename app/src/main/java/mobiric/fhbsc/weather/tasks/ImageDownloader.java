package mobiric.fhbsc.weather.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import lib.io.IOUtils;
import lib.web.WebUtils;

/**
 * Web Service task that downloads an image in the background and saves it as a file in the
 * application's private directory. Caller is notified of results through the
 * {@link ImageDownloader.OnImageDownloadedListener} interface callbacks.
 */
public class ImageDownloader extends AsyncTask<String, Void, String> {
    OnImageDownloadedListener listener;
    Context context;
    boolean success = false;

    public ImageDownloader(Context context, OnImageDownloadedListener listener) {
        super();
        this.context = context;
        this.listener = listener;
    }

    /**
     * Starts downloading the image.
     *
     * @param url      image to download
     * @param filename file to save it to
     */
    public void execute(String url, String filename) {
        super.execute(url, filename);
    }

    /**
     * Downloads an image from the URL, and saves it as a file.
     *
     * @param params URL must be passed as the first string parameter; filename is the second string
     *               parameter
     * @return name of file that was saved if successful; error message if failure
     */
    @Override
    protected String doInBackground(String... params) {
        success = false;

        // empty check
        if ((params == null) || (params.length < 2)) {
            return "Error: No URL or filename provided.";
        }

        String url = params[0];
        String filename = params[1];

        try {
            BufferedInputStream bis = WebUtils.getStream(url);
            IOUtils.streamToDisk(context, bis, filename);
        } catch (MalformedURLException e) {
            return "Malformed URL [" + url + "] (" + e.getLocalizedMessage() + ")";
        } catch (IOException e) {
            return "IO Error (" + e.getLocalizedMessage() + ")";
        }

        success = true;
        return filename;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (listener != null) {
            if (success) {
                listener.onImageDownloadSuccess(result);
            } else {
                listener.onImageDownloadError(result);
            }
        }
    }

    /**
     * Listener interface to be implemented by any class interested in calling this
     * {@link ImageDownloader}.
     */
    public static interface OnImageDownloadedListener {
        /**
         * Called when the image has been successfully written to disk.
         *
         * @return filename of the image
         */
        public void onImageDownloadSuccess(String filename);

        /**
         * Called when there was an error saving the image.
         *
         * @return error string
         */
        public void onImageDownloadError(String error);
    }

}

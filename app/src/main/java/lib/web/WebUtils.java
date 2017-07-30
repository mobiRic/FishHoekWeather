package lib.web;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import lib.io.IOUtils;

/**
 * Web Service helper methods.
 */
public class WebUtils {

    private static final int BUFFER_IO_SIZE = 8000;

    /**
     * Generic web service call.
     *
     * @param request {@link HttpGet} or {@link HttpPost} that defines the request to fetch.
     * @return result of the call, or an error string
     */
    public static String getHttpResponse(HttpRequestBase request) {
        StringBuilder sb = new StringBuilder();
        try {

            DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            String reason = response.getStatusLine().getReasonPhrase();

            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();

                BufferedReader bReader =
                        new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                String line = null;
                while ((line = bReader.readLine()) != null) {
                    sb.append(line);
                }
            } else {
                sb.append(reason);
            }
        } catch (UnsupportedEncodingException ex) {
        } catch (ClientProtocolException ex1) {
        } catch (IOException ex2) {
        }
        return sb.toString();
    }

    /**
     * Fetches an image from a URL.
     *
     * @param url URL for your image
     * @return {@link Bitmap} image
     * @throws MalformedURLException if the URL is badly formed
     * @throws IOException           if the URL does not return a valid response
     */
    public static Bitmap getBitmap(final String url) throws IOException, MalformedURLException {
        // Code from:
        // http://stackoverflow.com/a/4752490/383414
        // Addresses bug in SDK :
        // http://groups.google.com/group/android-developers/browse_thread/thread/4ed17d7e48899b26/
        BufferedInputStream bis = getStream(url);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(baos, BUFFER_IO_SIZE);
        IOUtils.copyStream(bis, bos);
        bos.flush();
        return (BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.size()));
    }

    /**
     * Opens a {@link BufferedInputStream} from a URL. If the stream is used to create a
     * {@link Drawable} consider using {@link #getDrawable(String)} method instead as it works
     * around an issue which can result in a <code>null</code> drawable.
     *
     * @param url URL to fetch
     * @return {@link BufferedInputStream} providing the contents of the URL
     * @throws IOException if the URL does not return a valid response
     */
    public static BufferedInputStream getStream(final String url)
            throws IOException, MalformedURLException {
        return new BufferedInputStream(new URL(url).openStream(), BUFFER_IO_SIZE);
    }
}

package mobiric.fhbsc.weather.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import lib.debug.Dbug;
import lib.io.IOUtils;
import mobiric.fhbsc.weather.R;
import mobiric.fhbsc.weather.WeatherApp;

/**
 * Base fragment class that registers for intent updates to receive refreshed information. Only
 * listens for local broadcasts sent by the {@link LocalBroadcastManager}.
 */
public abstract class ARefreshableFragment extends Fragment {
    /**
     * Application context for this fragment.
     */
    Context appContext;

    /**
     * Handle to {@link WeatherApp} instance for caching data.
     */
    WeatherApp myApp;

    /**
     * Bundle containing data for this fragment. Initialised with the arguments returned by
     * {@link #getArguments()}, and updated when {@link #refreshReceiver} gets a new intent.
     */
    Bundle bundle = null;

    /**
     * Hold a reference to the current animator, so that it can be canceled mid-way.
     */
    Animator mCurrentAnimator;
    /**
     * The system "short" animation time duration, in milliseconds. This duration is ideal for
     * subtle animations or animations that occur very frequently.
     */
    int mAnimationDuration;
    View.OnClickListener unzoomListener;


    /**
     * Receiver for refresh intents. Passes the intent to the implementing subclass.
     */
    private BroadcastReceiver refreshReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                bundle.putAll(intent.getExtras());
            }
            onRefreshIntentReceived(intent);
        }
    };

    public ARefreshableFragment() {
        super();
    }

    /**
     * Initialises the application context for this fragment.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        appContext = activity.getApplicationContext();
        myApp = (WeatherApp) activity.getApplication();
    }

    /**
     * Initialises the {@link #bundle}.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (bundle == null) {
            bundle = getArguments();
            if (bundle == null) {
                bundle = new Bundle();
            }
        }

        // Retrieve and cache the system's default "short" animation time.
        mAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    @Override
    public void onResume() {
        super.onResume();
        register();
        refreshOnResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregister();
    }

    /**
     * Override this method to return the Intent Filter to match refreshed data sent to the
     * implementing class.
     *
     * @return {@link IntentFilter} that this {@link ARefreshableFragment} is registered to receive.
     */
    abstract IntentFilter getRefreshIntentFilter();

    /**
     * Override this method to receive notification of a data refresh. Any data received will
     * already have been added to {@link #bundle} before this method is called.
     *
     * @param intent {@link Intent} received
     */
    abstract void onRefreshIntentReceived(Intent intent);

    /**
     * Override this method to do any data refresh that may be required when this
     * {@link ARefreshableFragment} is created, or resumes from a paused state. This allows the
     * fragment to display updated data that may have been refreshed while the
     * {@link #refreshReceiver} was not registered.</p>
     * <p>
     * Strictly speaking this can be accomplished by overriding the {@link #onResume()} method.
     * Creating this {@link #refreshOnResume()} abstract method means that this update stage cannot
     * be forgotten.
     */
    abstract void refreshOnResume();

    private void register() {
        LocalBroadcastManager.getInstance(appContext).registerReceiver(refreshReceiver,
                getRefreshIntentFilter());
    }

    private void unregister() {
        LocalBroadcastManager.getInstance(appContext).unregisterReceiver(refreshReceiver);
    }

    /**
     * Updates the given view with an image file. Also resizes the view to fit the width of the
     * screen.
     *
     * @param view      {@link ImageView} to update
     * @param imagePath path to the image
     */
    public void updateImage(final ImageView view, String imagePath) {
        Drawable drawable = null;
        try {
            Bitmap bitmap = IOUtils.getBitmap(appContext, imagePath);
            drawable = new BitmapDrawable(getResources(), bitmap);
        } catch (Exception e) {
            Dbug.log("Image not updated [", imagePath, "] ", e.getLocalizedMessage());
        }

        view.setImageDrawable(drawable);
    }

    /**
     * Override this to allow BACK button processing.
     *
     * @return <code>true</code> if the BACK button has been processed; <code>false</code> to pass
     * the event on
     */
    public boolean onBackPressed() {
        if (unzoomListener != null) {
            unzoomListener.onClick(null);
            return true;
        }

        return false;
    }

    void zoomIfApi14(final View imageView, final View rootView, final String imageName) {
        imageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                zoomImageFromThumb(imageView, rootView, imageName);
            }
        });
    }

    void zoomImageFromThumb(final View thumbView, final View rootView, String imageName) {
        // TODO this animation doesn't really work for this case

        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) rootView.findViewById(R.id.ivZoomedImage);
        // expandedImageView.setImageResource(imageResId);
        updateImage(expandedImageView, imageName);
        final View zoomBackground = rootView.findViewById(R.id.backgroundZoomedImage);


        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        rootView.findViewById(R.id.layoutContainer).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds.width()
                / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);
        zoomBackground.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set.play(
                ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left,
                        finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top,
                        finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f))
                .with(ObjectAnimator.ofFloat(zoomBackground, View.ALPHA, 0f, 1f));
        set.setDuration(mAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        unzoomListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                                startScaleFinal))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y,
                                startScaleFinal))
                        .with(ObjectAnimator.ofFloat(zoomBackground, View.ALPHA, 0f));
                set.setDuration(mAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        zoomBackground.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        zoomBackground.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;

                unzoomListener = null;
            }
        };
        expandedImageView.setOnClickListener(unzoomListener);
    }
}
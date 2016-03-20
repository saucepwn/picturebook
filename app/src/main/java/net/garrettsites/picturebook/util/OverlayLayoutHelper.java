package net.garrettsites.picturebook.util;

import android.view.MotionEvent;
import android.view.View;

import net.garrettsites.picturebook.R;
import net.garrettsites.picturebook.activities.ViewSlideshowActivity;

/**
 * Created by garrett on 3/20/2016.
 */
public class OverlayLayoutHelper implements View.OnTouchListener {
    private boolean mIsOverlayAllowed = false;
    private View overlayRootView;
    private final ViewSlideshowActivity mViewSlideshowActivity;

    /**
     * Creates a new instance of the OverlayLayoutHelper class. This class supports the UI overlay
     * that's shown when a user taps the View Slideshow activity.
     * @param slideshowActivity The ViewSlideshow activity. The Activity reference must be passed so
     *                          the overlay can finish the activity if the user requests it.
     * @param overlayRootView The root View of the overlay.
     */
    public OverlayLayoutHelper(ViewSlideshowActivity slideshowActivity, View overlayRootView) {
        if (overlayRootView == null) throw new IllegalArgumentException("overlayRootView");

        this.mViewSlideshowActivity = slideshowActivity;
        this.overlayRootView = overlayRootView;

        // Hide overlay UI if the user taps it.
        overlayRootView.setOnTouchListener(this);

        // Hook up the "Exit Slideshow" button.
        overlayRootView.findViewById(R.id.overlay_exit_slideshow_layout)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewSlideshowActivity.finishSlideshow();
                    }
                });
    }

    /**
     * Sets the overlay allowed flag to true, meaning the overlay is now able to be displayed.
     */
    public void setOverlayAllowed() {
        mIsOverlayAllowed = true;
    }

    /**
     * Shows the overlay if the overlay is allowed to be shown.
     */
    public void showOverlay() {
        if (!mIsOverlayAllowed) return;

        mViewSlideshowActivity.pauseSlideshow();
        overlayRootView.setVisibility(View.VISIBLE);
    }

    /**
     * Hide the overlay if it's clicked.
     * @param v The view that was clicked.
     * @param event The touch event.
     * @return True if the touch event was handled, false otherwise.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        View overlayView = v.findViewById(R.id.view_slideshow_overlay_root_layout);
        overlayView.setVisibility(View.GONE);

        mViewSlideshowActivity.resumeSlideshow();

        // Prevent the event from propagating downward.
        return true;
    }
}

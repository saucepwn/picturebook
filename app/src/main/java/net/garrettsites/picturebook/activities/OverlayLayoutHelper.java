package net.garrettsites.picturebook.activities;

import android.view.MotionEvent;
import android.view.View;

import net.garrettsites.picturebook.R;

/**
 * Created by garrett on 3/20/2016.
 */
public class OverlayLayoutHelper implements View.OnTouchListener {
    private boolean isOverlayAllowed = false;
    private View overlayRootView;
    private final ViewSlideshowActivity viewSlideshowActivity;

    /**
     * Creates a new instance of the OverlayLayoutHelper class.
     * @param slideshowActivity The ViewSlideshow activity. The Activity reference must be passed so
     *                          the overlay can finish the activity if the user requests it.
     * @param overlayRootView The root View of the overlay.
     */
    public OverlayLayoutHelper(ViewSlideshowActivity slideshowActivity, View overlayRootView) {
        if (overlayRootView == null) throw new IllegalArgumentException("overlayRootView");

        this.viewSlideshowActivity = slideshowActivity;
        this.overlayRootView = overlayRootView;

        // Hide overlay UI if the user taps it.
        overlayRootView.setOnTouchListener(this);

        // Hook up the "Exit Slideshow" button.
        overlayRootView.findViewById(R.id.overlay_exit_slideshow_layout)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewSlideshowActivity.finishSlideshow();
                    }
                });
    }

    /**
     * Sets the overlay allowed flag to true, meaning the overlay is now able to be displayed.
     */
    public void setOverlayAllowed() {
        isOverlayAllowed = true;
    }

    /**
     * @return Whether or not the overlay is allowed at this time.
     */
    public boolean isOverlayAllowed() {
        return isOverlayAllowed;
    }

    /**
     * Shows the overlay if the overlay is allowed to be shown.
     */
    public void showOverlay() {
        if (!isOverlayAllowed) return;

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

        // Prevent the event from propegating downward.
        return true;
    }
}

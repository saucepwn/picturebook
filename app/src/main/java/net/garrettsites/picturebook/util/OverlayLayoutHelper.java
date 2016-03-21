package net.garrettsites.picturebook.util;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.garrettsites.picturebook.R;
import net.garrettsites.picturebook.activities.ViewSlideshowActivity;
import net.garrettsites.picturebook.model.IPhoto;

import java.util.HashMap;
import java.util.Map;

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
        writeInsightsToUi(mViewSlideshowActivity.getCurrentPhoto());
        overlayRootView.setVisibility(View.VISIBLE);
    }

    private void writeInsightsToUi(IPhoto photo) {
        Resources r = mViewSlideshowActivity.getResources();

        LinearLayout insightsContainer =
                (LinearLayout) overlayRootView.findViewById(R.id.overlay_photo_insights_container);

        // Remove any old insights in the UI.
        insightsContainer.removeAllViews();

        HashMap<String, String> insightsMap = photo.getPhotoInsights().getLocalizedInsights(r);

        for (Map.Entry<String, String> entry : insightsMap.entrySet()) {
            LinearLayout insightRow = new LinearLayout(mViewSlideshowActivity);
            insightRow.setOrientation(LinearLayout.HORIZONTAL);

            TextView insightKey = new TextView(mViewSlideshowActivity);
            TextView insightValue = new TextView(mViewSlideshowActivity);

            insightKey.setPadding(0, 2, 5, 2);
            insightValue.setPadding(0, 2, 5, 2);

            insightKey.setTextColor(r.getColor(android.R.color.white));
            insightValue.setTextColor(r.getColor(android.R.color.white));

            insightKey.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            insightValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

            insightKey.setText(entry.getKey() + ":");
            insightValue.setText(entry.getValue());

            insightRow.addView(insightKey);
            insightRow.addView(insightValue);

            insightsContainer.addView(insightRow);
        }
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

package net.garrettsites.picturebook.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * Created by garre on 7/6/2016.
 */
public class ImageResizer {
    private static final String TAG = ImageResizer.class.getName();

    // Image resizing configuration. When images are larger than the display's size, shrink the
    // image to this size, where 1.0 is the size of the tablet's screen.
    private static final double RESIZE_SCALE_FACTOR = 1.0;

    private Bitmap mBitmap;

    private int bitmapWidth;
    private int bitmapHeight;

    private int scaledScreenWidth;
    private int scaledScreenHeight;

    public ImageResizer(Context context, Bitmap bitmap) {
        this.mBitmap = bitmap;

        this.bitmapWidth = bitmap.getWidth();
        this.bitmapHeight = bitmap.getHeight();

        // Get the device's screen size and apply scale factor.
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);

        this.scaledScreenWidth = (int) (metrics.heightPixels * RESIZE_SCALE_FACTOR);
        this.scaledScreenHeight = (int) (metrics.widthPixels * RESIZE_SCALE_FACTOR);
    }

    /**
     * Determines whether or not the photo needs to be resized. A photo will be resized if it is
     * larger than the device's screen size plus a scale multiplier. Shrinking image sizes keeps
     * the app running performant especially on systems with low resources.
     * @return True if both the width and the height of the bitmap are larger than the device's
     *         size multiplied by the scale factor.
     */
    public boolean doesImageNeedToBeResized() {
        return (this.bitmapWidth > scaledScreenWidth && this.bitmapHeight > scaledScreenHeight);
    }

    /**
     * Shrinks the input bitmap so that the input bitmap's smallest dimension is the device's screen
     * size multiplied by the scale factor.
     * @return A shrunk bitmap.
     */
    public Bitmap shrinkBitmap() {
        if (!doesImageNeedToBeResized()) {
            Log.w(TAG, "shrinkBitmap was called but the image does not need to be resized.");
            return mBitmap;
        }

        double widthOversizeFactor = (double) bitmapWidth / scaledScreenWidth;
        double heightOversizeFactor = (double) bitmapHeight / scaledScreenHeight;
        double scaleFactor;

        if (widthOversizeFactor < heightOversizeFactor) {
            scaleFactor = 1 / widthOversizeFactor;
        } else {
            scaleFactor = 1 / heightOversizeFactor;
        }

        return Bitmap.createScaledBitmap(
                mBitmap,
                (int) (bitmapWidth * scaleFactor),
                (int) (bitmapHeight * scaleFactor),
                true);
    }
}

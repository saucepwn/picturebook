package net.garrettsites.picturebook.util;

import java.util.Random;
import java.util.Stack;

/**
 * Created by Garrett on 12/1/2015.
 */
public class RandomPhotoOrder extends PhotoOrder {

    private Stack<Integer> mRandomOrder = new Stack<>();

    public RandomPhotoOrder(int numPhotos) {
        super(numPhotos);
    }

    @Override
    public int getNextPhotoIdx() {
        if (mRandomOrder.size() == 0) randomizePhotoOrder();
        return mRandomOrder.pop();
    }

    /**
     * Randomly generates a photo order and stores the result in mRandomOrder.
     */
    private void randomizePhotoOrder() {
        // Create an array of integers, in order.
        int[] inOrder = new int[mNumPhotos];
        int arraySize = mNumPhotos;
        for (int i = 0; i < mNumPhotos; i++) {
            inOrder[i] = i;
        }

        // Randomly remove elements from the array and add them to the "random" queue. Take the last
        // element in the in-order array and move it to the index that was just read from. This way,
        // we can build the "random" queue in O(n) time.
        Random r = new Random();
        while (arraySize > 0) {
            int randIdx = r.nextInt(arraySize);
            mRandomOrder.push(inOrder[randIdx]);
            inOrder[randIdx] = inOrder[--arraySize];
        }
    }
}

package net.garrettsites.picturebook.model;

import org.joda.time.DateTime;

/**
 * Created by Garrett on 11/20/2015.
 */
public class Album {
    private String mType;
    private String mName;
    private DateTime mCreatedTime;
    private DateTime mUpdatedTime;
    private int mId;

    public Album(String type, String name, DateTime createdTime, DateTime updatedTime, int id) {
        mType = type;
        mName = name;
        mCreatedTime = createdTime;
        mUpdatedTime = updatedTime;
        mId = id;
    }

    public String getType() {
        return mType;
    }

    public String getName() {
        return mName;
    }

    public DateTime getCreatedTime() {
        return mCreatedTime;
    }

    public DateTime getUpdatedTime() {
        return mUpdatedTime;
    }

    public int getId() {
        return mId;
    }
}

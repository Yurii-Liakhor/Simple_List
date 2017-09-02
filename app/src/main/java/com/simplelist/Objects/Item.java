package com.simplelist.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * Created by Yurii on 01.07.2017.
 */

public class Item implements Parcelable {
    private static final String JSON_UUID = "uuid";
    private static final String JSON_TITLE = "title";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_TIMESTAMP = "timestamp";
    private static final String JSON_IMAGE = "image";

    private String uuid;
    private String title;
    private String description;
    private Timestamp timestamp;
    private String image;

    public Item(String uuid, String title, String description, String image){
        this.uuid = uuid;
        this.title = title;
        this.description = description;
        this.image = image;
    }

    public Item(String uuid, String title, String description, Timestamp timestamp, String image){
        this.uuid = uuid;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.image = image;
    }

    public Item(JSONObject json) throws JSONException{
        uuid = json.getString(JSON_UUID);
        title = json.getString(JSON_TITLE);
        description = json.getString(JSON_DESCRIPTION);
        timestamp = Timestamp.valueOf(json.get(JSON_TIMESTAMP).toString());
        image = json.getString(JSON_IMAGE);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_UUID, uuid);
        json.put(JSON_TITLE, title);
        json.put(JSON_DESCRIPTION, description);
        json.put(JSON_TIMESTAMP, timestamp);
        json.put(JSON_IMAGE, image);
        return json;
    }

    public String getUuid() {
        return uuid;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString(){
        return this.getTitle();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(uuid, item.uuid) &&
                Objects.equals(title, item.title) &&
                Objects.equals(description, item.description) &&
                Objects.equals(timestamp, item.timestamp) &&
                Objects.equals(image, item.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, title, description, timestamp, image);
    }

    //////////////////
    private Item(Parcel in){
        uuid = in.readString();
        title = in.readString();
        description = in.readString();
        image = in.readString();
        timestamp = (java.sql.Timestamp)in.readSerializable();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(uuid);
        out.writeString(title);
        out.writeString(description);
        out.writeString(image);
        out.writeSerializable(timestamp);
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}

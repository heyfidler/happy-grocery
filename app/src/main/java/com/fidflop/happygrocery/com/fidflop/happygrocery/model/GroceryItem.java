package com.fidflop.happygrocery.com.fidflop.happygrocery.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class GroceryItem implements Parcelable {
    private String name;
    final private Date creationDate;
    private boolean strikeout;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GroceryItem(){
        creationDate = new Date();
    }

    GroceryItem(String name){
        this.name = name;
        creationDate = new Date();
    }

    public boolean isStrikeout() {
        return strikeout;
    }

    public void setStrikeout(boolean strikeout) {
        this.strikeout = strikeout;
    }

    private GroceryItem(Parcel in) {
        name = in.readString();
        long tmpCreationDate = in.readLong();
        creationDate = tmpCreationDate != -1 ? new Date(tmpCreationDate) : null;
        strikeout = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(creationDate != null ? creationDate.getTime() : -1L);
        dest.writeByte((byte) (strikeout ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<GroceryItem> CREATOR = new Parcelable.Creator<GroceryItem>() {
        @Override
        public GroceryItem createFromParcel(Parcel in) {
            return new GroceryItem(in);
        }

        @Override
        public GroceryItem[] newArray(int size) {
            return new GroceryItem[size];
        }
    };
}
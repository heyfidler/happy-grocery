package com.fidflop.happygrocery.com.fidflop.happygrocery.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class GroceryItem implements Parcelable {
    private String name;
    private Date creationDate;
    private int quantity;
    private boolean strikeout;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public GroceryItem(){
        creationDate = new Date();
    }

    public boolean isStrikeout() {
        return strikeout;
    }

    public void setStrikeout(boolean strikeout) {
        this.strikeout = strikeout;
    }

    public Date getCreationDate() {
        return creationDate;
    }



    protected GroceryItem(Parcel in) {
        name = in.readString();
        long tmpCreationDate = in.readLong();
        creationDate = tmpCreationDate != -1 ? new Date(tmpCreationDate) : null;
        quantity = in.readInt();
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
        dest.writeInt(quantity);
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
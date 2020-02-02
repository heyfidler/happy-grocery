package com.fidflop.happygrocery.com.fidflop.happygrocery.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Recipe implements Parcelable {
    private String name;
    private boolean current;
    private List<GroceryItem> groceryItems;
    private Date creationDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public List<GroceryItem> getGroceryItems() {
        return groceryItems;
    }

    public void setGroceryItems(List<GroceryItem> groceryItems) {
        this.groceryItems = groceryItems;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Recipe(){
        creationDate = new Date();
    }

    protected Recipe(Parcel in) {
        name = in.readString();
        creationDate = new Date(in.readLong());
        current = in.readByte() != 0x00;
        if (in.readByte() == 0x01) {
            groceryItems = new ArrayList<>();
            in.readList(groceryItems, GroceryItem.class.getClassLoader());
        } else {
            groceryItems = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(creationDate.getTime());
        dest.writeByte((byte) (current ? 0x01 : 0x00));
        if (groceryItems == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(groceryItems);
        }
    }

    @SuppressWarnings("unused")
    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
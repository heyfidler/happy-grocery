package com.fidflop.happygrocery.com.fidflop.happygrocery.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroceryList implements Parcelable {
    private String name;
    private boolean current;
    private List<GroceryItem> groceryItems;
    private final Date creationDate;

    @SuppressWarnings("unused")
    public Date getCreationDate() {
        return creationDate;
    }

    public GroceryList(){
        creationDate = new Date();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GroceryItem> getGroceryItems() {
        return groceryItems;
    }

    public void setGroceryItems(List<GroceryItem> groceryItems) {
        this.groceryItems = groceryItems;
    }


    protected GroceryList(Parcel in) {
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
    public static final Parcelable.Creator<GroceryList> CREATOR = new Parcelable.Creator<GroceryList>() {
        @Override
        public GroceryList createFromParcel(Parcel in) {
            return new GroceryList(in);
        }

        @Override
        public GroceryList[] newArray(int size) {
            return new GroceryList[size];
        }
    };

    public static GroceryList buildDummyGroceryList() {
        GroceryList groceryList = new GroceryList();
        groceryList.setName("List for Sat Picnic");

        List<GroceryItem> itemList = new ArrayList<>();
        itemList.add(new GroceryItem("bottle of wine"));
        itemList.add(new GroceryItem("baguette"));
        itemList.add(new GroceryItem("gouda cheese"));
        itemList.add(new GroceryItem("green grapes"));
        itemList.add(new GroceryItem("sparkling water"));
        itemList.add(new GroceryItem("napkins"));
        groceryList.setGroceryItems(itemList);
        return groceryList;
    }
}
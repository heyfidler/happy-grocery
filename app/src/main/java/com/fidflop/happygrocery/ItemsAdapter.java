package com.fidflop.happygrocery;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.fidflop.happygrocery.com.fidflop.happygrocery.model.GroceryItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import github.nisrulz.recyclerviewhelper.RVHAdapter;
import github.nisrulz.recyclerviewhelper.RVHViewHolder;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder>
        implements RVHAdapter {

    private List<GroceryItem> items;
    private static final String TAG = "ItemsAdapter";
    private Context context;

    public class ItemViewHolder extends RecyclerView.ViewHolder implements RVHViewHolder {
        final TextView txt;
        final EditText editTxt;

        private ItemViewHolder(final View itemView) {
            super(itemView);

            txt = itemView.findViewById(R.id.txt);
            editTxt = itemView.findViewById(R.id.edit_text);

            editTxt.setOnFocusChangeListener(new View.OnFocusChangeListener()  {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    Toast.makeText(v.getContext(), "focus change ", Toast.LENGTH_SHORT)
                            .show();
                    if (!hasFocus) {
                        displayTxt();
                    }
                }
            });

            ImageView editImage = itemView.findViewById(R.id.edit);
            editImage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (txt.getVisibility() == View.VISIBLE) {
                        displayEditTxt();
                    } else {
                        displayTxt();
                    }
                }
            });
        }

        void strikeoutCheck() {
            GroceryItem groceryItem = items.get(getAdapterPosition());
            if (groceryItem != null && txt != null && txt.getText() != null && groceryItem.isStrikeout()) {
                txt.setPaintFlags(txt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                txt.setPaintFlags(0);
            }
        }

        private void update() {
            GroceryItem groceryItem = items.get(getAdapterPosition());
            if (groceryItem != null && txt != null && txt.getText() != null) {
                groceryItem.setName(txt.getText().toString());
            }
        }

        private void displayTxt() {
            txt.setText(editTxt.getText());
            editTxt.setVisibility(View.GONE);
            txt.setVisibility(View.VISIBLE);
            hideKeyboard();
            update();
        }

        private void displayEditTxt() {
            editTxt.setText(txt.getText());
            editTxt.setVisibility(View.VISIBLE);
            editTxt.requestFocus();
            txt.setVisibility(View.GONE);
        }

        private void hideKeyboard() {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editTxt.getWindowToken(),0);
        }

        @Override
        public void onItemClear() {
        }

        @Override
        public void onItemSelected(int actionstate) {
        }
    }

    ItemsAdapter(Context context, List<GroceryItem> groceryItems) {
        if (groceryItems != null) {
            items = groceryItems;
        } else {
            items = new ArrayList<>();
        }
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, final int position) {
        GroceryItem groceryItem = items.get(position);
        if (groceryItem != null) {
            holder.txt.setText(groceryItem.getName());
            holder.strikeoutCheck();
        }
    }

    @Override
    @NonNull
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onItemDismiss(int position, int direction) {
        if (direction == 16) {
            remove(position);
        } else {
            GroceryItem groceryItem = items.get(position);
            if (groceryItem == null) {
                return;
            }
            if (!groceryItem.isStrikeout()) {
                groceryItem.setStrikeout(true);
            } else {
                groceryItem.setStrikeout(false);
            }
            notifyItemChanged(position);
        }
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        swap(fromPosition, toPosition);
        return false;
    }

    private void remove(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.item_delete_confirm)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        items.remove(position);
                        notifyItemRemoved(position);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notifyItemChanged(position);
                    }
                })
                .show();
    }

    public List<GroceryItem> getGroceryItems() {
        return items;
    }

    void addItem(GroceryItem groceryItem) {
        items.add(groceryItem);
        notifyItemInserted(items.indexOf(groceryItem));
    }

    private void swap(int firstPosition, int secondPosition) {
        Collections.swap(items, firstPosition, secondPosition);
        notifyItemMoved(firstPosition, secondPosition);
    }
}

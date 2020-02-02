package com.fidflop.happygrocery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fidflop.happygrocery.com.fidflop.happygrocery.model.GroceryList;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import github.nisrulz.recyclerviewhelper.RVHAdapter;
import github.nisrulz.recyclerviewhelper.RVHViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroceryListsAdapter extends RecyclerView.Adapter<GroceryListsAdapter.GroceryListViewHolder>
        implements RVHAdapter, EventListener<QuerySnapshot> {

    private List<DocumentSnapshot> mSnapshots = new ArrayList<>();
    private static final String TAG = "GroceryListsAdapter";
    private Query mQuery;
    private ListenerRegistration mRegistration;
    private CollectionReference groceryListsCollectionReference;
    private Context context;

    public class GroceryListViewHolder extends RecyclerView.ViewHolder implements RVHViewHolder {

        final TextView txt;
        final EditText editTxt;

        private GroceryListViewHolder(final View itemView) {
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

        private void update() {
            DocumentSnapshot snapshot = mSnapshots.get(getAdapterPosition());
            GroceryList groceryList = snapshot.toObject(GroceryList.class);
            if (groceryList != null && txt != null && txt.getText() != null) {
                groceryList.setName(txt.getText().toString());
            }
            updateGroceryList(snapshot, groceryList);
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
            System.out.println("Item is unselected");
        }

        @Override
        public void onItemSelected(int actionstate) {
            System.out.println("Item is selected");
        }
    }

    @Override
    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "onEvent:error", e);
            return;
        }

        // Dispatch the event
        Log.d(TAG, "onEvent:numChanges:" + documentSnapshots.getDocumentChanges().size());
        for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
            switch (change.getType()) {
                case ADDED:
                    onRemoteDocumentAdded(change);
                    break;
                case MODIFIED:
                    onRemoteDocumentModified(change);
                    break;
                case REMOVED:
                    onRemoteDocumentRemoved(change);
                    break;
            }
        }
    }

    private void onRemoteDocumentAdded(DocumentChange change) {
        mSnapshots.add(change.getNewIndex(), change.getDocument());
        notifyItemInserted(change.getNewIndex());
    }

    private void onRemoteDocumentModified(DocumentChange change) {
        if (change.getOldIndex() == change.getNewIndex()) {
            // Item changed but remained in same position
            mSnapshots.set(change.getOldIndex(), change.getDocument());
            notifyItemChanged(change.getOldIndex());
        } else {
            // Item changed and changed position
            mSnapshots.remove(change.getOldIndex());
            mSnapshots.add(change.getNewIndex(), change.getDocument());
            notifyItemMoved(change.getOldIndex(), change.getNewIndex());
        }
    }

    private void onRemoteDocumentRemoved(DocumentChange change) {
        mSnapshots.remove(change.getOldIndex());
        notifyItemRemoved(change.getOldIndex());
    }

    GroceryListsAdapter(FirebaseUser user, Context context) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        String path = "users/" + user.getUid() + "/GROCERY_LIST";
        mQuery = mFirestore.collection(path).orderBy("creationDate");
        groceryListsCollectionReference = mFirestore.collection(path);
        this.context = context;

        startListening();
    }

    @Override
    public int getItemCount() {
        return mSnapshots.size();
    }

    @Override
    public void onBindViewHolder(@NonNull GroceryListViewHolder holder, final int position) {
        GroceryList groceryList = mSnapshots.get(position).toObject(GroceryList.class);
        if (groceryList != null) {
            holder.txt.setText(groceryList.getName());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, ItemsActivity.class);
                DocumentSnapshot snapshot = mSnapshots.get(position);
                GroceryList groceryList = snapshot.toObject(GroceryList.class);

                intent.putExtra("path", snapshot.getReference().getPath());
                intent.putExtra("groceryList", groceryList);

                context.startActivity(intent);
            }
        });
    }

    @Override
    @NonNull
    public GroceryListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_grocery_list, parent, false);

        return new GroceryListViewHolder(view);
    }

    @Override
    public void onItemDismiss(int position, int direction) {
        remove(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        swap(fromPosition, toPosition);
        return false;
    }

    private void remove(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.grocery_list_delete_confirm)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSnapshots.get(position).getReference().delete();
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

    void add(GroceryList groceryList) {
        groceryListsCollectionReference.document().set(groceryList);
    }

    private void updateGroceryList(DocumentSnapshot groceryListDocumentSnapshot, GroceryList groceryList) {
        groceryListDocumentSnapshot.getReference().set(groceryList);
    }

    private void swap(int firstPosition, int secondPosition) {
        Collections.swap(mSnapshots, firstPosition, secondPosition);
        notifyItemMoved(firstPosition, secondPosition);
    }

    private void startListening() {
        if (mQuery != null && mRegistration == null) {
            mRegistration = mQuery.addSnapshotListener(MetadataChanges.INCLUDE,this);
        }
    }
}

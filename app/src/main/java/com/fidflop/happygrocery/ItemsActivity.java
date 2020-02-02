package com.fidflop.happygrocery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.fidflop.happygrocery.com.fidflop.happygrocery.model.GroceryItem;
import com.fidflop.happygrocery.com.fidflop.happygrocery.model.GroceryList;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import github.nisrulz.recyclerviewhelper.RVHItemDividerDecoration;
import github.nisrulz.recyclerviewhelper.RVHItemTouchHelperCallback;

public class ItemsActivity extends AppCompatActivity {

    private RecyclerView itemsRV;
    private ItemsAdapter adapter;
    private FirebaseUser user;
    private static final String TAG = "ItemsActivity";
    GroceryList groceryList;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        path = getIntent().getStringExtra("path");
        groceryList = getIntent().getParcelableExtra("groceryList");

        Toolbar myToolbar = findViewById(R.id.my_toolbar);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(groceryList.getName());

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, FirebaseUIActivity.class));
            finish();
        } else {
            itemsRV = findViewById(R.id.rv_items);
            setupRV();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
                Toast.makeText(ItemsActivity.this, "Adding", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void updateGroceryList() {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = mFirestore.document(path);
        groceryList.setGroceryItems(adapter.getGroceryItems());
        documentReference.set(groceryList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();
        updateGroceryList();
        Log.i(TAG, "++ ON STOP ++");
    }

    public void setupRV() {
        adapter = new ItemsAdapter(this, groceryList.getGroceryItems());
        itemsRV.setLayoutManager(new LinearLayoutManager(this));
        itemsRV.setAdapter(adapter);

        // Setup onItemTouchHandler
        ItemTouchHelper.Callback callback = new RVHItemTouchHelperCallback(adapter, true, true, true);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(itemsRV);

        // Set the divider
        itemsRV.addItemDecoration(
                new RVHItemDividerDecoration(this, LinearLayoutManager.VERTICAL));

        Toast.makeText(ItemsActivity.this, R.string.item_directions,
                Toast.LENGTH_LONG).show();
    }

    private void addItem() {
        GroceryItem groceryItem = new GroceryItem();
        groceryItem.setName("New item");
        adapter.addItem(groceryItem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.logout:
                Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT)
                        .show();

                AuthUI.getInstance()
                        .signOut(ItemsActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(ItemsActivity.this, FirebaseUIActivity.class));
                            }
                        });
                break;
            case R.id.user_info:
                AlertDialog alertDialog = new AlertDialog.Builder(ItemsActivity.this).create();
                alertDialog.setTitle("user info");

                alertDialog.setMessage("name: " + user.getDisplayName() + "\n"
                        + "email: " + user.getEmail() + "\n"
                        + "id: " + user.getUid());
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                break;
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }
}

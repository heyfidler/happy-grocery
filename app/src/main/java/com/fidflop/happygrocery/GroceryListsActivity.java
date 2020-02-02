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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.fidflop.happygrocery.com.fidflop.happygrocery.model.GroceryList;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import github.nisrulz.recyclerviewhelper.RVHItemDividerDecoration;
import github.nisrulz.recyclerviewhelper.RVHItemTouchHelperCallback;


public class GroceryListsActivity extends AppCompatActivity {
    private RecyclerView groceryListRV;
    private GroceryListsAdapter adapter;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_lists);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //db = new DB();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, FirebaseUIActivity.class));
            finish();
        } else {
            groceryListRV = findViewById(R.id.rv_grocery_lists);
            setupRV();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addGroceryList();
                Toast.makeText(GroceryListsActivity.this, "Adding", Toast.LENGTH_SHORT)
                        .show();
                //db.saveGroceryLists(user,groceryLists);
            }
        });


    }

    public void setupRV() {
        adapter = new GroceryListsAdapter(user, this);
        groceryListRV.setLayoutManager(new LinearLayoutManager(this));
        groceryListRV.setAdapter(adapter);

        // Setup onItemTouchHandler
        ItemTouchHelper.Callback callback = new RVHItemTouchHelperCallback(adapter, true, true, true);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(groceryListRV);

        // Set the divider
        groceryListRV.addItemDecoration(
                new RVHItemDividerDecoration(this, LinearLayoutManager.VERTICAL));

        Toast.makeText(GroceryListsActivity.this, "Swipe lists left/right to delete\nLong press and drag and drop",
                Toast.LENGTH_LONG).show();
    }

    private void addGroceryList() {
        GroceryList groceryList = new GroceryList();
        groceryList.setName("New List");
        adapter.add(groceryList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.logout:
                Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT)
                        .show();

                AuthUI.getInstance()
                        .signOut(GroceryListsActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(GroceryListsActivity.this, FirebaseUIActivity.class));
                            }
                        });
                break;
            case R.id.user_info:
                AlertDialog alertDialog = new AlertDialog.Builder(GroceryListsActivity.this).create();
                alertDialog.setTitle("user info");

                alertDialog.setMessage("name: " + user.getDisplayName() + "\n"
                    + "email: " + user.getEmail() +"\n"
                    + "id: " + user.getUid());
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                break;
            default:
                break;
        }

        return true;
    }
}

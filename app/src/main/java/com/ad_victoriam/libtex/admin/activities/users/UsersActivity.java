package com.ad_victoriam.libtex.admin.activities.users;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.adapters.UserAdapter;
import com.ad_victoriam.libtex.admin.models.AdminBook;
import com.ad_victoriam.libtex.common.models.User;
import com.ad_victoriam.libtex.admin.utils.TopAppBarAdmin;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    private UserAdapter userAdapter;

    private SearchView searchView;
    private RecyclerView recyclerView;

    private final List<User> initUsers = new ArrayList<>();
    private List<User> users = new ArrayList<>();

    private String searchQueryText = "";
    private boolean searchFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);

        databaseReference = FirebaseDatabase
                .getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        TopAppBarAdmin.get().setChildMode(this, topAppBar);
        TopAppBarAdmin.get().setTitleMode(this, topAppBar, "Users");
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.addNew) {
                    addNewUser();
                }
                return false;
            }
        });
        searchFilter = false;
        setSearchView();
        userAdapter = new UserAdapter(this, users);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);

        getUsers();
    }

    private void setSearchView() {
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                executeSearchQueryFilter(newText);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty() && searchFilter) {
                    executeSearchQueryFilter(newText);
                }
                return true;
            }
        });
    }

    private void executeSearchQueryFilter(String newText) {
        searchQueryText = newText;
        users.clear();
        List<User> filteredUsers = new ArrayList<>();

        if (newText.isEmpty()) {
            searchFilter = false;
            users.addAll(initUsers);
        } else {
            searchFilter = true;
            for (User user: initUsers) {
                if (isNewTextSubstringOfUserDetails(user, newText)) {
                    filteredUsers.add(user);
                }
            }
            users.addAll(filteredUsers);
        }
        userAdapter.notifyDataSetChanged();
    }

    private boolean isNewTextSubstringOfUserDetails(User user, String newText) {
        return user.getFullName().toLowerCase().contains(newText.toLowerCase()) ||
                user.getEmail().toLowerCase().contains(newText.toLowerCase()) ||
                user.getPhoneNumber().toLowerCase().contains(newText.toLowerCase());
    }

    private void getUsers() {
        Query query = databaseReference
                .child(this.getString(R.string.n_users));
        query
                .get()
                .addOnSuccessListener(task -> {
                    for (DataSnapshot dataSnapshot: task.getChildren()) {

                        User user = dataSnapshot.getValue(User.class);

                        if (user != null) {
                            user.setUid(dataSnapshot.getKey());

                            if (!initUsers.contains(user)) {
                                initUsers.add(user);
                            }
                            if (!searchFilter) {
                                users.add(user);
                                userAdapter.notifyItemInserted(users.size() - 1);
                            } else {
                                executeSearchQueryFilter(searchQueryText);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("GET_USERS_DB", e.toString());
                });
    }


    public void addNewUser() {
        startActivity(new Intent(this, AddUserActivity.class));
    }
}

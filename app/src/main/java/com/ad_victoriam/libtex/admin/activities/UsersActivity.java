package com.ad_victoriam.libtex.admin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.adapters.UserAdapter;
import com.ad_victoriam.libtex.common.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    private UserAdapter userAdapter;

    private RecyclerView recyclerView;

    private final List<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseReference = FirebaseDatabase.getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        final FloatingActionButton bAddUser = findViewById(R.id.bAddUser);
        bAddUser.setOnClickListener(this::addUser);

        userAdapter = new UserAdapter(this, users);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);

        attachDatabaseUsersListener();
    }

    private void attachDatabaseUsersListener() {
        databaseReference.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                User user = snapshot.getValue(User.class);

                if (user != null) {
                    user.setUid(snapshot.getKey());

                    if (!users.contains(user)) {
                        users.add(user);
                    }

                    userAdapter.notifyItemChanged(users.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                User user = snapshot.getValue(User.class);

                if (user != null) {
                    user.setUid(snapshot.getKey());

                    int indexOfChangedUser = -1;
                    for (User u: users) {
                        if (u.getUid().equals(user.getUid())) {
                            indexOfChangedUser = users.indexOf(u);
                            users.set(indexOfChangedUser, user);
                        }
                    }

                    if (indexOfChangedUser != -1) {
                        userAdapter.notifyItemChanged(indexOfChangedUser);
                    } else {
                        userAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                if (user != null) {
                    user.setUid(snapshot.getKey());

                    int indexOfRemovedUser = -1;
                    for (User u: users) {
                        if (u.getUid().equals(user.getUid())) {
                            indexOfRemovedUser = users.indexOf(u);
                            users.remove(indexOfRemovedUser);
                        }
                    }

                    if (indexOfRemovedUser != -1) {
                        userAdapter.notifyItemChanged(indexOfRemovedUser);
                    } else {
                        userAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void addUser(View view) {
        startActivity(new Intent(this, AddUserActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:

                break;
        }
        return true;
    }
}

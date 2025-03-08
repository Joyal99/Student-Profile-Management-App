package com.example.techassignment_2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements InsertProfileDialogFragment.InsertProfileListener { // ✅ Implemented listener

    private DatabaseHelper dbHelper;
    private ListView profileListView;
    private TextView profileCountText;
    private ArrayAdapter<String> adapter;
    private ArrayList<Profile> profileList;
    private boolean sortByName = true; // Default sorting mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ✅ Initialize UI
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        profileListView = findViewById(R.id.profileListView);
        profileCountText = findViewById(R.id.profileCountText);
        FloatingActionButton fab = findViewById(R.id.fab);

        // ✅ Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // ✅ Load profiles from database
        loadProfiles();

        // ✅ Handle profile click -> Open ProfileActivity
        profileListView.setOnItemClickListener((parent, view, position, id) -> {
            Profile selectedProfile = profileList.get(position);
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("PROFILE_ID", selectedProfile.getId());
            startActivityForResult(intent, 1); // ✅ Request code 1 for profile activity
        });

        // ✅ Handle FAB click -> Open InsertProfileDialogFragment
        fab.setOnClickListener(v -> {
            InsertProfileDialogFragment dialog = new InsertProfileDialogFragment();
            dialog.show(getSupportFragmentManager(), "InsertProfileDialogFragment");
        });
    }

    // ✅ Load profiles from database
    private void loadProfiles() {
        profileList = dbHelper.getAllProfiles(sortByName);

        if (profileList == null || profileList.isEmpty()) {
            profileCountText.setText("No profiles found");
            profileListView.setAdapter(null);
            return;
        }

        // ✅ Sort based on current mode
        if (sortByName) {
            Collections.sort(profileList, Comparator.comparing(Profile::getSurname));
        } else {
            Collections.sort(profileList, Comparator.comparingInt(Profile::getId));
        }

        // ✅ Update UI
        updateListView();
    }

    private void updateListView() {
        ArrayList<String> displayList = new ArrayList<>();
        for (int i = 0; i < profileList.size(); i++) {
            Profile profile = profileList.get(i);
            String displayText = (sortByName)
                    ? (i + 1) + ". " + profile.getSurname() + ", " + profile.getName()
                    : (i + 1) + ". ID: " + profile.getId();
            displayList.add(displayText);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        profileListView.setAdapter(adapter);
        profileCountText.setText(profileList.size() + " Profiles, " + (sortByName ? "By Surname" : "By ID"));
    }

    // ✅ Callback for Profile Inserted (Prevents Crash!)
    @Override
    public void onProfileInserted() {
        loadProfiles(); // Refresh the list after inserting a new profile
        Toast.makeText(this, "Profile Added Successfully!", Toast.LENGTH_SHORT).show();
    }

    // ✅ Refresh the list when returning from ProfileActivity (after deleting a profile)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("PROFILE_DELETED", false)) {
                loadProfiles();  // ✅ Refresh the list immediately after deletion
            }
        }
    }

    // ✅ Handle toolbar menu clicks
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_sort) {
            // Toggle sorting mode
            sortByName = !sortByName;
            item.setTitle(sortByName ? "Sort by ID" : "Sort by Name");
            loadProfiles();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

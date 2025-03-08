package com.example.techassignment_2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private TextView nameTextView, surnameTextView, idTextView, gpaTextView, profileCreatedTextView; // ✅ Added profileCreatedTextView
    private ListView accessListView;
    private Button deleteButton;
    private DatabaseHelper dbHelper;
    private int profileId;
    private boolean isDeleting = false; // ✅ Prevents logging "Closed" when deleting

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // ✅ Initialize UI Elements
        nameTextView = findViewById(R.id.nameTextView);
        surnameTextView = findViewById(R.id.surnameTextView);
        idTextView = findViewById(R.id.idTextView);
        gpaTextView = findViewById(R.id.gpaTextView);
        profileCreatedTextView = findViewById(R.id.profileCreatedTextView); // ✅ Fix: Initialize profileCreatedTextView
        accessListView = findViewById(R.id.accessListView);
        deleteButton = findViewById(R.id.deleteButton);
        dbHelper = new DatabaseHelper(this);

        // ✅ Set up Toolbar with Back Button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Profile Activity");
        }

        // ✅ Get Profile ID from Intent
        profileId = getIntent().getIntExtra("PROFILE_ID", -1);
        if (profileId == -1) {
            Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ✅ Load Profile Data & Access History
        loadProfileData();
        loadAccessHistory();

        // ✅ Log Profile Open Event
        logAccessEvent("Opened");

        // ✅ Handle Delete Button
        deleteButton.setOnClickListener(v -> deleteProfile());
    }

    // ✅ Handle Back Arrow Click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Goes back to MainActivity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadProfileData() {
        Profile profile = dbHelper.getProfile(profileId);
        if (profile != null) {
            surnameTextView.setText("Surname: " + profile.getSurname());
            nameTextView.setText("Name: " + profile.getName());
            idTextView.setText("ID: " + profile.getId());
            gpaTextView.setText("GPA: " + profile.getGpa());

            // ✅ Get the profile creation timestamp from the database
            String createdTimestamp = dbHelper.getProfileCreatedTimestamp(profileId);
            profileCreatedTextView.setText("Profile created: " + createdTimestamp);
        }
    }

    private void loadAccessHistory() {
        ArrayList<String> accessHistory = dbHelper.getAccessHistory(profileId);
        AccessHistoryAdapter adapter = new AccessHistoryAdapter(this, accessHistory);
        accessListView.setAdapter(adapter);
    }

    private void logAccessEvent(String eventType) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd @ HH:mm:ss", Locale.getDefault()).format(new Date());
        dbHelper.addAccessEntry(profileId, eventType); // ✅ Timestamp should already have "@"
    }

    private void deleteProfile() {
        isDeleting = true; // ✅ Prevents logging "Closed" when deleting

        logAccessEvent("Profile Deleted");
        dbHelper.deleteProfile(profileId);
        Toast.makeText(this, "Profile Deleted", Toast.LENGTH_SHORT).show();

        // ✅ Send result back to MainActivity to refresh list
        Intent resultIntent = new Intent();
        resultIntent.putExtra("PROFILE_DELETED", true);
        setResult(RESULT_OK, resultIntent);

        finish(); // Go back to MainActivity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isDeleting) { // ✅ Only log "Closed" if NOT deleting
            logAccessEvent("Closed");
        }
    }
}

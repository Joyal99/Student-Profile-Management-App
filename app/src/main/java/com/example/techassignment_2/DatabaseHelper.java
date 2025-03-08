package com.example.techassignment_2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "profiles.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_PROFILE = "Profile";
    private static final String TABLE_ACCESS = "Access";

    // Profile Table Columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_SURNAME = "surname";
    private static final String COLUMN_GPA = "gpa";

    // Access Table Columns
    private static final String COLUMN_ACCESS_ID = "access_id";
    private static final String COLUMN_PROFILE_ID = "profile_id";
    private static final String COLUMN_ACCESS_TYPE = "access_type";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    // Create Table Queries
    private static final String CREATE_PROFILE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_PROFILE + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_SURNAME + " TEXT NOT NULL, " +
                    COLUMN_GPA + " REAL NOT NULL)";

    private static final String CREATE_ACCESS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ACCESS + " (" +
                    COLUMN_ACCESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PROFILE_ID + " INTEGER, " +
                    COLUMN_ACCESS_TYPE + " TEXT NOT NULL, " +
                    COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(" + COLUMN_PROFILE_ID + ") REFERENCES " + TABLE_PROFILE + "(" + COLUMN_ID + ") ON DELETE CASCADE)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROFILE_TABLE);
        db.execSQL(CREATE_ACCESS_TABLE);
        Log.d("DatabaseHelper", "Tables created successfully.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "Upgrading database...");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCESS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        onCreate(db);
    }

    // Insert New Profile
    public boolean addProfile(Profile profile) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, profile.getId());
        values.put(COLUMN_NAME, profile.getName());
        values.put(COLUMN_SURNAME, profile.getSurname());
        values.put(COLUMN_GPA, profile.getGpa());

        long result = db.insert(TABLE_PROFILE, null, values);
        db.close();

        if (result != -1) {
            addAccessEntry(profile.getId(), "Created");
            return true;
        }
        return false;
    }

    // Get Profile by ID
    public Profile getProfile(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Profile profile = null;
        Cursor cursor = db.query(TABLE_PROFILE, new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_SURNAME, COLUMN_GPA},
                COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            profile = new Profile(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getFloat(3)
            );
            cursor.close();
        }
        db.close();
        return profile;
    }

    // Get All Profiles (Handles Sorting)
    public ArrayList<Profile> getAllProfiles(boolean sortByName) {
        return sortByName ? getAllProfilesByName() : getAllProfilesById();
    }

    // Get All Profiles (Ordered by Name)
    public ArrayList<Profile> getAllProfilesByName() {
        ArrayList<Profile> profiles = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROFILE + " ORDER BY " + COLUMN_SURNAME + " ASC", null);

        if (cursor.moveToFirst()) {
            do {
                profiles.add(new Profile(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getFloat(3)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return profiles;
    }

    // Get All Profiles (Ordered by ID)
    public ArrayList<Profile> getAllProfilesById() {
        ArrayList<Profile> profiles = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROFILE + " ORDER BY " + COLUMN_ID + " ASC", null);

        if (cursor.moveToFirst()) {
            do {
                profiles.add(new Profile(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getFloat(3)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return profiles;
    }

    // Check if ID already exists
    public boolean isIdExists(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_PROFILE + " WHERE " + COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Delete Profile and its Access History
    public void deleteProfile(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete all access history for the profile
        db.delete(TABLE_ACCESS, COLUMN_PROFILE_ID + "=?", new String[]{String.valueOf(id)});

        // Delete the profile itself
        db.delete(TABLE_PROFILE, COLUMN_ID + "=?", new String[]{String.valueOf(id)});

        db.close();
    }


    // Add Access Entry
    public void addAccessEntry(int profileId, String accessType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROFILE_ID, profileId);
        values.put(COLUMN_ACCESS_TYPE, accessType);
        values.put(COLUMN_TIMESTAMP, new SimpleDateFormat("yyyy-MM-dd @ HH:mm:ss", Locale.getDefault()).format(new Date())); // Store timestamp correctly
        db.insert(TABLE_ACCESS, null, values);
        db.close();
    }

    public String getProfileCreatedTimestamp(int profileId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_TIMESTAMP +
                        " FROM " + TABLE_ACCESS +
                        " WHERE " + COLUMN_PROFILE_ID + "=? AND " + COLUMN_ACCESS_TYPE + "='Created' " +
                        " ORDER BY " + COLUMN_TIMESTAMP + " ASC LIMIT 1",
                new String[]{String.valueOf(profileId)});

        String createdTimestamp = "Unknown";
        if (cursor.moveToFirst()) {
            createdTimestamp = cursor.getString(0); // Directly return the timestamp
        }
        cursor.close();
        return createdTimestamp;
    }


    // Get Access History for a Profile
    public ArrayList<String> getAccessHistory(int profileId) {
        ArrayList<String> accessHistory = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_ACCESS_TYPE + ", " + COLUMN_TIMESTAMP +
                        " FROM " + TABLE_ACCESS +
                        " WHERE " + COLUMN_PROFILE_ID + "=? ORDER BY " + COLUMN_TIMESTAMP + " DESC",
                new String[]{String.valueOf(profileId)});

        if (cursor.moveToFirst()) {
            do {
                String eventType = cursor.getString(0);
                String timestamp = cursor.getString(1);

                // Emojis based on event type
                String emoji;
                switch (eventType) {
                    case "Created":
                        emoji = "🏁";
                        break;
                    case "Opened":
                        emoji = "🟢";
                        break;
                    case "Closed":
                        emoji = "🔴";
                        break;
                    case "Profile Deleted":
                        emoji = "❌";
                        break;
                    default:
                        emoji = "ℹ️";
                        break;
                }

                accessHistory.add(emoji + " " + timestamp + " " + eventType);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return accessHistory;
    }

}



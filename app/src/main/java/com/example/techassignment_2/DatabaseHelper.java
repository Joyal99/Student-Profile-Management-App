package com.example.techassignment_2;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper  {

    private static final String DATABASE_NAME = "profiles.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_PROFILES = "profiles";
    private static final String TABLE_ACCESS = "access";

    // Table Columns for Profiles
    private static final String COLUMN_ID = "profile_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_SURNAME = "surname";
    private static final String COLUMN_GPA = "gpa";
    private static final String COLUMN_CREATED_AT = "created_at";

    // Table Columns for Access
    private static final String COLUMN_ACCESS_ID = "access_id";
    private static final String COLUMN_ACCESS_PROFILE_ID = "profile_id";
    private static final String COLUMN_ACCESS_TYPE = "access_type";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    // Creation of Profiles Table
    public void onCreate(SQLiteDatabase db){
        String createProfilesTable = "CREATE TABLE " + TABLE_PROFILES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_SURNAME + " TEXT NOT NULL, " +
                COLUMN_GPA + " REAL CHECK (" + COLUMN_GPA + " BETWEEN 0 AND 4.3), " +
                COLUMN_CREATED_AT + " TEXT NOT NULL);";
        db.execSQL(createProfilesTable);

    // Creation of Access Table
        String createAccessTable = "CREATE TABLE " + TABLE_ACCESS + " (" +
                COLUMN_ACCESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ACCESS_PROFILE_ID + " INTEGER, " +
                COLUMN_ACCESS_TYPE + " TEXT, " +
                COLUMN_TIMESTAMP + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_ACCESS_PROFILE_ID + ") REFERENCES " + TABLE_PROFILES + "(" + COLUMN_ID + "));";
        db.execSQL(createAccessTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCESS);
        onCreate(db);
    }


}

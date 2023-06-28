package com.example.opticalcharacterrecognitionapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class UserDBHandler extends SQLiteOpenHelper {

    public UserDBHandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE users (uid INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT, password TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    public boolean addUser(User user){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("name", user.getName());
            values.put("email", user.getEmail());
            values.put("password", user.getPassword());
            long k = db.insert("users", null, values);
            db.close();
            return k != -1;
        }
        catch(Exception e){
            Log.e("myTag", "" + e);
        }
        return false;
    }


    public boolean checkUser(String email, String password){
        SQLiteDatabase db;
        try {
            db = this.getReadableDatabase();
            Cursor cursor = db.query("users", new String[]{"email"}, "email=? and password=?", new String[]{email, password}, null, null, null);
            boolean t = cursor != null && cursor.moveToFirst();
            if(cursor != null)
                cursor.close();
            db.close();
            return t;
        }
        catch(Exception e){
            Log.e("myTag", "" + e);
        }
        return false;
    }

    public boolean isUserPresent(String email){
        SQLiteDatabase db;
        try {
            db = this.getReadableDatabase();
            Cursor cursor = db.query("users", new String[] {"email"}, "email=?", new String[]{email}, null, null, null);
            boolean t = cursor != null && cursor.moveToFirst();
            if(cursor != null)
                cursor.close();
            db.close();
            return t;
        }
        catch(Exception e){
            Log.e("myTag", "" + e);
        }
        return false;
    }
}

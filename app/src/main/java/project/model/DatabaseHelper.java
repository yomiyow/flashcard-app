package project.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    public static final String TABLE_FLASHCARDS = "flashcards";
    public static final String COLUMN_FLASHCARD_ID = "flashcard_id";
    public static final String COLUMN_TERM = "term";
    public static final String COLUMN_DEFINITION = "definition";

    public DatabaseHelper(@Nullable Context context) {
        super(context, "flashcard.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create users table
        final String CREATE_TABLE_USERS = String.format(
                "CREATE TABLE %s (" +
                    "%s TEXT PRIMARY KEY, " +
                    "%s TEXT UNIQUE, " +
                    "%s TEXT" +
                ")",
                TABLE_USERS, COLUMN_EMAIL,
                COLUMN_USERNAME, COLUMN_PASSWORD
        );

        // create flashcards table
        final String CREATE_TABLE_FLASHCARD = String.format(
                "CREATE TABLE %s (" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "%s TEXT, " +
                    "%s TEXT, " +
                    "%s TEXT, " +
                    "FOREIGN KEY (%s) REFERENCES users(%s)" +
                ")",
                TABLE_FLASHCARDS, COLUMN_FLASHCARD_ID,
                COLUMN_EMAIL, COLUMN_TERM,
                COLUMN_DEFINITION, COLUMN_EMAIL,
                COLUMN_EMAIL
        );

        // query to database
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_FLASHCARD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    // account username validation
    public boolean isUsernameTaken(UserModel userModel) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String query = "SELECT 1 FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?";

            Cursor cursor = db.rawQuery(query, new String[]{userModel.getUsername()});
            boolean exist = (cursor.getCount() > 0);
            cursor.close();

            return exist;
        }
    }

    public boolean registerUser(UserModel userModel) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_EMAIL, userModel.getEmail());
            cv.put(COLUMN_USERNAME, userModel.getUsername());
            cv.put(COLUMN_PASSWORD, userModel.getPassword());
            long insertResult = db.insert(TABLE_USERS, null, cv);

            return (insertResult != -1);
        }
    }

    public boolean loginUser(UserModel userModel) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String query = String.format(
                    "SELECT 1 FROM %s " +
                    "WHERE email = ? AND password = ?",
                    TABLE_USERS
            );
            Cursor cursor = db.rawQuery(
                    query,
                    new String[]{userModel.getEmail(), userModel.getPassword()}
            );
            boolean exist = (cursor.getCount() > 0);
            cursor.close();

            return exist;
        }
    }
}

package project.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    public static final String TABLE_FLASHCARDS = "flashcards";
    public static final String COLUMN_FLASHCARD_ID = "flashcard_id";
    public static final String COLUMN_TITLE = "title";

    public static final String TABLE_TERM_DEFINITION = "terms_definitions";
    public static final String COLUMN_TERM_DEFINITION_ID = "term_definition_id";
    public static final String COLUMN_TERM = "term";
    public static final String COLUMN_DEFINITION = "definition";
    public static final String COLUMN_NO_OF_TERMS = "no_of_terms";

    public DatabaseHelper(@Nullable Context context) {
        super(context, "flashcard.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create users table
        final String CREATE_TABLE_USERS =
                "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_EMAIL + " TEXT PRIMARY KEY, " +
                    COLUMN_USERNAME + " TEXT UNIQUE, " +
                    COLUMN_PASSWORD + " TEXT" +
                ")";


        // create term_definition table
        final String CREATE_TABLE_TERM_DEFINITION =
                "CREATE TABLE " + TABLE_TERM_DEFINITION + " (" +
                    COLUMN_TERM_DEFINITION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_FLASHCARD_ID + " INTEGER, " +
                    COLUMN_TERM + " TEXT, " +
                    COLUMN_DEFINITION + " TEXT, " +
                    "FOREIGN KEY (" + COLUMN_FLASHCARD_ID + ") REFERENCES flashcards(" + COLUMN_FLASHCARD_ID + ")" +
                ")";

        // create flashcards table
        final String CREATE_TABLE_FLASHCARD =
                "CREATE TABLE " + TABLE_FLASHCARDS + " (" +
                    COLUMN_FLASHCARD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT" +
                ")";

        // query to database
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_FLASHCARD);
        db.execSQL(CREATE_TABLE_TERM_DEFINITION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /*====================================================================================
                                Signup Activity Queries
    ====================================================================================*/

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

    /*====================================================================================
                            Login Activity Queries
    ====================================================================================*/

    public boolean loginUser(UserModel userModel) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String query = "SELECT 1 FROM " + TABLE_USERS + " WHERE email = ? AND password = ?";
            Cursor cursor = db.rawQuery(
                    query,
                    new String[]{userModel.getEmail(), userModel.getPassword()}
            );
            boolean exist = (cursor.getCount() > 0);
            cursor.close();

            return exist;
        }
    }

    /*====================================================================================
                                Create Activity Queries
    ====================================================================================*/

    public boolean insertFlashcard(FlashcardModel flashcard) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues flashcardValues = new ContentValues();
            flashcardValues.put(COLUMN_TITLE, flashcard.getTitle());

            long flashCardId = db.insert(TABLE_FLASHCARDS, null, flashcardValues);

            for (FlashcardModel.TermDefinition termDefinition : flashcard.getTermDefinitions()) {
                ContentValues termDefValues = new ContentValues();
                termDefValues.put(COLUMN_FLASHCARD_ID, flashCardId);
                termDefValues.put(COLUMN_TERM, termDefinition.getTerm());
                termDefValues.put(COLUMN_DEFINITION, termDefinition.getDefinition());

                db.insert(TABLE_TERM_DEFINITION, null, termDefValues);
            }

            return flashCardId != -1;
        }
    }

    /*====================================================================================
                                Home Activity Queries
    ====================================================================================*/

    public List<FlashcardModel> getFlashcardTitleAndNumberOfTerms() {
        List<FlashcardModel> returnList = new ArrayList<>();

        try(SQLiteDatabase db = this.getReadableDatabase()) {
            String query =
                    "SELECT f." + COLUMN_TITLE + ", COUNT(" + COLUMN_TERM + ") AS " + COLUMN_NO_OF_TERMS + " " +
                    "FROM " + TABLE_TERM_DEFINITION + " td " +
                    "INNER JOIN " + TABLE_FLASHCARDS + " f " +
                        "ON td." + COLUMN_FLASHCARD_ID + " = " + "f." + COLUMN_FLASHCARD_ID + " " +
                    "GROUP BY f." + COLUMN_TITLE;
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                    int numberOfTerms = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NO_OF_TERMS));
                    returnList.add(new FlashcardModel(title, numberOfTerms));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return returnList;
    }
}

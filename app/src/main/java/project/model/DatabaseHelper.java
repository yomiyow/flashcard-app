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
                    "SELECT " +
                         "f." + COLUMN_TITLE + ", " +
                         "COUNT(" + COLUMN_TERM + ") AS " + COLUMN_NO_OF_TERMS + ", " +
                         "f." + COLUMN_FLASHCARD_ID + " " +
                    "FROM " + TABLE_TERM_DEFINITION + " td " +
                    "INNER JOIN " + TABLE_FLASHCARDS + " f " +
                        "ON td." + COLUMN_FLASHCARD_ID + " = " + "f." + COLUMN_FLASHCARD_ID + " " +
                    "GROUP BY f." + COLUMN_FLASHCARD_ID;
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    int flashcardId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FLASHCARD_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                    int numberOfTerms = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NO_OF_TERMS));
                    returnList.add(new FlashcardModel(flashcardId, title, numberOfTerms));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return returnList;
    }

    public boolean deleteFlashcard(int flashcardId) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.beginTransaction();
            try {
                int flashcardRowsAffected = db.delete(
                        TABLE_FLASHCARDS,
                        COLUMN_FLASHCARD_ID + " = ?",
                        new String[] { String.valueOf(flashcardId) }
                );

                int termDefinitionRowsAffected = db.delete(
                        TABLE_TERM_DEFINITION,
                        COLUMN_FLASHCARD_ID + " = ?",
                        new String[] { String.valueOf(flashcardId) }
                );

                if (flashcardRowsAffected > 0 && termDefinitionRowsAffected > 0) {
                    db.setTransactionSuccessful();
                    return true;
                }

                return false;
            } finally {
                db.endTransaction();
            }
        }
    }

    /*====================================================================================
                       FlashcardOpenActivity/EditActivity Queries
    ====================================================================================*/

    public List<FlashcardModel.TermDefinition> getFlashcardTermAndDefinition(int flashcardId) {
        List<FlashcardModel.TermDefinition> returnList = new ArrayList<>();

        try(SQLiteDatabase db = this.getReadableDatabase()) {
            String query =
                    "SELECT " + COLUMN_TERM_DEFINITION_ID + ", " + COLUMN_TERM + ", " + COLUMN_DEFINITION + " " +
                    "FROM " + TABLE_TERM_DEFINITION + " td " +
                    "INNER JOIN " + TABLE_FLASHCARDS + " f " +
                        "ON td." + COLUMN_FLASHCARD_ID + " = " + "f." + COLUMN_FLASHCARD_ID + " " +
                    "WHERE f." + COLUMN_FLASHCARD_ID + "= ?";
            Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(flashcardId)});
            if (cursor.moveToFirst()) {
                do {
                    int termDefinitionId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TERM_DEFINITION_ID));
                    String term = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TERM));
                    String definition = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEFINITION));
                    returnList.add(new FlashcardModel.TermDefinition(termDefinitionId, term, definition));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return returnList;
    }

    /*====================================================================================
                                    EditActivity Queries
    ====================================================================================*/

    public boolean updateFlashcard(FlashcardModel flashcard) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.beginTransaction();
            try {
                // Update the flashcard title
                ContentValues flashcardValues = new ContentValues();
                flashcardValues.put(COLUMN_TITLE, flashcard.getTitle());
                int rowsAffected = db.update(
                        TABLE_FLASHCARDS,
                        flashcardValues,
                        COLUMN_FLASHCARD_ID + " = ?",
                        new String[] { String.valueOf(flashcard.getFlashcardId()) }
                );

                if (rowsAffected == 0) {
                    return false;
                }

                // Insert or update the term definitions
                for (FlashcardModel.TermDefinition termDefinition : flashcard.getTermDefinitions()) {
                    ContentValues termDefValues = new ContentValues();
                    termDefValues.put(COLUMN_FLASHCARD_ID, flashcard.getFlashcardId());
                    termDefValues.put(COLUMN_TERM, termDefinition.getTerm());
                    termDefValues.put(COLUMN_DEFINITION, termDefinition.getDefinition());

                    // Update existing term definition
                    int termDefRowsAffected = db.update(
                            TABLE_TERM_DEFINITION,
                            termDefValues,
                            COLUMN_FLASHCARD_ID + " = ? AND " + COLUMN_TERM_DEFINITION_ID + " = ?",
                            new String[] {
                                    String.valueOf(flashcard.getFlashcardId()),
                                    String.valueOf(termDefinition.getTermDefinitionId())
                            }
                    );

                    // if data not exist, insert it
                    if (termDefRowsAffected == 0) {
                        db.insert(TABLE_TERM_DEFINITION, null, termDefValues);
                    }
                }

                db.setTransactionSuccessful();
                return true;
            } finally {
                db.endTransaction();
            }
        }
    }

    public boolean deleteTermDefinition(int termDefinitionId) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            int rowsAffected = db.delete(
                    TABLE_TERM_DEFINITION,
                    COLUMN_TERM_DEFINITION_ID + " = ?",
                    new String[] { String.valueOf(termDefinitionId) }
            );

            return rowsAffected > 0;
        }
    }
}

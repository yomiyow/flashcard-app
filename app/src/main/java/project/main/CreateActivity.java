package project.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import project.authentication.R;
import project.model.DatabaseHelper;
import project.model.FlashcardModel;

public class CreateActivity extends AppCompatActivity {

    private LinearLayout flashcardContainer;
    private EditText flashcardTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.create);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.create_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        flashcardContainer = findViewById(R.id.flashcard_container);
        flashcardTitle = findViewById(R.id.flashcard_title);
        ImageButton previousAct = findViewById(R.id.previous_activity);
        FloatingActionButton addBtn = findViewById(R.id.add_btn);
        ImageButton saveBtn = findViewById(R.id.save_flashcard);

        previousAct.setOnClickListener((v) -> returnToPreviousActivity());
        addBtn.setOnClickListener((v) -> addNewEmptyFlashcardItem());
        saveBtn.setOnClickListener((v) -> {
            FlashcardModel flashcardData = collectFlashcardsData();
            try (DatabaseHelper db = new DatabaseHelper(CreateActivity.this)) {
                boolean success = db.insertFlashcard(flashcardData);
                if (success) {
                    Toast.makeText(this, "Flashcard saved successfully.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void returnToPreviousActivity() {
        Intent intent = new Intent(CreateActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    // create new flashcard item using flashcard_item.xml
    private void addNewEmptyFlashcardItem() {
        LayoutInflater inflater = LayoutInflater.from(CreateActivity.this);
        View cardView = inflater.inflate(R.layout.flashcard_item, flashcardContainer, false);
        flashcardContainer.addView(cardView);
    }

    private FlashcardModel collectFlashcardsData() {
        FlashcardModel flashcard = new FlashcardModel();
        List<FlashcardModel.TermDefinition> termDefinitions = new ArrayList<>();

        int flashcardItemCount = flashcardContainer.getChildCount();
        for (int i = 0; i < flashcardItemCount; i++) {
            View cardView = flashcardContainer.getChildAt(i);
            if (cardView instanceof CardView) {
                LinearLayout linearLayout = (LinearLayout) ((CardView) cardView).getChildAt(0);

                EditText term = (EditText) linearLayout.getChildAt(0);
                EditText definition = (EditText) linearLayout.getChildAt(2);

                termDefinitions.add(flashcard.createTermDefinitions(
                        term.getText().toString(),
                        definition.getText().toString()
                ));
            }
        }

        flashcard.setTitle(flashcardTitle.getText().toString());
        flashcard.setTermDefinitions(termDefinitions);

        return flashcard;
    }
}
package project.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

import project.authentication.R;
import project.model.DatabaseHelper;
import project.model.FlashcardModel;

public class FlashcardOpenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.flashcard_open);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.flashcard_open_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton previousAct = findViewById(R.id.previous_activity);
        previousAct.setOnClickListener((v) -> returnToHomeActivity());
        renderFlashcardItems();
    }

    private void returnToHomeActivity() {
        Intent intent = new Intent(FlashcardOpenActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private void renderFlashcardItems() {
        // Set title
        TextView details = findViewById(R.id.details);
        Intent intent = getIntent();
        String title = intent.getStringExtra("flashcardTitle");
        String numberOfTerms = intent.getStringExtra("numberOfTerms");
        details.setText(title + " | " + numberOfTerms);

        // Generate flashcard item
        LinearLayout flashcardItemContainer = findViewById(R.id.flashcard_item_container);
        LayoutInflater inflater = LayoutInflater.from(FlashcardOpenActivity.this);
        try (DatabaseHelper dbHelper = new DatabaseHelper(FlashcardOpenActivity.this)) {
            List<FlashcardModel.TermDefinition> termDefinitionList = dbHelper.getFlashcardTermAndDefinition(title);

            for (var termDefinition : termDefinitionList) {
                View flashcardItem = inflater.inflate(R.layout.flascard_item, flashcardItemContainer, false);
                TextView termTV = flashcardItem.findViewById(R.id.term);
                termTV.setText(termDefinition.getTerm());

                flashcardItemContainer.addView(flashcardItem);
            }
        }
    }
}
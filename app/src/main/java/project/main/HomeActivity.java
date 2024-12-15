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

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton addBtn = findViewById(R.id.new_flashcard);
        addBtn.setOnClickListener((v) -> navigateToCreateActivity());
        renderFlashcards();
    }

    private void navigateToCreateActivity() {
        Intent intent = new Intent(HomeActivity.this, CreateActivity.class);
        startActivity(intent);
    }

    private void openFlashcard(TextView title, TextView numberOfTerms) {
        Intent intent = new Intent(HomeActivity.this, FlashcardOpenActivity.class);
        intent.putExtra("flashcardTitle", title.getText().toString());
        intent.putExtra("numberOfTerms", numberOfTerms.getText().toString());
        startActivity(intent);
    }

    private void renderFlashcards() {
        LinearLayout flashcardContainer = findViewById(R.id.flashcard_container);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);

        try (DatabaseHelper dbHelper = new DatabaseHelper(HomeActivity.this)) {
            List<FlashcardModel> flashcardList = dbHelper.getFlashcardTitleAndNumberOfTerms();

            for (var flashchard : flashcardList) {
                View flashcardPreview = inflater.inflate(R.layout.flashcard_preview, flashcardContainer, false);
                TextView titleTV = flashcardPreview.findViewById(R.id.title);
                titleTV.setText(flashchard.getTitle());
                TextView numberOfTermsTV = flashcardPreview.findViewById(R.id.no_of_terms);
                numberOfTermsTV.setText(flashchard.getNumberOfTerms() + " terms");

                flashcardContainer.addView(flashcardPreview);
                flashcardPreview.setOnClickListener((v) -> openFlashcard(titleTV, numberOfTermsTV));
            }
        }
    }
}
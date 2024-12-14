package project.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import project.authentication.R;

public class CreateActivity extends AppCompatActivity {

    private LinearLayout flashcardContainer;

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
        FloatingActionButton addBtn = findViewById(R.id.add_btn);
        ImageButton previousAct = findViewById(R.id.previous_activity);

        previousAct.setOnClickListener((v) -> returnToPreviousActivity());
        addBtn.setOnClickListener((v) -> addNewFlashcardItem());
    }


    private void returnToPreviousActivity() {
        Intent intent = new Intent(CreateActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    // create new flashcard item using flashcard_item.xml
    private void addNewFlashcardItem() {
        LayoutInflater inflater = LayoutInflater.from(CreateActivity.this);
        View cardView = inflater.inflate(R.layout.flashcard_item, flashcardContainer, false);
        flashcardContainer.addView(cardView);
    }
}
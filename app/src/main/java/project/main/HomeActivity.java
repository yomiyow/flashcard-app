package project.main;

import android.content.Context;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import project.authentication.R;
import project.model.DatabaseHelper;
import project.model.FlashcardAdapter;
import project.model.FlashcardModel;

public class HomeActivity extends AppCompatActivity {

    Context context;

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

        context = HomeActivity.this;
        ImageButton addBtn = findViewById(R.id.new_flashcard);
        addBtn.setOnClickListener((v) -> navigateToCreateActivity());
        renderFlashcardsPreview();
    }

    private void navigateToCreateActivity() {
        Intent intent = new Intent(context, CreateActivity.class);
        startActivity(intent);
    }

    private void renderFlashcardsPreview() {
        RecyclerView recyclerView = findViewById(R.id.flashcard_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        try (DatabaseHelper dbHelper = new DatabaseHelper(context)) {
            List<FlashcardModel> flashcardList = dbHelper.getFlashcardTitleAndNumberOfTerms();
            FlashcardAdapter adapter = new FlashcardAdapter(context, flashcardList);
            recyclerView.setAdapter(adapter);
        }
    }
}
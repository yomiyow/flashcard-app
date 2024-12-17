package project.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

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
import project.model.FlashcardRecyclerAdapter;
import project.model.FlashcardModel;

public class HomeActivity extends AppCompatActivity {

    private Context context;
    private ImageButton addBtn;

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

        initInstanceVariables();
        addBtn.setOnClickListener((v) -> navigateToCreateActivity());
        renderFlashcardsPreview();
    }

    private void initInstanceVariables() {
        context = HomeActivity.this;
        addBtn = findViewById(R.id.new_flashcard);
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
            FlashcardRecyclerAdapter adapter = new FlashcardRecyclerAdapter(context, flashcardList);
            recyclerView.setAdapter(adapter);
        }
    }
}
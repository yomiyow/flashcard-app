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
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

import project.authentication.R;
import project.model.DatabaseHelper;
import project.model.FlashcardModel;
import project.model.FlashcardPagerAdapter;

public class FlashcardOpenActivity extends AppCompatActivity {

    private Context context;
    private ImageButton previousAct;

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

        initInstanceVariables();
        previousAct.setOnClickListener((v) -> returnToHomeActivity());
        renderFlashcardItems();
    }

    private void initInstanceVariables() {
        context = FlashcardOpenActivity.this;
        previousAct = findViewById(R.id.previous_activity);
    }

    private void returnToHomeActivity() {
        Intent intent = new Intent(context, HomeActivity.class);
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
        ViewPager2 viewPager = findViewById(R.id.flashcard_view_pager);
        try (DatabaseHelper dbHelper = new DatabaseHelper(context)) {
            List<FlashcardModel.TermDefinition> termDefinitionList = dbHelper.getFlashcardTermAndDefinition(title);
            FlashcardPagerAdapter adapter = new FlashcardPagerAdapter(context, termDefinitionList);
            viewPager.setAdapter(adapter);
        }
    }
}
package project.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import java.text.MessageFormat;
import java.util.List;

import project.authentication.R;
import project.model.DatabaseHelper;
import project.model.FlashcardModel;
import project.model.FlashcardPagerAdapter;

public class FlashcardOpenActivity extends AppCompatActivity {

    private Context context;
    private ImageButton previousAct;
    private ImageButton editAct;
    private FlashcardModel flashcard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_flashcard_open);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.flashcard_open_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initInstanceVariables();
        previousAct.setOnClickListener((v) -> returnToHomeActivity());
        editAct.setOnClickListener((v) -> navigateToEditActivity());
        renderFlashcardItems();
    }

    private void initInstanceVariables() {
        context = FlashcardOpenActivity.this;
        previousAct = findViewById(R.id.previous_activity);
        editAct = findViewById(R.id.edit_flashcard);
        flashcard = getIntent().getParcelableExtra("flashcard");
    }

    private void returnToHomeActivity() {
        Intent intent = new Intent(context, HomeActivity.class);
        startActivity(intent);
    }

    private void navigateToEditActivity() {
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra("flashcard", flashcard);
        startActivity(intent);
    }

    private void renderFlashcardItems() {
        // Set title
        TextView details = findViewById(R.id.details);
        details.setText(MessageFormat.format("{0} | {1} terms", flashcard.getTitle(), flashcard.getNumberOfTerms()));

        // Generate flashcard term-definition
        ViewPager2 viewPager = findViewById(R.id.flashcard_view_pager);
        try (DatabaseHelper dbHelper = new DatabaseHelper(context)) {
            List<FlashcardModel.TermDefinition> termDefinitionList = dbHelper.getFlashcardTermAndDefinition(flashcard.getFlashcardId());
            FlashcardPagerAdapter adapter = new FlashcardPagerAdapter(context, termDefinitionList);
            viewPager.setAdapter(adapter);
        }
    }
}
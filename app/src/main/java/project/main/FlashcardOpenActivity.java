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

import project.authentication.R;
import project.model.DatabaseHelper;
import project.model.FlashcardModel;
import project.model.FlashcardPagerAdapter;

public class FlashcardOpenActivity extends AppCompatActivity {

    private Context context;
    private ImageButton leftArrowBtn;
    private ImageButton editBtn;
    private TextView termCurrentIndex;
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
        leftArrowBtn.setOnClickListener((v) -> returnToHomeActivity());
        editBtn.setOnClickListener((v) -> navigateToEditActivity());
        renderFlashcardItems();
    }

    private void initInstanceVariables() {
        context = FlashcardOpenActivity.this;
        leftArrowBtn = findViewById(R.id.previous_activity);
        editBtn = findViewById(R.id.edit_flashcard);
        flashcard = getIntent().getParcelableExtra("flashcard");
        termCurrentIndex = findViewById(R.id.term_current_number);
    }

    private void returnToHomeActivity() {
        var intent = new Intent(context, HomeActivity.class);
        startActivity(intent);
    }

    private void navigateToEditActivity() {
        var intent = new Intent(context, EditActivity.class);
        intent.putExtra("flashcard", flashcard);
        startActivity(intent);
    }

    private void renderFlashcardItems() {
        // Set title and number of terms
        TextView details = findViewById(R.id.details);
        details.setText(MessageFormat.format("{0} | {1} terms", flashcard.getTitle(), flashcard.getNumberOfTerms()));

        // Fetch flashcard data from database and save it to adapter
        ViewPager2 viewPager = findViewById(R.id.flashcard_view_pager);
        try (var dbHelper = new DatabaseHelper(context)) {
            var termDefinitionList = dbHelper.getFlashcardTermAndDefinition(flashcard.getFlashcardId());
            var adapter = new FlashcardPagerAdapter(context, termDefinitionList);
            viewPager.setAdapter(adapter);
        }


        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                termCurrentIndex.setText(MessageFormat.format(
                        "Term {0} of {1}", position + 1, flashcard.getNumberOfTerms()
                ));
            }
        });
    }
}
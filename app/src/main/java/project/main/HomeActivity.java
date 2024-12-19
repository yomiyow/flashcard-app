package project.main;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ViewSwitcher;
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
import project.model.FlashcardPreviewAdapter;
import project.model.FlashcardModel;

public class HomeActivity extends AppCompatActivity {

    private Context context;
    private ImageButton addBtn;
    private FlashcardPreviewAdapter adapter;
    private RecyclerView recyclerView;
    private ViewSwitcher viewSwitcher;
    public final static int EMPTY_VIEW = 0;
    public final static int RECYCLER_VIEW = 1;

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
        recyclerView = findViewById(R.id.flashcard_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        viewSwitcher = findViewById(R.id.home_view_switcher);
    }

    private void navigateToCreateActivity() {
        Intent intent = new Intent(context, CreateActivity.class);
        startActivity(intent);
    }

    private void checkRecyclerViewContent() {
        int homeDisplay = adapter.getItemCount() == 0 ? EMPTY_VIEW : RECYCLER_VIEW;
        viewSwitcher.setDisplayedChild(homeDisplay);
    }

    private void renderFlashcardsPreview() {
        try (DatabaseHelper dbHelper = new DatabaseHelper(context)) {
            List<FlashcardModel> flashcardList = dbHelper.getFlashcardTitleAndNumberOfTerms();
            adapter = new FlashcardPreviewAdapter(context, flashcardList);
            recyclerView.setAdapter(adapter);

            // Check if the RecyclerView has content
            // and switch the view accordingly
            checkRecyclerViewContent();
        }
    }
}
package project.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import project.authentication.R;
import project.model.DatabaseHelper;
import project.model.FlashcardItemRecyclerAdapter;
import project.model.FlashcardModel;

public class CreateActivity extends AppCompatActivity {

    private Context context;
    private ImageButton previousAct;
    private ImageButton saveBtn;
    private FloatingActionButton addBtn;
    private EditText flashcardTitle;
    private int itemCount;
    RecyclerView recyclerView;

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

        initClassVariables();
        // add 1 empty item as default
        addNewEmptyFlashcardItem();
        previousAct.setOnClickListener((v) -> returnToPreviousActivity());
        addBtn.setOnClickListener((v) -> addNewEmptyFlashcardItem());
        saveBtn.setOnClickListener((v) -> saveFlashcard());
    }

    private void initClassVariables() {
        context = CreateActivity.this;
        flashcardTitle = findViewById(R.id.flashcard_title);
        previousAct = findViewById(R.id.previous_activity);
        addBtn = findViewById(R.id.add_btn);
        saveBtn = findViewById(R.id.save_flashcard);
        recyclerView = findViewById(R.id.flashcard_new_item_rv);
    }

    private void returnToPreviousActivity() {
        Intent intent = new Intent(CreateActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    // create new flashcard item using flashcard_item.xml
    private void addNewEmptyFlashcardItem() {
        itemCount++;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        FlashcardItemRecyclerAdapter adapter = new FlashcardItemRecyclerAdapter(context, itemCount);
        recyclerView.setAdapter(adapter);
    }

    private FlashcardModel collectFlashcardsData() {
        FlashcardModel flashcard = new FlashcardModel();
        List<FlashcardModel.TermDefinition> termDefinitions = new ArrayList<>();

        FlashcardItemRecyclerAdapter adapter = (FlashcardItemRecyclerAdapter) recyclerView.getAdapter();
        for (int i = 0; i < adapter.getItemCount(); i++) {
            RecyclerView.ViewHolder flashcardItem = recyclerView.findViewHolderForAdapterPosition(i);
            if (flashcardItem instanceof FlashcardItemRecyclerAdapter.FlashcardViewHolder) {
                FlashcardItemRecyclerAdapter.FlashcardViewHolder flashcardViewHolder = (FlashcardItemRecyclerAdapter.FlashcardViewHolder) flashcardItem;
                String term = flashcardViewHolder.getTermET().getText().toString();
                String definition = flashcardViewHolder.getDefinitionET().getText().toString();

                termDefinitions.add(flashcard.createTermDefinitions(term, definition));
            }
        }

        flashcard.setTitle(flashcardTitle.getText().toString());
        flashcard.setTermDefinitions(termDefinitions);

        return flashcard;
    }

    private void saveFlashcard() {
        FlashcardModel flashcardData = collectFlashcardsData();
        try (DatabaseHelper db = new DatabaseHelper(CreateActivity.this)) {
            boolean success = db.insertFlashcard(flashcardData);
            if (success) {
                Toast.makeText(this, "Flashcard saved successfully.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}